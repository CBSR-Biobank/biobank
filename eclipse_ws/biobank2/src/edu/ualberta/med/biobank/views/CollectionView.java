package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.List;

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

public class CollectionView extends AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.CollectionView";

    private static CollectionView currentInstance;

    public CollectionView() {
        currentInstance = this;
        SessionManager.addView(this);
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

    @Override
    protected void notFound(String text) {
        boolean create = BiobankPlugin.openConfirm("Patient not found",
            "Do you want to create this patient ?");
        if (create) {
            PatientWrapper patient = new PatientWrapper(
                SessionManager.getAppService());
            patient.setPnumber(text);
            PatientAdapter adapter = new PatientAdapter(searchedNode, patient);
            adapter.openEntryForm();
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

    public static CollectionView getCurrent() {
        return currentInstance;
    }

    public static PatientAdapter getCurrentPatient() {
        AdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null && selectedNode instanceof PatientAdapter) {
            return (PatientAdapter) selectedNode;
        }
        return null;
    }

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    public static void showPatient(PatientWrapper patient) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(patient),
                false);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "Enter a patient number";
    }

    @Override
    protected String getString() {
        return toString();
    }
}
