package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.rcp.PatientsAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.PatientTodayNode;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.PatientsAdminView";

    public static PatientAdministrationView currentInstance;

    private static PatientAdapter currentPatientAdapter = null;

    private List<PatientWrapper> todayPatients = new ArrayList<PatientWrapper>();

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
        // rootNode.removeAll();
        PatientWrapper patient = (PatientWrapper) searchedObject;
        SiteAdapter siteAdapter = new SiteAdapter(searchedNode, SessionManager
            .getInstance().getCurrentSite(), false);
        searchedNode.addChild(siteAdapter);
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
        // rootNode.removeAll();
        searchedNode.addChild(getNotFoundAdapter());
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

    public void reloadTodayPatients() {
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            try {
                todayPatients = PatientWrapper.getPatientsInTodayShipments(
                    SessionManager.getAppService(), SessionManager
                        .getInstance().getCurrentSite());
                for (PatientWrapper p : todayPatients) {
                    System.out.println(p);
                }
            } catch (ApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
