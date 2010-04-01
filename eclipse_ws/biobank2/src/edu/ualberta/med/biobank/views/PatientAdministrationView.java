package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.StructuredSelection;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.rcp.PatientsAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.PatientTodayNode;
import edu.ualberta.med.biobank.treeview.PatientViewNodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.PatientsAdminView";

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
    public void showInTree(Object searchedObject, boolean today) {
        PatientWrapper patient = (PatientWrapper) searchedObject;
        AdapterBase topParent = searchedNode;
        if (today) {
            topParent = todayNode;
        }
        StudyAdapter studyAdapter = (StudyAdapter) topParent
            .accept(new PatientViewNodeSearchVisitor(patient.getStudy()));
        if (studyAdapter == null) {
            studyAdapter = new StudyAdapter(topParent, patient.getStudy(),
                false);
            topParent.addChild(studyAdapter);
        }
        PatientAdapter patientAdapter = (PatientAdapter) studyAdapter
            .accept(new PatientViewNodeSearchVisitor(patient));
        if (patientAdapter == null) {
            patientAdapter = new PatientAdapter(studyAdapter, patient);
            studyAdapter.addChild(patientAdapter);
        }
        currentPatientAdapter = patientAdapter;
        // patientAdapter.performExpand();
        getSite().getSelectionProvider().setSelection(
            new StructuredSelection(currentPatientAdapter));
    }

    @Override
    protected void notFound(String text) {
        currentPatientAdapter = null;
        // rootNode.removeAll();
        // searchedNode.addChild(getNotFoundAdapter());
        boolean create = BioBankPlugin.openConfirm("Patient not found",
            "Do you want to create this patient ?");
        if (create) {
            PatientWrapper patient = new PatientWrapper(SessionManager
                .getAppService());
            patient.setPnumber(text);
            PatientAdapter adapter = new PatientAdapter(searchedNode, patient);
            adapter.openEntryForm();
        }
    }

    public static RootNode getRootNode() {
        return currentInstance.rootNode;
    }

    public static PatientAdapter getCurrentPatientAdapter() {
        return currentPatientAdapter;
    }

    @Override
    protected AbstractTodayNode getTodayNode() {
        return new PatientTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode getSearchedNode() {
        return new PatientSearchedNode(rootNode, 1);
    }

}
