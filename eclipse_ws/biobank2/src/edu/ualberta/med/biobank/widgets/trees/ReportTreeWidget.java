package edu.ualberta.med.biobank.widgets.trees;

import java.io.File;

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

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.forms.input.ReportInput;

public class ReportTreeWidget extends Composite {

    private TreeViewer treeViewer;

    public ReportTreeWidget(Composite parent) {
        super(parent, SWT.BORDER);

        setLayout(new FillLayout());

        treeViewer = new TreeViewer(this);
        treeViewer.setUseHashlookup(true);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (SessionManager.getInstance().isConnected())
                    executeDoubleClick(event);
            }
        });

        treeViewer.setContentProvider(new ITreeContentProvider() {
            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {

            }

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return ((AbstractReportTreeNode) inputElement).getChildren()
                    .toArray();
            }

            @Override
            public boolean hasChildren(Object element) {
                return !((AbstractReportTreeNode) element).isLeaf();
            }

            @Override
            public Object getParent(Object element) {
                return ((AbstractReportTreeNode) element).getParent();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((AbstractReportTreeNode) parentElement).getChildren()
                    .toArray();
            }
        });
        treeViewer.setLabelProvider(new ILabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                return ((AbstractReportTreeNode) element).getLabel();
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

        addTooltipBehaviour();

        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Menu menu = treeViewer.getTree().getMenu();
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }

                Object element = ((StructuredSelection) treeViewer
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
                            treeViewer.refresh();
                        }
                    });
                }
            }
        });
        treeViewer.getTree().setMenu(menu);
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    private void addTooltipBehaviour() {
        // lengthy tooltip faking code here
        final Tree tree = treeViewer.getTree();
        // Disable native tooltip
        tree.setToolTipText("");
        final Display display = tree.getDisplay();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
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
                        String text = ((AbstractReportTreeNode) item.getData())
                            .getToolTipText();
                        if (text == null || text.equalsIgnoreCase(""))
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
    }

    private void executeDoubleClick(DoubleClickEvent event) {
        AbstractReportTreeNode node = (AbstractReportTreeNode) ((IStructuredSelection) event
            .getSelection()).getFirstElement();
        try {
            if (((ReportTreeNode) node).getReport() != null)
                PlatformUI
                    .getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(new ReportInput(node),
                        ((ReportTreeNode) node).getReport().getEditorId());
        } catch (Exception ex) {
            BgcPlugin.openAsyncError("Error", ex,
                "There was an error while building page.");
        }
    }

    @Override
    public boolean setFocus() {
        return treeViewer.getTree().setFocus();
    }

    public void setInput(Object input) {
        this.treeViewer.setInput(input);
    }

    public void expandAll() {
        this.treeViewer.expandAll();
    }

}
