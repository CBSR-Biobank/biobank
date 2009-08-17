package edu.ualberta.med.biobank.views;

import java.util.ArrayList;

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
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientAdministrationView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.patientsAdmin";

    private Text patientNumberText;

    private AdapterTreeWidget adaptersTree;

    private AdapterBase root;

    public PatientAdministrationView() {
        root = new AdapterBase(null) {
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
                return "Root";
            }

            @Override
            public AdapterBase accept(NodeSearchVisitor visitor) {
                return null;
            }

            @Override
            public WritableApplicationService getAppService() {
                return SessionManager.getInstance().getSession()
                    .getAppService();
            }
        };
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

        adaptersTree.getTreeViewer().setInput(root);
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());
    }

    protected void searchPatient() {
        for (AdapterBase child : new ArrayList<AdapterBase>(root.getChildren())) {
            root.removeChild(child);
        }
        Patient patient = ModelUtils.getObjectWithAttr(SessionManager
            .getInstance().getSession().getAppService(), Patient.class,
            "number", String.class, patientNumberText.getText());
        PatientAdapter patientAdapter = new PatientAdapter(root, patient);
        root.addChild(patientAdapter);
        patientAdapter.performExpand();
    }

    @Override
    public void setFocus() {

    }

}
