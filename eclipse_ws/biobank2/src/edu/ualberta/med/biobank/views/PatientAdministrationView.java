package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.StructuredSelection;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.rcp.PatientsAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.patientsAdmin";

    public static PatientAdministrationView currentInstance;

    private static PatientAdapter currentPatientAdapter = null;

    public PatientAdministrationView() {
        currentInstance = this;
        SessionManager.addView(PatientsAdministrationPerspective.ID, this);
    }

    @Override
    protected void setTextEnablement(Integer siteId) {
        currentPatientAdapter = null;
        super.setTextEnablement(siteId);
    }

    @Override
    protected Object search(String text) throws Exception {
        return PatientWrapper.getPatientInSite(SessionManager.getAppService(),
            text, SessionManager.getInstance().getCurrentSite());
    }

    @Override
    protected String getNoFoundText() {
        return "- No patient found -";
    }

    @Override
    public void showInTree(Object searchedObject) {
        rootNode.removeAll();
        PatientWrapper patient = (PatientWrapper) searchedObject;
        SiteAdapter siteAdapter = new SiteAdapter(rootNode, SessionManager
            .getInstance().getCurrentSite(), false);
        rootNode.addChild(siteAdapter);
        StudyAdapter studyAdapter = new StudyAdapter(siteAdapter, patient
            .getStudy(), false);
        siteAdapter.addChild(studyAdapter);
        PatientAdapter patientAdapter = new PatientAdapter(studyAdapter,
            patient);
        currentPatientAdapter = patientAdapter;
        studyAdapter.addChild(patientAdapter);
        patientAdapter.performExpand();
        getSite().getSelectionProvider().setSelection(
            new StructuredSelection(currentPatientAdapter));
    }

    @Override
    protected void notFound(String text) {
        currentPatientAdapter = null;
        rootNode.removeAll();
        rootNode.addChild(getNotFoundAdapter());
        boolean create = BioBankPlugin.openConfirm("Patient not found",
            "Do you want to create this patient ?");
        if (create) {
            PatientWrapper patient = new PatientWrapper(SessionManager
                .getAppService());
            patient.setPnumber(text);
            PatientAdapter adapter = new PatientAdapter(rootNode, patient);
            AdapterBase.openForm(new FormInput(adapter), PatientEntryForm.ID);
        }
    }

    public static RootNode getRootNode() {
        return currentInstance.rootNode;
    }

    public static PatientAdapter getCurrentPatientAdapter() {
        return currentPatientAdapter;
    }

}
