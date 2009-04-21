package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientViewForm";
    
    private PatientAdapter patientAdapter;
    
    private Patient patient;
    
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
        form.setText("Patient: " + patient.getNumber());    
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        createPatientSection();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  
        
        createBoundWidget(client, Text.class, SWT.NONE, "Patient Number",
            PojoObservables.observeValue(patient, "number"));
        
    }
}
