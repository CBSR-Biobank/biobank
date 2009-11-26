package edu.ualberta.med.biobank.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientAdministrationView extends AbstractAdministrationView {

    private static Logger LOGGER = Logger
        .getLogger(PatientAdministrationView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.patientsAdmin";

    public static PatientAdministrationView currentInstance;

    private static PatientAdapter currentPatientAdapter = null;

    public PatientAdministrationView() {
        currentInstance = this;
    }

    @Override
    protected void setTextEnablement(Integer siteId) {
        currentPatientAdapter = null;
        super.setTextEnablement(siteId);
    }

    @Override
    protected Object search(String text) throws Exception {
        return PatientWrapper.getPatientInSite(SessionManager.getAppService(),
            text, SessionManager.getInstance().getCurrentSiteWrapper());
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
            .getInstance().getCurrentSiteWrapper(), false);
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
            patient.setNumber(text);
            PatientAdapter adapter = new PatientAdapter(rootNode, patient);
            try {
                BioBankPlugin.getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage().openEditor(
                        new FormInput(adapter), PatientEntryForm.ID, true);
            } catch (PartInitException e) {
                String msg = "Wasn't able to open the form";
                BioBankPlugin.openError("Patient Form", msg);
                LOGGER.error(msg, e);
            }
        }
    }

    public static RootNode getRootNode() {
        return currentInstance.rootNode;
    }

    public static PatientAdapter getCurrentPatientAdapter() {
        return currentPatientAdapter;
    }

    public static void setSelectedNode(AdapterBase node) {
        currentInstance.selectNode(node);
    }

}
