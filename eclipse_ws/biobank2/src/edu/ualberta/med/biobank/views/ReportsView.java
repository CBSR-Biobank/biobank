package edu.ualberta.med.biobank.views;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.client.reports.AbstractReport;
import edu.ualberta.med.biobank.client.reports.ReportTreeNode;
import edu.ualberta.med.biobank.client.reports.advanced.HQLField;
import edu.ualberta.med.biobank.client.reports.advanced.QueryTreeNode;
import edu.ualberta.med.biobank.client.reports.advanced.SearchUtils;
import edu.ualberta.med.biobank.forms.AdvancedReportsEditor;
import edu.ualberta.med.biobank.forms.ReportsEditor;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.rcp.ReportsPerspective;
import edu.ualberta.med.biobank.treeview.QueryTree;

public class ReportsView extends AbstractViewWithTree {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";
    public static ReportsView reportsView;

    private Composite top;

    private TreeViewer querySelect;

    @Override
    public TreeViewer getTreeViewer() {
        return getTree();
    }

    public ReportsView() {
        SessionManager.addView(ReportsPerspective.ID, this);
        reportsView = this;
    }

    public static TreeViewer getTree() {
        // retrieves the report tree
        return reportsView.querySelect;
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.BORDER);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        querySelect = new TreeViewer(top, SWT.BORDER);
        querySelect.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                ReportTreeNode node = (ReportTreeNode) ((IStructuredSelection) event
                    .getSelection()).getFirstElement();
                try {
                    if (node.getQuery() != null) {
                        if (node.getQuery() instanceof QueryTreeNode)
                            PlatformUI
                                .getWorkbench()
                                .getActiveWorkbenchWindow()
                                .getActivePage()
                                .openEditor(new ReportInput(node),
                                    AdvancedReportsEditor.ID);
                        else
                            PlatformUI
                                .getWorkbench()
                                .getActiveWorkbenchWindow()
                                .getActivePage()
                                .openEditor(new ReportInput(node),
                                    ReportsEditor.ID);
                    }
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError("Error",
                        "There was an error while building page.");
                }
            }
        });

        querySelect.setContentProvider(new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {

            }

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return ((ReportTreeNode) inputElement).getChildren().toArray();
            }

            @Override
            public boolean hasChildren(Object element) {
                return !((ReportTreeNode) element).isLeaf();
            }

            @Override
            public Object getParent(Object element) {
                return ((ReportTreeNode) element).getParent();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((ReportTreeNode) parentElement).getChildren().toArray();
            }
        });
        querySelect.setLabelProvider(new ILabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                return ((ReportTreeNode) element).getLabel();
            }

            @Override
            public void addListener(ILabelProviderListener listener) {

            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }
        });

        // lengthy tooltip faking code here
        final Tree tree = querySelect.getTree();
        final Display display = tree.getDisplay();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        // Disable native tooltip
        tree.setToolTipText("");

        // Implement a "fake" tooltip
        final Listener labelListener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                Label label = (Label) event.widget;
                Shell shell = label.getShell();
                switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (TreeItem) label.getData("_TREEITEM");
                    // Assuming table is single select, set the selection as if
                    // the mouse down event went through to the table
                    tree.setSelection(new TreeItem[] { (TreeItem) e.item });
                    tree.notifyListeners(SWT.Selection, e);
                    shell.dispose();
                    tree.setFocus();
                    break;
                case SWT.MouseExit:
                    shell.dispose();
                    break;
                }
            }
        };

        Listener tableListener = new Listener() {
            Shell tip = null;
            Label label = null;

            @Override
            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Dispose:
                case SWT.KeyDown:
                case SWT.MouseMove: {
                    if (tip == null)
                        break;
                    tip.dispose();
                    tip = null;
                    label = null;
                    break;
                }
                case SWT.MouseHover: {
                    TreeItem item = tree.getItem(new Point(event.x, event.y));
                    if (item != null) {
                        if (tip != null && !tip.isDisposed())
                            tip.dispose();
                        tip = new Shell(shell, SWT.ON_TOP | SWT.NO_FOCUS
                            | SWT.TOOL);
                        tip.setBackground(display
                            .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                        FillLayout layout = new FillLayout();
                        layout.marginWidth = 2;
                        tip.setLayout(layout);
                        label = new Label(tip, SWT.NONE);
                        label.setForeground(display
                            .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                        label.setBackground(display
                            .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                        label.setData("_TREEITEM", item);
                        String text = ((ReportTreeNode) item.getData())
                            .getToolTipText();
                        if (text.equalsIgnoreCase(""))
                            return;
                        else
                            label.setText(text);
                        label.addListener(SWT.MouseExit, labelListener);
                        label.addListener(SWT.MouseDown, labelListener);
                        Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        Rectangle rect = item.getBounds(0);
                        Point pt = tree.toDisplay(rect.x, rect.y);
                        tip.setBounds(pt.x, pt.y, size.x, size.y);
                        tip.setVisible(true);
                    }
                }
                }
            }
        };
        tree.addListener(SWT.Dispose, tableListener);
        tree.addListener(SWT.KeyDown, tableListener);
        tree.addListener(SWT.MouseMove, tableListener);
        tree.addListener(SWT.MouseHover, tableListener);

        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Menu menu = querySelect.getTree().getMenu();
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }

                Object element = ((StructuredSelection) querySelect
                    .getSelection()).getFirstElement();
                final ReportTreeNode node = (ReportTreeNode) element;
                if (node != null
                    && node.getParent().getLabel().compareTo("Custom") == 0) {
                    MenuItem mi = new MenuItem(menu, SWT.NONE);
                    mi.setText("Delete");
                    mi.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            File file = new File(Platform.getInstanceLocation()
                                .getURL().getPath()
                                + "/saved_reports/" + node.getLabel() + ".xml");
                            file.delete();
                            node.getParent().removeChild(node);
                            querySelect.refresh();
                        }
                    });
                }
            }
        });
        querySelect.getTree().setMenu(menu);

        ReportTreeNode root = new ReportTreeNode("", null);
        ReportTreeNode standard = new ReportTreeNode("Standard", null);
        ReportTreeNode advanced = new ReportTreeNode("Advanced", null);

        // create standard's subnodes
        ReportTreeNode aliquots = new ReportTreeNode("Aliquots", null);
        ReportTreeNode clinics = new ReportTreeNode("Clinics", null);
        ReportTreeNode patientVisits = new ReportTreeNode("PatientVisits", null);
        ReportTreeNode patients = new ReportTreeNode("Patients", null);
        ReportTreeNode misc = new ReportTreeNode("Sample Types", null);

        standard.addChild(aliquots);
        standard.addChild(clinics);
        standard.addChild(patientVisits);
        standard.addChild(patients);
        standard.addChild(misc);
        aliquots.setParent(standard);
        clinics.setParent(standard);
        patientVisits.setParent(standard);
        patients.setParent(standard);
        misc.setParent(standard);

        String[] names = AbstractReport.getReportNames();
        for (int i = 0; i < names.length; i++)
            try {
                ReportTreeNode child = new ReportTreeNode(names[i],
                    AbstractReport.getReportByName(names[i]));
                if (names[i].contains("Aliquot")) {
                    aliquots.addChild(child);
                    child.setParent(aliquots);
                } else if (names[i].contains("Sample Type")) {
                    misc.addChild(child);
                    child.setParent(misc);
                } else if (names[i].contains("Patient Visit")) {
                    patientVisits.addChild(child);
                    child.setParent(patientVisits);
                } else if (names[i].contains("Patient")) {
                    patients.addChild(child);
                    child.setParent(patients);
                } else if (names[i].contains("Clinic")) {
                    clinics.addChild(child);
                    child.setParent(clinics);
                } else
                    throw new Exception("Unable to place report node.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        List<Class<?>> advancedObjs = SearchUtils.getSearchableObjs();
        for (Class<?> obj : advancedObjs) {
            ReportTreeNode child = new ReportTreeNode(obj.getSimpleName()
                .replace("Wrapper", ""), QueryTree.constructTree(new HQLField(
                "", obj.getSimpleName(), obj)));
            advanced.addChild(child);
            child.setParent(advanced);
        }

        ReportTreeNode custom = new ReportTreeNode("Custom", null);
        custom.setParent(advanced);
        advanced.addChild(custom);

        File dir = new File(Platform.getInstanceLocation().getURL().getPath()
            + "/saved_reports");
        File[] files = dir.listFiles();
        if (files != null)
            try {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().contains(".xml")) {
                        String name = files[i].getName().replace(".xml", "");
                        ReportTreeNode customNode = new ReportTreeNode(name,
                            QueryTreeNode.getTreeFromFile(files[i]));
                        customNode.setParent(custom);
                        custom.addChild(customNode);
                    }
                }
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Error loading saved reports", e);
            }

        root.addChild(standard);
        standard.setParent(root);
        root.addChild(advanced);
        advanced.setParent(root);
        querySelect.setInput(root);
        querySelect.expandAll();

        GridData qgd = new GridData();
        qgd.verticalAlignment = SWT.FILL;
        qgd.horizontalAlignment = SWT.FILL;
        qgd.grabExcessHorizontalSpace = true;
        qgd.grabExcessVerticalSpace = true;
        querySelect.getTree().setLayoutData(qgd);
    }

    @Override
    public void setFocus() {

    }

    @Override
    public void reload() {
    }
}
