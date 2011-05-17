package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ItemWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.forms.utils.DispatchTableGroup;
import edu.ualberta.med.biobank.forms.utils.RequestTableGroup;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;

public class RequestSpecimensTreeTable extends BiobankWidget {

    private TreeViewer tv;

    protected List<DispatchTableGroup> groups;

    @SuppressWarnings("unused")
    public RequestSpecimensTreeTable(Composite parent, RequestWrapper shipment) {
        super(parent, SWT.NONE);

        setLayout(new FillLayout());
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 400;
        setLayoutData(gd);

        tv = new TreeViewer(this, SWT.MULTI | SWT.BORDER);
        Tree tree = tv.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Inventory Id");
        tc.setWidth(200);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Type");
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Location");
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Claimed By");
        tc.setWidth(100);

        ITreeContentProvider contentProvider = new ITreeContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
                // groups = RequestTableGroup
                // .getGroupsForShipment(RequestSpecimensTreeTable.this.shipment);
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
                return ((Node) element).getChildren().size() != 0;
            }
        };
        tv.setContentProvider(contentProvider);

        final BiobankLabelProvider labelProvider = new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof RequestTableGroup) {
                    if (columnIndex == 0)
                        return ((RequestTableGroup) element).getTitle();
                    return "";
                } else if (element instanceof RequestContainerAdapter) {
                    if (columnIndex == 0)
                        return ((RequestContainerAdapter) element)
                            .getLabelInternal();
                    return "";
                } else if (element instanceof TreeItemAdapter) {
                    return ((TreeItemAdapter) element)
                        .getColumnText(columnIndex);
                }
                return "";
            }
        };
        tv.setLabelProvider(labelProvider);
        tv.setInput("root");

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

                RequestSpecimenWrapper ra = getSelectedSpecimen();
                if (ra != null) {
                    BiobankClipboard.addClipboardCopySupport(tv, menu,
                        labelProvider, 4);
                    addSetUnavailableMenu(menu);
                    addClaimMenu(menu);
                } else {
                    Object node = getSelectedNode();
                    if (node != null) {
                        addClaimMenu(menu);
                    }
                }
            }
        });
    }

    protected Object getSelectedNode() {
        IStructuredSelection selection = (IStructuredSelection) tv
            .getSelection();
        if (selection != null
            && selection.size() > 0
            && (selection.getFirstElement() instanceof TreeItemAdapter || selection
                .getFirstElement() instanceof RequestContainerAdapter))
            return selection.getFirstElement();
        return null;
    }

    protected RequestSpecimenWrapper getSelectedSpecimen() {
        Object node = getSelectedNode();
        if (node != null && node instanceof TreeItemAdapter) {
            return (RequestSpecimenWrapper) ((TreeItemAdapter) node)
                .getSpecimen();
        }
        return null;
    }

    protected void addClaimMenu(Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Claim");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                claim(getSelectedNode());
                refresh();
            }
        });
    }

    protected void claim(Object node) {
        try {
            if (node instanceof TreeItemAdapter) {
                RequestSpecimenWrapper a = (RequestSpecimenWrapper) ((TreeItemAdapter) node)
                    .getSpecimen();
                a.setClaimedBy(SessionManager.getUser().getFirstName());
                a.persist();
            } else {
                List<Node> children = ((RequestContainerAdapter) node)
                    .getChildren();
                for (Object child : children)
                    claim(child);
            }
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Failed to claim", e);
        }
    }

    private void addSetUnavailableMenu(final Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Flag as unavailable");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getSelectedSpecimen().setState(
                    RequestSpecimenState.UNAVAILABLE_STATE.getId());
                try {
                    getSelectedSpecimen().persist();
                } catch (Exception e) {
                    BiobankPlugin.openAsyncError("Save Error", e);
                }
                refresh();
            }
        });
    }

    public void refresh() {
        tv.setInput("refresh");
    }

}
