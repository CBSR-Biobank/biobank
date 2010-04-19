package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.rcp.PatientsAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.PatientTodayNode;
import edu.ualberta.med.biobank.treeview.PatientViewNodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.PatientsAdminView";

    private static PatientAdministrationView currentInstance;

    public PatientAdministrationView() {
        currentInstance = this;
        SessionManager.addView(PatientsAdministrationPerspective.ID, this);
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        PatientWrapper patient = PatientWrapper.getPatientInSite(SessionManager
            .getAppService(), text, SessionManager.getInstance()
            .getCurrentSite());
        if (patient != null) {
            return Arrays.asList(patient);
        }
        return null;
    }

    @Override
    public AdapterBase addToNode(AdapterBase parentNode, ModelWrapper<?> wrapper) {
        if (wrapper instanceof PatientWrapper) {
            PatientWrapper patient = (PatientWrapper) wrapper;
            StudyAdapter studyAdapter = (StudyAdapter) parentNode
                .accept(new PatientViewNodeSearchVisitor(patient.getStudy()));
            if (studyAdapter == null) {
                studyAdapter = new StudyAdapter(parentNode, patient.getStudy());
                studyAdapter.setEditable(false);
                studyAdapter.setLoadChildrenInBackground(false);
                parentNode.addChild(studyAdapter);
            }
            PatientAdapter patientAdapter = (PatientAdapter) studyAdapter
                .accept(new PatientViewNodeSearchVisitor(patient));
            if (patientAdapter == null) {
                patientAdapter = new PatientAdapter(studyAdapter, patient);
                studyAdapter.addChild(patientAdapter);
            }
            return patientAdapter;
        }
        return null;
    }

    @Override
    protected PatientViewNodeSearchVisitor getVisitor(
        ModelWrapper<?> searchedObject) {
        return new PatientViewNodeSearchVisitor(searchedObject);
    }

    @Override
    protected void notFound(String text) {
        boolean create = BioBankPlugin.openConfirm("Patient not found",
            "Do you want to create this patient ?");
        if (create) {
            PatientWrapper patient = new PatientWrapper(SessionManager
                .getAppService());
            patient.setPnumber(text);
            openNewPatientForm(patient);
        }
    }

    public void openNewPatientForm(final PatientWrapper patient) {
        patient.addWrapperListener(new WrapperListenerAdapter() {
            @Override
            public void inserted(WrapperEvent event) {
                showSearchedObjectsInTree(Arrays.asList(patient));
            }
        });
        PatientAdapter adapter = new PatientAdapter(searchedNode, patient);
        adapter.openEntryForm();
    }

    @Override
    protected AbstractTodayNode getTodayNode() {
        return new PatientTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode getSearchedNode() {
        return new PatientSearchedNode(rootNode, 1);
    }

    public static PatientAdapter getCurrentPatient() {
        AdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null && selectedNode instanceof PatientAdapter) {
            return (PatientAdapter) selectedNode;
        }
        return null;
    }

    public static void showPatient(PatientWrapper patient) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(patient));
        }
    }

    public static PatientAdministrationView getCurrent() {
        return currentInstance;
    }
}
