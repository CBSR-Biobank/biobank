package edu.ualberta.med.biobank.forms;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.gface.date.DatePickerCombo;
import com.gface.date.DatePickerStyle;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PatientVisitData;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientVisitEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String NEW_PATIENT_VISIT_OK_MESSAGE =
        "Creating a new patient visit record.";

    public static final String PATIENT_VISIT_OK_MESSAGE =
        "Editing an existing patient visit record.";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

    private Study study;

    private ListOrderedMap pvInitialValuesMap;

    private Button submit;

    public PatientVisitEntryForm() {
        super();
        pvInitialValuesMap = new ListOrderedMap();
    }
    
    class PvInitialValue {
        String label;
        String options;
        String value;
        
        public PvInitialValue(String label) {
            this.label = label;
            this.value = new String();
        }
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        patientVisitAdapter = (PatientVisitAdapter) node;
        appService = patientVisitAdapter.getAppService();
        patientVisit = patientVisitAdapter.getPatientVisit();

        if (patientVisit.getId() == null) {
            setPartName("New Patient Visit");
        }
        else {
            setPartName("Patient Visit" + patientVisit.getId());
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createPvSection();
        createButtonsSection();
    }

    private void createPvSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        study = (Study) ((StudyAdapter)
            patientVisitAdapter.getParent().getParent().getParent()).getStudy();

        for (Sdata sdata : study.getSdataCollection()) {
            PvInitialValue pvInitialValue = new PvInitialValue(sdata.getSdataType().getType());
            pvInitialValue.options = sdata.getValue();
            pvInitialValuesMap.put(sdata.getSdataType().getId(), pvInitialValue);
        }

        Collection<PatientVisitData> pvDataCollection =
            patientVisit.getPatientVisitDataCollection();
        if (pvDataCollection != null) {
            for (PatientVisitData pvData : pvDataCollection) {
                PvInitialValue pvInitialValue = (PvInitialValue) 
                    pvInitialValuesMap.get(pvData.getSdataType().getId());
                pvInitialValue.value = pvData.getValue();
            }
        }

        Control control;
        MapIterator it = pvInitialValuesMap.mapIterator();
        while (it.hasNext()) {
            control = null;
            Integer key = (Integer) it.next();
            PvInitialValue pvInitialValue = (PvInitialValue) it.getValue();
            Label label = toolkit.createLabel(client, pvInitialValue.label + ":", 
                SWT.LEFT);
            label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

            switch (key) {
                case  1: // Date Drawn
                case  2: // Date Received
                case  3: // Date Processed
                case 11: // Shipped Date
                    control = createDatePickerSection(client, pvInitialValue.value);
                    break;
                    
                case  5: // Aliquot Volume
                case  6: // Blood Received
                case 10: // Visit
                    control = createComboSection(client, pvInitialValue.options, 
                        pvInitialValue.value);
                    break;
                    
                case  4: // Comments
                    control = toolkit.createText(client, pvInitialValue.value, 
                        SWT.LEFT | SWT.MULTI);
                    break;
                    
                case  7: // WBC Count
                case  8: // Time Arrived
                case  9: // Biopsy Length
                    control = toolkit.createText(client, pvInitialValue.value, 
                        SWT.LEFT);
                    break;
                    
                default:
                    Assert.isTrue(false, "Invalid sdata type: " + key);
            }
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            if (key == 4) {
                gd.heightHint = 40;
            }
            control.setLayoutData(gd);
            controls.put(pvInitialValue.label, control);
        }
    }

    private Control createDatePickerSection(Composite client, String value) {        
        DatePickerCombo datePicker = new DatePickerCombo(client, SWT.BORDER,
            DatePickerStyle.BUTTONS_ON_BOTTOM | DatePickerStyle.YEAR_BUTTONS
            | DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
        datePicker.setLayout(new GridLayout(1, false));
        datePicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (value.length() > 0) {
            DateFormat df = DateFormat.getDateInstance();
            try {
                datePicker.setDate(df.parse(value));
            }
            catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return datePicker;
    }
    
    private Control createComboSection(Composite client, String values, 
        String selected) {
        
        String [] options = values.split(";");
        int count = 0, index = 0;
        for (String option : options) {
            if (selected.equals(option)) {
                index = count;
                break;
            }
            ++count;
        }
        
        Combo combo = new Combo(client, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        combo.setItems(options);
        combo.select(index);
        
        toolkit.adapt(combo, true, true);
        
        return combo;
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        submit = toolkit.createButton(client, "Submit", SWT.PUSH);
        submit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                doSaveInternal();
            }
        });
    }

    private String getOkMessage() {
        if (patientVisit.getId() == null) {
            return NEW_PATIENT_VISIT_OK_MESSAGE;
        }
        return PATIENT_VISIT_OK_MESSAGE;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
    }

    @Override
    protected void saveForm() {

    }
}
