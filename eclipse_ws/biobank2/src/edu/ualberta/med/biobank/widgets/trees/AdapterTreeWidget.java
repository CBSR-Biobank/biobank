package edu.ualberta.med.biobank.widgets.trees;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.dialogs.FilteredTree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.ContainerDragDropListener;
import edu.ualberta.med.biobank.treeview.util.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.util.NodeLabelProvider;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNodeTransfer;
import edu.ualberta.med.biobank.widgets.utils.TreeFilter;

public class AdapterTreeWidget extends Composite {

    private TreeViewer treeViewer;

    private ContainerDragDropListener adapterTreeDragDropListener;

    private Object mouseMoveLastElement = null;

    private String lastToolTipText = null;

    public AdapterTreeWidget(Composite parent, boolean patternFilter) {
        super(parent, SWT.NONE);

        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        parent.setLayout(gl);
        setLayout(gl);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (patternFilter) {
            FilteredTree filteredTree = new FilteredTree(this, SWT.BORDER
                | SWT.SINGLE | SWT.V_SCROLL, new TreeFilter(), true);
            filteredTree.setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_LIST_BACKGROUND));
            filteredTree.setCursor(new Cursor(parent.getDisplay(),
                SWT.CURSOR_HAND));
            treeViewer = filteredTree.getViewer();
        } else {
            treeViewer = new TreeViewer(this, SWT.SINGLE);
        }

        gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        treeViewer.getTree().setLayout(gl);
        treeViewer.getTree().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));

        /*----------------------------DND-----------------------------------*/

        adapterTreeDragDropListener = new ContainerDragDropListener(treeViewer);

        treeViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { MultiSelectNodeTransfer.getInstance() },
            adapterTreeDragDropListener);

        treeViewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { MultiSelectNodeTransfer.getInstance() },
            adapterTreeDragDropListener);

        /*----------------------------DND-----------------------------------*/

        treeViewer.setLabelProvider(new NodeLabelProvider());
        treeViewer.setContentProvider(new NodeContentProvider());
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();

                if (selection == null)
                    return;

                Object element = ((StructuredSelection) selection)
                    .getFirstElement();
                ((AbstractAdapterBase) element).performDoubleClick();
                treeViewer.expandToLevel(element, 1);
            }
        });
        treeViewer.addTreeListener(new ITreeViewerListener() {
            @Override
            public void treeCollapsed(TreeExpansionEvent e) {
                //
            }

            @Override
            public void treeExpanded(TreeExpansionEvent e) {
                ((AbstractAdapterBase) e.getElement()).performExpand();
            }
        });
        treeViewer.setUseHashlookup(true);
        treeViewer.getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                AbstractViewWithAdapterTree view = SessionManager
                    .getCurrentAdapterViewWithTree();
                view.setFocus();
                // make sure the view is activated when the user click in the
                // tree (to update the icons) :
                view.activate();
            }
        });

        Menu menu = new Menu(parent.getShell(), SWT.NONE);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.type != SWT.Show) return;

                Menu menu = treeViewer.getTree().getMenu();
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }

                Object element = ((StructuredSelection) treeViewer.getSelection()).getFirstElement();
                if (element != null) {
                    ((AbstractAdapterBase) element).popupMenu(
                        treeViewer, treeViewer.getTree(), menu);
                }
            }
        });
        treeViewer.getTree().setMenu(menu);

        treeViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof AbstractAdapterBase
                    && e2 instanceof AbstractAdapterBase) {
                    return ((AbstractAdapterBase) e1)
                        .compareTo((AbstractAdapterBase) e2);
                }
                return 0;
            }
        });

        treeViewer.getTree().addListener(SWT.MouseMove, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String tooltip = null;
                ViewerCell cell = treeViewer
                    .getCell(new Point(event.x, event.y));
                if (cell != null) {
                    Object element = cell.getElement();
                    if ((element != null) && (element != mouseMoveLastElement)) {
                        tooltip = ((AbstractAdapterBase) element)
                            .getTooltipText();
                        lastToolTipText = tooltip;
                        mouseMoveLastElement = element;
                    } else {
                        tooltip = lastToolTipText;
                    }
                }
                treeViewer.getTree().setToolTipText(tooltip);
            }

        });

    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    @Override
    public boolean setFocus() {
        return treeViewer.getTree().setFocus();
    }

}
