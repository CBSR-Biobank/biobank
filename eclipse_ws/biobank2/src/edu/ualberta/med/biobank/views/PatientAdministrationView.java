package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.patient.PatientTodayNode;
import edu.ualberta.med.biobank.treeview.patient.ProcessingEventGroup;
import edu.ualberta.med.biobank.treeview.patient.StudyWithPatientAdapter;

public class PatientAdministrationView extends
    AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.PatientsAdminView";

    private static PatientAdministrationView currentInstance;

    private ProcessingEventGroup processingNode;

    public PatientAdministrationView() {
        super();
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        processingNode = new ProcessingEventGroup(rootNode, 2,
            "Processing Events");
        processingNode.setParent(rootNode);
        rootNode.addChild(processingNode);
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        PatientWrapper patient = PatientWrapper.getPatient(
            SessionManager.getAppService(), text.trim(),
            SessionManager.getUser());
        if (patient != null) {
            return Arrays.asList(patient);
        }
        return null;
    }

    public static AdapterBase addToNode(AdapterBase parentNode,
        ModelWrapper<?> wrapper) {
        if (wrapper instanceof PatientWrapper) {
            PatientWrapper patient = (PatientWrapper) wrapper;
            List<AdapterBase> res = parentNode.search(patient.getStudy());
            StudyWithPatientAdapter studyAdapter = null;
            if (res.size() > 0)
                studyAdapter = (StudyWithPatientAdapter) res.get(0);
            if (studyAdapter == null) {
                studyAdapter = new StudyWithPatientAdapter(parentNode,
                    patient.getStudy());
                studyAdapter.setEditable(false);
                studyAdapter.setLoadChildrenInBackground(false);
                parentNode.addChild(studyAdapter);
            }
            List<AdapterBase> patientAdapterList = studyAdapter.search(patient);
            PatientAdapter patientAdapter = null;
            if (patientAdapterList.size() > 0)
                patientAdapter = (PatientAdapter) patientAdapterList.get(0);
            else {
                patientAdapter = new PatientAdapter(studyAdapter, patient);
                studyAdapter.addChild(patientAdapter);
            }
            return patientAdapter;
        }
        return null;
    }

    @Override
    protected void notFound(String text) {
        boolean canCreate = SessionManager.canCreate(PatientWrapper.class);
        if (canCreate) {
            boolean create = BiobankPlugin.openConfirm("Patient not found",
                "Do you want to create this patient ?");
            if (create) {
                PatientWrapper patient = new PatientWrapper(
                    SessionManager.getAppService());
                patient.setPnumber(text);
                PatientAdapter adapter = new PatientAdapter(searchedNode,
                    patient);
                adapter.openEntryForm();
            }
        } else {
            BiobankPlugin.openInformation("Patient not found",
                "This patient doesn't exist.");
        }
    }

    @Override
    protected AbstractTodayNode<?> createTodayNode() {
        return new PatientTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode createSearchedNode() {
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

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "Enter a patient number and hit enter";
    }

    @Override
    protected String getString() {
        return toString();
    }

    public static void showPatient(PatientWrapper patient) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(patient),
                false);
        }
    }

}
