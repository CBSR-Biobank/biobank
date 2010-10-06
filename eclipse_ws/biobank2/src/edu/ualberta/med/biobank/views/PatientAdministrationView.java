package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.rcp.perspective.PatientsAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.patient.PatientTodayNode;
import edu.ualberta.med.biobank.treeview.patient.StudyWithPatientAdapter;

public class PatientAdministrationView extends
    AbstractTodaySearchAdministrationView {

    public static final String ID =
        "edu.ualberta.med.biobank.views.PatientsAdminView";

    private static PatientAdministrationView currentInstance;

    public PatientAdministrationView() {
        currentInstance = this;
        SessionManager.addView(PatientsAdministrationPerspective.ID, this);
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        PatientWrapper patient =
            PatientWrapper.getPatient(SessionManager.getAppService(),
                text.trim());
        if (patient != null) {
            return Arrays.asList(patient);
        }
        return null;
    }

    @Override
    public AdapterBase addToNode(AdapterBase parentNode, ModelWrapper<?> wrapper) {
        if (wrapper instanceof PatientWrapper) {
            PatientWrapper patient = (PatientWrapper) wrapper;
            StudyWithPatientAdapter studyAdapter =
                (StudyWithPatientAdapter) parentNode.search(patient.getStudy());
            if (studyAdapter == null) {
                studyAdapter =
                    new StudyWithPatientAdapter(parentNode, patient.getStudy());
                studyAdapter.setEditable(false);
                studyAdapter.setLoadChildrenInBackground(false);
                parentNode.addChild(studyAdapter);
            }
            PatientAdapter patientAdapter =
                (PatientAdapter) studyAdapter.search(patient);
            if (patientAdapter == null) {
                patientAdapter = new PatientAdapter(studyAdapter, patient);
                studyAdapter.addChild(patientAdapter);
            }
            return patientAdapter;
        }
        return null;
    }

    @Override
    protected void notFound(String text) {
        boolean create =
            BioBankPlugin.openConfirm("Patient not found",
                "Do you want to create this patient ?");
        if (create) {
            PatientWrapper patient =
                new PatientWrapper(SessionManager.getAppService());
            patient.setPnumber(text);
            openNewPatientForm(patient);
        }
    }

    public void openNewPatientForm(final PatientWrapper patient) {
        patient.addWrapperListener(new WrapperListenerAdapter() {
            @Override
            public void inserted(WrapperEvent event) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        showSearchedObjectsInTree(Arrays.asList(patient), true);
                    }
                });
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

    public static PatientAdministrationView getCurrent() {
        return currentInstance;
    }

}
