package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientEntryForm";
    
    public static final String MSG_NEW_PATIENT_OK =
        "Creating a new patient record.";
        
    public static final String MSG_PATIENT_OK =
        "Editing an existing patient record.";
    
    public static final String MSG_NO_PATIENT_NUMBER =
        "Patient must have a patient number";
    
    private PatientAdapter patientAdapter;
    
    private Patient patient;
    
    private Button submit;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");
        
        patientAdapter = (PatientAdapter) node;
        appService = patientAdapter.getAppService();
        patient = patientAdapter.getPatient();       
        
        if (patient.getId() == null) {
            setPartName("New Patient");
        }
        else {
            setPartName("Patient " + patient.getNumber());
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Storage Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        
        createPatientSection();
        createButtons();
    }
    
    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Patient Number", null,
            PojoObservables.observeValue(patient, "number"),
            NonEmptyString.class, MSG_NO_PATIENT_NUMBER);
    }
    
    protected void createButtons() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        submit = toolkit.createButton(client, "Submit", SWT.PUSH);
        submit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().saveEditor(PatientEntryForm.this, false);
            }
        });
    }
    
    private String getOkMessage() {
        if (patient.getId() == null) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
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
    protected void saveForm() {
        try {
            SDKQuery query;
            SDKQueryResult result;
            
            if ((patient.getId() == null) && !checkPatientNumberUnique()) {
                setDirty(true);
                return;
            }
            
            Study study = (Study) (
                (StudyAdapter) patientAdapter.getParent().getParent()).getStudy();
            patient.setStudy(study);

            if ((patient.getId() == null) || (patient.getId() == 0)) {
                query = new InsertExampleQuery(patient);   
            }
            else { 
                query = new UpdateExampleQuery(patient);   
            }
            
            result = appService.executeQuery(query);
            patient = (Patient) result.getObjectResult();
        }
        catch (final RemoteAccessException exp) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                            "Connection Attempt Failed", 
                            "Could not perform database operation. Make sure server is running correct version.");
                }
            });
        }
        catch (Exception exp) {
            exp.printStackTrace(); 
        }

        patientAdapter.getParent().performExpand();        
        getSite().getPage().closeEditor(this, false);
    }
    
    private boolean checkPatientNumberUnique() throws ApplicationException {
        WritableApplicationService appService = patientAdapter.getAppService();
        Study study = (Study) ((StudyAdapter) 
            patientAdapter.getParent().getParent()).getStudy();
        
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Patient as p "
            + "inner join fetch p.study "
            + "where p.study.id='" + study.getId() + "' "
            + "and p.number = '" + patient.getNumber() + "'");
        
        List<Object> results = appService.query(c);
        if (results.size() == 0) return true;
        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                    "Patient Number Problem", 
                    "A patient with number \"" + patient.getNumber() 
                    + "\" already exists.");
            }
        });
        return false;
    }

}
