package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.SearchPatientAction;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientSearchedNode;
import edu.ualberta.med.biobank.treeview.patient.StudyWithPatientAdapter;

public class CollectionView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.CollectionView"; //$NON-NLS-1$

    private static CollectionView currentInstance;

    private PatientSearchedNode searchedNode;

    private Button radioPnumber;

    public CollectionView() {
        super();
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        createNodes();
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioPnumber = new Button(composite, SWT.RADIO);
        radioPnumber.setText(Messages.CollectionView_patient_label);
        radioPnumber.setSelection(true);
    }

    protected void notFound(String text) {
        boolean create = BgcPlugin.openConfirm(
            Messages.CollectionView_patient_error_title,
            Messages.CollectionView_patient_error_msg);
        if (create) {
            Patient patient = new Patient();
            patient.setPnumber(text);
            AbstractAdapterBase adapter = new PatientAdapter(null, null);
            adapter.openEntryForm();
        }
    }

    protected PatientSearchedNode createSearchedNode() {
        if (searchedNode == null)
            return new PatientSearchedNode(rootNode, 0);
        else
            return searchedNode;
    }

    public static CollectionView getCurrent() {
        return currentInstance;
    }

    public static PatientAdapter getCurrentPatient() {
        AbstractAdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null && selectedNode instanceof PatientAdapter) {
            return (PatientAdapter) selectedNode;
        }
        return null;
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
        return Messages.CollectionView_patient_tooltip;
    }

    public void showSearchedObjectsInTree(PatientInfo pinfo, boolean doubleClick) {
        List<AbstractAdapterBase> nodeRes = rootNode.search(pinfo);
        if (nodeRes.size() == 0) {
            searchedNode.addSearchObject(pinfo);
            searchedNode.performExpand();
            nodeRes = searchedNode.search(pinfo);
        }
        if (nodeRes.size() > 0) {
            if (doubleClick) {
                nodeRes.get(0).performDoubleClick();
            }
        }
    }

    public static AbstractAdapterBase addToNode(AdapterBase parentNode,
        Object obj) {
        if (obj instanceof PatientInfo) {
            PatientInfo pinfo = (PatientInfo) obj;
            List<AbstractAdapterBase> res = parentNode.search(pinfo.patient
                .getStudy());
            StudyWithPatientAdapter studyAdapter = null;
            if (res.size() > 0)
                studyAdapter = (StudyWithPatientAdapter) res.get(0);
            if (studyAdapter == null) {
                studyAdapter = new StudyWithPatientAdapter(parentNode,
                    new StudyWrapper(SessionManager.getAppService(),
                        pinfo.patient.getStudy()));
                studyAdapter.setEditable(false);
                studyAdapter.setLoadChildrenInBackground(false);
                parentNode.addChild(studyAdapter);
            }
            List<AbstractAdapterBase> patientAdapterList = studyAdapter
                .search(pinfo);
            PatientAdapter patientAdapter = null;
            if (patientAdapterList.size() > 0)
                patientAdapter = (PatientAdapter) patientAdapterList.get(0);
            else {
                patientAdapter = new PatientAdapter(studyAdapter, pinfo);
                studyAdapter.addChild(patientAdapter);
            }
            return patientAdapter;
        }
        return null;
    }

    @Override
    protected void internalSearch() {
        String text = treeText.getText();

        if (text.trim().isEmpty()) {
            return;
        }

        try {
            PatientInfo pinfo = SessionManager.getAppService().doAction(
                new SearchPatientAction(text.trim()));
            if (pinfo == null) {
                notFound(text);
            } else {
                showSearchedObjectsInTree(pinfo, true);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.CollectionView_search_error_msg,
                e);
        }
    }

    private void createNodes() {
        searchedNode = createSearchedNode();
        rootNode.addChild(searchedNode);
        searchedNode.setParent(rootNode);
    }

    public AdapterBase getSearchedNode() {
        return searchedNode;
    }

    @Override
    public void reload() {
        for (AbstractAdapterBase adapter : rootNode.getChildren())
            adapter.rebuild();
        super.reload();
    }

    @Override
    public void clear() {
        searchedNode.clear();
        setSearchFieldsEnablement(false);
    }

}
