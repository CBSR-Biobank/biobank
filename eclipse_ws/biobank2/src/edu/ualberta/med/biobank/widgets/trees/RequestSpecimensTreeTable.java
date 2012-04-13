package edu.ualberta.med.biobank.widgets.trees;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.request.RequestClaimAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ItemWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.forms.utils.RequestTableGroup;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestContainerAdapter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class RequestSpecimensTreeTable extends BgcBaseWidget {

    private TreeViewer tv;
    private RequestWrapper request;
    protected List<RequestTableGroup> groups;

    public RequestSpecimensTreeTable(Composite parent, RequestWrapper request) {
        super(parent, SWT.NONE);

        this.request = request;

        setLayout(new GridLayout(1, false));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 400;
        setLayoutData(gd);

        tv = new TreeViewer(this, SWT.SINGLE | SWT.BORDER);
        Tree tree = tv.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_location_label);
        tc.setWidth(300);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_id_label);
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_type_label);
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_patient_label);
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_claimed_label);
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.RequestSpecimensTreeTable_state_label);
        tc.setWidth(100);

        ITreeContentProvider contentProvider = new ITreeContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
                if (newInput != null)
                    groups =
                        RequestTableGroup
                            .getGroupsForRequest(RequestSpecimensTreeTable.this.request);
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return groups.toArray();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((Node) parentElement).getChildren().toArray();
            }

            @Override
            public Object getParent(Object element) {
                return ((Node) element).getParent();
            }

            @Override
            public boolean hasChildren(Object element) {
                return ((Node) element).getChildren() == null ? false
                    : ((Node) element).getChildren().size() > 0;
            }
        };
        tv.setContentProvider(contentProvider);

        final BiobankLabelProvider labelProvider = new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof RequestTableGroup) {
                    if (columnIndex == 0)
                        return ((RequestTableGroup) element).getTitle();
                    return ""; //$NON-NLS-1$
                } else if (element instanceof RequestContainerAdapter) {
                    if (columnIndex == 0)
                        return ((RequestContainerAdapter) element)
                            .getLabelInternal();
                    return ""; //$NON-NLS-1$
                } else if (element instanceof Node) {
                    if (columnIndex == 0) {
                        if ((RequestContainerAdapter) ((TreeItemAdapter) element)
                            .getParent() == null)
                            return ""; //$NON-NLS-1$
                        return ((RequestContainerAdapter) ((TreeItemAdapter) element)
                            .getParent()).container.getLabel()
                            + ((RequestSpecimenWrapper) ((TreeItemAdapter) element)
                                .getSpecimen()).getSpecimen()
                                .getWrappedObject()
                                .getSpecimenPosition().getPositionString();
                    } else if (columnIndex < 4)
                        return ((TreeItemAdapter) element)
                            .getColumnText(columnIndex - 1);
                    else if (columnIndex == 4)
                        return ((RequestSpecimenWrapper) ((TreeItemAdapter) element)
                            .getSpecimen()).getClaimedBy();
                    else if (columnIndex == 5) {
                        return RequestSpecimenState
                            .getState(
                                ((RequestSpecimenWrapper) ((TreeItemAdapter) element)
                                    .getSpecimen()).getState()).getLabel();
                    } else
                        return ""; //$NON-NLS-1$
                }
                return ""; //$NON-NLS-1$
            }
        };
        tv.setLabelProvider(labelProvider);
        tv.setInput(new RootNode());

        tv.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object o = ((IStructuredSelection) tv.getSelection())
                    .getFirstElement();
                if (o instanceof TreeItemAdapter) {
                    ItemWrapper ra = ((TreeItemAdapter) o).getSpecimen();
                    SessionManager.openViewForm(ra.getSpecimen());
                }
            }
        });

        final Menu menu = new Menu(this);
        tv.getTree().setMenu(menu);

        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }
                Object node = ((StructuredSelection) tv.getSelection())
                    .getFirstElement();
                if (node != null && ((Node) node).getParent() != null) {
                    addSetUnavailableMenu(menu);
                    addClaimMenu(menu);
                }
            }
        });
        GridData gdtree = new GridData();
        gdtree.grabExcessHorizontalSpace = true;
        gdtree.horizontalAlignment = SWT.FILL;
        gdtree.verticalAlignment = SWT.FILL;
        gdtree.grabExcessVerticalSpace = true;
        tv.getTree().setLayoutData(gdtree);

    }

    protected void addClaimMenu(Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.RequestSpecimensTreeTable_claim_menu);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                claim(getSelectionWrappers());
                refresh();
            }
        });
    }

    protected void claim(List<RequestSpecimenWrapper> specs) {
        try {
            List<Integer> rs = new ArrayList<Integer>();
            for (RequestSpecimenWrapper spec : specs) {
                spec.setClaimedBy(SessionManager.getUser().getLogin());
                rs.add(spec.getId());
            }
            SessionManager.getAppService().doAction(new RequestClaimAction(rs));
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.RequestSpecimensTreeTable_claim_error_title, e);
        }
    }

    private void addSetUnavailableMenu(final Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.RequestSpecimensTreeTable_flag_unavailable_menu);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                List<Integer> rs =
                    new ArrayList<Integer>();
                for (RequestSpecimenWrapper spec : getSelectionWrappers()) {
                    spec.setState(RequestSpecimenState.UNAVAILABLE_STATE
                        .getId());
                    rs.add(spec.getId());
                    try {
                        SessionManager.getAppService().doAction(
                            new RequestStateChangeAction(rs,
                                RequestSpecimenState.UNAVAILABLE_STATE));
                    } catch (Exception e) {
                        BgcPlugin
                            .openAsyncError(
                                Messages.RequestSpecimensTreeTable_save_error_title,
                                e);
                    }
                }
                refresh();
            }
        });
    }

    public List<RequestSpecimenWrapper> getSelectionWrappers() {
        return getWrappers(getAdapterSelection());
    }

    public List<RequestSpecimenWrapper> getWrappers() {
        return getWrappers(groups.get(0));
    }

    public List<RequestSpecimenWrapper> getWrappers(Node parent) {
        List<RequestSpecimenWrapper> list =
            new ArrayList<RequestSpecimenWrapper>();
        if (parent instanceof TreeItemAdapter) {
            RequestSpecimenWrapper spec =
                (RequestSpecimenWrapper) ((TreeItemAdapter) parent)
                    .getSpecimen();
            list.add(spec);
        } else {
            for (Node child : parent.getChildren()) {
                list.addAll(getWrappers(child));
            }
        }
        return list;
    }

    private Node getAdapterSelection() {
        Node o = (Node) ((StructuredSelection) tv.getSelection())
            .getFirstElement();
        return o.getParent() == null ? null : o;
    }

    public void refresh() {
        tv.refresh(true);
    }

    public void rebuild() {
        tv.setInput(new RootNode());
    }

    public Node search(String text) {
        return search(groups.get(0), text);
    }

    public Node search(Node startNode, String text) {
        if (startNode instanceof TreeItemAdapter
            && ((RequestSpecimenWrapper) ((TreeItemAdapter) startNode)
                .getSpecimen()).getSpecimen().getInventoryId().equals(text)) {
            return startNode;
        }
        Node found = null;
        List<Node> children = startNode.getChildren();
        for (Node child : children) {
            found = search(child, text);
            if (found != null)
                return found;
        }
        return null;
    }

    public void pull(Node updateNode) {
        groups.get(1).addChild(updateNode);
        tv.refresh();
    }

    public void dispatch(Node specNode) {
        groups.get(1).removeChild(specNode);
        tv.refresh();
    }
}
