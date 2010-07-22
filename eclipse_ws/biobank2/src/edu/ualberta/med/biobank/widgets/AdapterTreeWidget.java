package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;
import edu.ualberta.med.biobank.views.TreeFilter;

public class AdapterTreeWidget extends Composite {

    private TreeViewer treeViewer;

    private ContainerWrapper containerSelectedAtDrag;
    private boolean dstDragLocationSelected;

    public AdapterTreeWidget(Composite parent,
        final AbstractViewWithAdapterTree parentView, boolean patternFilter) {
        super(parent, SWT.NONE);

        setLayout(new FillLayout());

        if (patternFilter) {
            FilteredTree filteredTree = new FilteredTree(this, SWT.BORDER
                | SWT.MULTI | SWT.V_SCROLL, new TreeFilter(), true);
            filteredTree.setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_LIST_BACKGROUND));
            filteredTree.setCursor(new Cursor(parent.getDisplay(),
                SWT.CURSOR_HAND));
            treeViewer = filteredTree.getViewer();
        } else {
            treeViewer = new TreeViewer(this);
        }
        /*----------------------------DND-----------------------------------*/

        treeViewer.addDragSupport(DND.DROP_MOVE,
            new Transfer[] { TextTransfer.getInstance() },
            new DragSourceListener() {

                @Override
                public void dragStart(DragSourceEvent event) {

                    ContainerWrapper container = getSrcSelectContainer();
                    if (container != null && container.hasParent()) {
                        event.doit = true;
                        containerSelectedAtDrag = container;
                    } else {
                        event.doit = false;
                        containerSelectedAtDrag = null;
                    }
                    dstDragLocationSelected = false;
                }

                @Override
                public void dragSetData(DragSourceEvent event) {
                    event.doit = false;
                }

                @Override
                public void dragFinished(DragSourceEvent event) {
                    containerSelectedAtDrag = null;
                    dstDragLocationSelected = false;
                }

            });

        treeViewer.addDropSupport(DND.DROP_MOVE,
            new Transfer[] { TextTransfer.getInstance() },
            new DropTargetListener() {
                @Override
                public void dragOver(DropTargetEvent event) {
                    event.feedback = DND.FEEDBACK_NONE;
                    if (event.item != null && containerSelectedAtDrag != null) {

                        TreeItem item = (TreeItem) event.item;

                        ModelWrapper<?> wrapper = ((AdapterBase) (item
                            .getData())).getModelObject();

                        if ((wrapper instanceof ContainerWrapper)) {
                            ContainerWrapper container = (ContainerWrapper) wrapper;
                            if (container.getContainerType()
                                .getChildContainerTypeCollection().size() != 0) {
                                if (container
                                    .getContainerType()
                                    .getChildContainerTypeCollection()
                                    .contains(
                                        containerSelectedAtDrag
                                            .getContainerType())) {
                                    if (!container.isContainerFull()) {
                                        event.feedback |= DND.FEEDBACK_SELECT;
                                        event.feedback |= DND.FEEDBACK_EXPAND;
                                    }
                                } else
                                    event.feedback |= DND.FEEDBACK_EXPAND;
                            }
                        }
                    }
                    dstDragLocationSelected = ((event.feedback & DND.FEEDBACK_SELECT) != 0);

                }

                @Override
                public void dragEnter(DropTargetEvent event) {
                }

                @Override
                public void dragLeave(DropTargetEvent event) {
                }

                @Override
                public void dragOperationChanged(DropTargetEvent event) {
                }

                @Override
                public void drop(DropTargetEvent event) {
                    if (!dstDragLocationSelected || event.item == null) {
                        event.detail = DND.DROP_NONE;
                        return;
                    }
                    TreeItem item = (TreeItem) event.item;
                    ModelWrapper<?> wrapper = ((AdapterBase) (item.getData()))
                        .getModelObject();
                    if (wrapper != null
                        && (wrapper instanceof ContainerWrapper)) {
                        ContainerWrapper dstContainer = (ContainerWrapper) wrapper;
                        if (dstContainer != null) {
                            System.out.println("src: "
                                + containerSelectedAtDrag);
                            System.out.println("Dst: " + dstContainer);
                        }

                    }

                }

                @Override
                public void dropAccept(DropTargetEvent event) {
                }
            });

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
                ((AdapterBase) element).performDoubleClick();
                parentView.getTreeViewer().expandToLevel(element, 1);
            }
        });
        treeViewer.addTreeListener(new ITreeViewerListener() {
            @Override
            public void treeCollapsed(TreeExpansionEvent e) {
            }

            @Override
            public void treeExpanded(TreeExpansionEvent e) {
                ((AdapterBase) e.getElement()).performExpand();
            }
        });
        treeViewer.setUseHashlookup(true);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                // TODO don't work well. Something prevent the status to be well
                // printed all the time - see #123
                // ISelection selection = event.getSelection();
                // if (!selection.isEmpty()
                // && selection instanceof IStructuredSelection) {
                // AdapterBase node = (AdapterBase) ((IStructuredSelection)
                // selection)
                // .getFirstElement();
                // IWorkbenchPartSite site = PlatformUI.getWorkbench()
                // .getActiveWorkbenchWindow().getActivePage()
                // .getActivePart().getSite();
                // if (site instanceof IViewSite) {
                // ((IViewSite) site).getActionBars()
                // .getStatusLineManager().setMessage(node.getName());
                // }
                // }
            }
        });

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
                if (element != null) {
                    ((AdapterBase) element).popupMenu(treeViewer,
                        treeViewer.getTree(), menu);
                }
            }
        });
        treeViewer.getTree().setMenu(menu);

        treeViewer.setComparator(new ViewerComparator() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof AdapterBase && e2 instanceof AdapterBase) {
                    ModelWrapper<?> object1 = ((AdapterBase) e1)
                        .getModelObject();
                    ModelWrapper<?> object2 = ((AdapterBase) e2)
                        .getModelObject();
                    if (object1 != null && object2 != null) {
                        return ((Comparable) object1).compareTo(object2);
                    }
                }
                return 0;
            }
        });
    }

    private ContainerWrapper getSrcSelectContainer() {
        TreeSelection ts = (TreeSelection) treeViewer.getSelection();
        if (ts == null || ts.isEmpty())
            return null;

        if (ts.getFirstElement() instanceof ContainerAdapter) {

            if (ts.size() != 1)
                BioBankPlugin
                    .openError("Cannot move multiple container",
                        "You cannot move multiple containers, please drag them one at a time.");

            ContainerAdapter adapter = (ContainerAdapter) ts.getFirstElement();
            if (adapter != null)
                return adapter.getContainer();

        }
        return null;
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    @Override
    public boolean setFocus() {
        return treeViewer.getTree().setFocus();
    }

}
