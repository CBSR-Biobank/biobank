package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.reports.QueryObject;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.reports.advanced.HQLField;
import edu.ualberta.med.biobank.common.reports.advanced.SearchUtils;
import edu.ualberta.med.biobank.forms.AdvancedReportsEditor;
import edu.ualberta.med.biobank.forms.ReportsEditor;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.QueryTree;

public class ReportsView extends ViewPart {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";
    public static ReportsView reportsView;

    private Composite top;

    private TreeViewer querySelect;

    public ReportsView() {
        reportsView = this;
    }

    public static TreeViewer getTree() {
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
                Assert.isTrue(!node.getParent().isRoot());
                if (node.getParent().getLabel().compareTo("Advanced") == 0)
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().openEditor(new ReportInput(node),
                                AdvancedReportsEditor.ID);
                    } catch (Exception ex) {
                        BioBankPlugin.openAsyncError("Error opening editor.",
                            ex);
                    }
                else
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().openEditor(new ReportInput(node),
                                ReportsEditor.ID);
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
                return !((ReportTreeNode) element).isRoot();
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
        ReportTreeNode root = new ReportTreeNode("", null);
        ReportTreeNode standard = new ReportTreeNode("Standard", null);
        ReportTreeNode advanced = new ReportTreeNode("Advanced", null);
        String[] names = QueryObject.getQueryObjectNames();
        for (int i = 0; i < names.length; i++)
            try {
                ReportTreeNode child = new ReportTreeNode(names[i], QueryObject
                    .getQueryObjectByName(names[i]));
                standard.addChild(child);
                child.setParent(standard);
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
        root.addChild(standard);
        standard.setParent(root);
        root.addChild(advanced);
        advanced.setParent(root);
        querySelect.setInput(root);

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
}
