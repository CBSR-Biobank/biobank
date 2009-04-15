package edu.ualberta.med.biobank.forms;

import java.text.DateFormat;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.gface.date.DatePicker;
import com.gface.date.DateSelectedEvent;
import com.gface.date.DateSelectionListener;

import edu.ualberta.med.biobank.dialogs.DatePickerDlg;
import edu.ualberta.med.biobank.dialogs.ListAddDialog;
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

    private ListOrderedMap pvDataMap;

    private Button submit;

    public PatientVisitEntryForm() {
        super();
        pvDataMap = new ListOrderedMap();
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
            pvDataMap.put(sdata.getSdataType().getType(), "");
        }

        Collection<PatientVisitData> pvDataCollection =
            patientVisit.getPatientVisitDataCollection();
        if (pvDataCollection != null) {
            for (PatientVisitData pvData : pvDataCollection) {
                pvDataMap.put(pvData.getSdataType().getType(), pvData.getValue());
            }
        }

        MapIterator it = pvDataMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) it.getValue();

            toolkit.createLabel(client, key + ":", SWT.LEFT);

            if (key.equals("Date Drawn")) {
                createDatePickerSection(client, key, "select date", value);
            }
            else if (key.equals("Date Received")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Date Processed")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Comments")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Aliquot Volume")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Blood Received")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("WBC Count")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Time Arrived")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Biopsy Length")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Visit")) {
                toolkit.createText(client, value, SWT.NONE);
            }
            else if (key.equals("Shipped Date")) {
                toolkit.createText(client, value, SWT.NONE);
            }
        }
    }
    
    private void createDatePickerSection(Composite client, final String title, 
        final String prompt, String value) {
        Composite dateArea = toolkit.createComposite(client);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        dateArea.setLayout(layout);
        dateArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        toolkit.createText(dateArea, value, SWT.NONE);
        
        Button btn = toolkit.createButton(dateArea, "Pick Date", SWT.PUSH);
        btn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {                
                DatePickerDlg dlg = new DatePickerDlg(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                    title, prompt);
                dlg.open();
            }
        });
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
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveForm() {
        // TODO Auto-generated method stub

    }
}
