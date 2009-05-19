package edu.ualberta.med.biobank.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.beans.PojoObservables;
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
import org.eclipse.swt.widgets.Text;
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
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class PatientVisitEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK =
        "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK =
        "Editing an existing patient visit record.";
    
    public static final String MSG_NO_VISIT_NUMBER =
        "Visit must have a number";
    
    public static final String DATE_FORMAT = 
        "yyyy-MM-dd";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

    private Study study;

    private ListOrderedMap pvInfoMap;

    private Button submit;

    public PatientVisitEntryForm() {
        super();
        pvInfoMap = new ListOrderedMap();
    }
    
    class PatientVisitInfo {
        Sdata sdata;
        PatientVisitData pvData;
        
        public PatientVisitInfo() {
            sdata = null;
            pvData = null;
        }
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        patientVisitAdapter = (PatientVisitAdapter) node;
        patientVisit = patientVisitAdapter.getPatientVisit();
        appService = patientVisitAdapter.getAppService();

        if (patientVisit.getId() == null) {
            setPartName("New Patient Visit");
        }
        else {
            setPartName("Patient Visit " + patientVisit.getNumber());
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

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Visit Number", null,
            PojoObservables.observeValue(patientVisit, "number"),
            NonEmptyString.class, MSG_NO_VISIT_NUMBER);

        study = ((StudyAdapter) patientVisitAdapter.getParent().getParent().getParent()).getStudy();

        for (Sdata sdata : study.getSdataCollection()) {
            PatientVisitInfo pvInfo = new PatientVisitInfo();
            pvInfo.sdata = sdata;
            pvInfoMap.put(sdata.getSdataType().getType(), pvInfo);
        }

        Collection<PatientVisitData> pvDataCollection =
            patientVisit.getPatientVisitDataCollection();
        if (pvDataCollection != null) {
            for (PatientVisitData pvData : pvDataCollection) {
                String key = pvData.getSdata().getSdataType().getType();
                PatientVisitInfo pvInfo = (PatientVisitInfo) pvInfoMap.get(key);
                pvInfo.pvData = pvData;
                
                System.out.println("--- id: " + pvData.getId() + ", value: " 
                    + pvData.getValue() + ", pv_id: " 
                    + pvData.getPatientVisit().getId());
                
                // pvData.getId()
                // pvData.getValue()
                // pvDataCollection.size()
            }
        }

        Control control;
        MapIterator it = pvInfoMap.mapIterator();
        while (it.hasNext()) {
            control = null;
            String label = (String) it.next();
            PatientVisitInfo pvInfo = (PatientVisitInfo) it.getValue();
            String value = null;
            int typeId = pvInfo.sdata.getSdataType().getId();
            
            if (pvInfo.pvData != null) {
                value = pvInfo.pvData.getValue();
            }
            
            Label labelWidget = toolkit.createLabel(client, label + ":", SWT.LEFT);
            labelWidget.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

            switch (typeId) {
                case 1: // Date Drawn
                case 2: // Date Received
                case 3: // Date Processed
                case 4: // Shipped Date
                    control = createDatePickerSection(client, value);
                    break;
                    
                case 5: // Aliquot Volume
                case 6: // Blood Received
                case 7: // Visit
                    control = createComboSection(client, 
                        pvInfo.sdata.getValue().split(";"), 
                        value);
                    break;
                    
                case  8: // WBC Count
                case  9: // Time Arrived
                case  10: // Biopsy Length
                    control = toolkit.createText(client, value, 
                        SWT.LEFT);
                    break;
                    
                case 11: // Comments
                    control = toolkit.createText(client, value, 
                        SWT.LEFT | SWT.MULTI);
                    break;
                    
                default:
                    Assert.isTrue(false, "Invalid sdata type: " + typeId);
            }
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            if (typeId == 11) {
                gd.heightHint = 40;
            }
            control.setLayoutData(gd);
            controls.put(label, control);
        }
    }

    private Control createDatePickerSection(Composite client, String value) {        
        DatePickerCombo datePicker = new DatePickerCombo(client, SWT.BORDER,
            DatePickerStyle.BUTTONS_ON_BOTTOM | DatePickerStyle.YEAR_BUTTONS
            | DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
        datePicker.setLayout(new GridLayout(1, false));
        datePicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        datePicker.setDateFormat(new SimpleDateFormat(DATE_FORMAT));

        if ((value != null) && (value.length() > 0)) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            try {
                datePicker.setDate(df.parse(value));
            }
            catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return datePicker;
    }
    
    private Control createComboSection(Composite client, String [] values, 
        String selected) {
        
        Combo combo = new Combo(client, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        combo.setItems(values);
        
        if (selected != null) {
            int count = 0;       
            for (String value : values) {
                if (selected.equals(value)) {
                    combo.select(count);
                    break;
                }
                ++count;
            }
        }
        
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
            @Override
			public void widgetSelected(SelectionEvent e) {
                doSaveInternal();
            }
        });
    }

    private String getOkMessage() {
        if (patientVisit.getId() == null) {
            return MSG_NEW_PATIENT_VISIT_OK;
        }
        return MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {   
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            submit.setEnabled(true);
        }
        else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            submit.setEnabled(false);
        }          
    }

    @Override
    protected void saveForm() throws Exception {
        SDKQuery query;
        SDKQueryResult result;

        PatientAdapter patientAdapter = 
            (PatientAdapter) patientVisitAdapter.getParent();

        System.out.println("*** patient visit id: " + patientVisit.getId()); 

        if (patientVisit.getPatientVisitDataCollection() != null) {
            for (PatientVisitData pvData : 
                patientVisit.getPatientVisitDataCollection()) {
                System.out.println("*** id: " + pvData.getId() + ", value: " 
                    + pvData.getValue() + ", pv_id: " 
                    + pvData.getPatientVisit().getId());
            }
        }

        patientVisit.setPatient(patientAdapter.getPatient());
        savePatientVisitData();

        for (PatientVisitData pvData : 
            patientVisit.getPatientVisitDataCollection()) {
            System.out.println("id: " + pvData.getId() + ", value: " 
                + pvData.getValue() + ", pv_id: " 
                + pvData.getPatientVisit().getId());
        }            

        System.out.println("pv data size: " + patientVisit.getPatientVisitDataCollection().size());

        if ((patientVisit.getId() == null) || (patientVisit.getId() == 0)) {
            query = new InsertExampleQuery(patientVisit);
        }
        else { 
            query = new UpdateExampleQuery(patientVisit);
        }

        result = appService.executeQuery(query);
        patientVisit = (PatientVisit) result.getObjectResult();   

        patientAdapter.performExpand();       
        getSite().getPage().closeEditor(this, false);  
    }
    
    private void savePatientVisitData() {
        boolean newCollection = false;
        Collection<PatientVisitData> pvDataCollection;
        
        pvDataCollection = patientVisit.getPatientVisitDataCollection();
        if (pvDataCollection == null) {
            pvDataCollection = new HashSet<PatientVisitData>();
            newCollection = true;
        }
        
        for (String key : controls.keySet()) {   
            PatientVisitInfo pvInfo = (PatientVisitInfo) pvInfoMap.get(key);         
            Control control = controls.get(key);
            String value = "";
            
            if (control instanceof Text) {
                value = ((Text) control).getText();
                System.out.println(key + ": " + ((Text) control).getText());
            }
            else if (control instanceof Combo) {
                String [] options = pvInfo.sdata.getValue().split(";");
                int index = ((Combo) control).getSelectionIndex();
                if (index >= 0) {
                    Assert.isTrue(index < options.length,
                        "Invalid combo box selection " + index);
                    value = options[index];
                    System.out.println(key + ": " + options[index]);
                }
            }
            else if (control instanceof DatePickerCombo) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Date date = ((DatePickerCombo) control).getDate();
                if (date != null) {
                    System.out.println(key + ": " +  sdf.format(date));
                    value = sdf.format(date);
                }
            }

            PatientVisitData pvData = pvInfo.pvData;

            if (pvInfo.pvData == null) {
                pvData = new PatientVisitData();
                pvData.setSdata(pvInfo.sdata);
                pvData.setPatientVisit(patientVisit);    
            }
            pvData.setValue(value);     
            
            // pvData.getId()
            
            if (pvInfo.pvData == null) {   
                pvDataCollection.add(pvData);
            }
        }
        
        if (newCollection) {
            patientVisit.setPatientVisitDataCollection(pvDataCollection);
        }
    }
}
