package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientViewForm";
    
    private PatientAdapter patientAdapter;
    
    private Patient patient;

	//private Label patientNumberLabel;
    
    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");
        
        if (node instanceof PatientAdapter) {
        	patientAdapter = (PatientAdapter) node;
        	appService = patientAdapter.getAppService();        	
        	retrievePatient();
        	setPartName("Patient " + patient.getNumber());
        } else {
        	Assert.isTrue(false, "Invalid editor input: object of type "
        			+ node.getClass().getName());
        }
    }

	private void retrievePatient() {
		List<Patient> result;
		Patient searchPatient = new Patient();
		searchPatient.setId(patientAdapter.getPatient().getId());
		try {
			result = appService.search(Patient.class, searchPatient);
			Assert.isTrue(result.size() == 1);
			patient = result.get(0);
			patientAdapter.setPatient(patient);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

    @Override
    protected void createFormContent() {
        form.setText("Patient: " + patient.getNumber());    
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        addRefreshToolbarAction();        
        //createPatientSection();
    }

//    private void createPatientSection() {
//        Composite client = toolkit.createComposite(form.getBody());
//        GridLayout layout = new GridLayout(2, false);
//        layout.horizontalSpacing = 10;
//        client.setLayout(layout);
//        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        toolkit.paintBordersFor(client);  
//        
//        patientNumberLabel = (Label)createWidget(client, Text.class, SWT.NONE, "Patient Number");
//        FormUtils.setTextValue(patientNumberLabel, patient.getNumber());    
//    }

	@Override
	protected void reload() {
		retrievePatient();		
		setPartName("Patient " + patient.getNumber());
		form.setText("Patient: " + patient.getNumber());  	
		//FormUtils.setTextValue(patientNumberLabel, patient.getNumber());
	}
}
