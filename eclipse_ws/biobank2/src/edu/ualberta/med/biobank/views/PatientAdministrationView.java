package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientAdministrationView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.patientsAdmin";

    private Text patientNumberText;

    private AdapterTreeWidget adaptersTree;

    private static RootNode rootNode;

    private static AdapterBase noPatientFoundAdapter;

    public static PatientAdministrationView currentInstance;

    public PatientAdministrationView() {
        currentInstance = this;
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        patientNumberText = new Text(parent, SWT.SINGLE);
        Listener searchListener = new Listener() {
            public void handleEvent(Event e) {
                searchPatient();
            }
        };
        patientNumberText.addListener(SWT.DefaultSelection, searchListener);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        patientNumberText.setLayoutData(gd);

        adaptersTree = new AdapterTreeWidget(parent);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        adaptersTree.setLayoutData(gd);

        getRootNode().setTreeViewer(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().setInput(getRootNode());
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().expandAll();
    }

    protected void searchPatient() {
        getSite().getPage().closeAllEditors(true);
        String number = patientNumberText.getText();
        try {
            PatientWrapper patientWrapper = PatientWrapper
                .getPatientWrapperInSite(SessionManager.getAppService(),
                    number, SessionManager.getInstance().getCurrentSite());
            if (patientWrapper == null) {
                notFoundPatient(number);
            } else {
                showPatientInTree(patientWrapper);
            }
        } catch (ApplicationException ae) {
            BioBankPlugin.openError("Search error", ae);
            notFoundPatient(number);
        }
    }

    public void showPatientInTree(PatientWrapper patientWrapper) {
        getRootNode().removeAll();
        SiteAdapter siteAdapter = new SiteAdapter(getRootNode(), patientWrapper
            .getStudy().getSite(), false);
        getRootNode().addChild(siteAdapter);
        StudyAdapter studyAdapter = new StudyAdapter(siteAdapter,
            patientWrapper.getStudy(), false);
        siteAdapter.addChild(studyAdapter);
        PatientAdapter patientAdapter = new PatientAdapter(studyAdapter,
            patientWrapper);
        studyAdapter.addChild(patientAdapter);
        patientAdapter.performExpand();
    }

    private void notFoundPatient(String number) {
        getRootNode().removeAll();
        getRootNode().addChild(getNoPatientFoundAdapter());
        boolean create = BioBankPlugin.openConfirm("Patient not found",
            "Do you want to create this patient ?");
        if (create) {
            Patient patient = new Patient();
            patient.setNumber(number);
            PatientAdapter adapter = new PatientAdapter(getRootNode(), patient);
            try {
                BioBankPlugin.getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage().openEditor(
                        new FormInput(adapter), PatientEntryForm.ID, true);
            } catch (PartInitException e) {
                String msg = "Wasn't able to open the form";
                BioBankPlugin.openError("Patient Form", msg);
                SessionManager.getLogger().error(msg, e);
            }
        }
    }

    @Override
    public void setFocus() {

    }

    public TreeViewer getTreeViewer() {
        return adaptersTree.getTreeViewer();
    }

    public static RootNode getRootNode() {
        if (rootNode == null) {
            rootNode = new RootNode();
        }
        return rootNode;
    }

    private static AdapterBase getNoPatientFoundAdapter() {
        if (noPatientFoundAdapter == null) {
            noPatientFoundAdapter = new AdapterBase(getRootNode(), null, null,
                0, "- No patient found -") {
                @Override
                public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
                }

                @Override
                public void performDoubleClick() {
                }

                @Override
                public void loadChildren(boolean updateNode) {
                }

                @Override
                public String getTitle() {
                    return null;
                }

                @Override
                public AdapterBase accept(NodeSearchVisitor visitor) {
                    return null;
                }

                @Override
                protected Integer getModelObjectId() {
                    return null;
                }

                @Override
                protected boolean integrityCheck() {
                    return true;
                }
            };
        }
        return noPatientFoundAdapter;
    }
}
