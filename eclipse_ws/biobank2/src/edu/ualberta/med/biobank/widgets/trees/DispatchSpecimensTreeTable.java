package edu.ualberta.med.biobank.widgets.trees;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.ModifyStateDispatchDialog;
import edu.ualberta.med.biobank.forms.utils.DispatchTableGroup;
import edu.ualberta.med.biobank.forms.utils.TableGroup;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.utils.BiobankClipboard;

public class DispatchSpecimensTreeTable extends BgcBaseWidget {

    private TreeViewer tv;
    private DispatchWrapper shipment;
    protected List<DispatchTableGroup> groups;

    public DispatchSpecimensTreeTable(Composite parent,
        final DispatchWrapper shipment, final boolean editSpecimensState,
        final boolean editSpecimensComment) {
        super(parent, SWT.NONE);

        this.shipment = shipment;

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
        tc.setText(Messages.DispatchSpecimensTreeTable_inventoryid_label);
        tc.setWidth(200);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.DispatchSpecimensTreeTable_type_label);
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.DispatchSpecimensTreeTable_pnumber_label);
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.DispatchSpecimensTreeTable_status_label);
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText(Messages.DispatchSpecimensTreeTable_comment_label);
        tc.setWidth(100);

        ITreeContentProvider contentProvider = new ITreeContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
                groups = DispatchTableGroup
                    .getGroupsForShipment(DispatchSpecimensTreeTable.this.shipment);
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
                if (element instanceof TableGroup) {
                    if (columnIndex == 0)
                        return ((TableGroup<?>) element).getTitle();
                    return ""; //$NON-NLS-1$
                } else if (element instanceof RequestContainerAdapter) {
                    if (columnIndex == 0)
                        return ((RequestContainerAdapter) element)
                            .getLabelInternal();
                    return ""; //$NON-NLS-1$
                } else if (element instanceof TreeItemAdapter) {
                    return ((TreeItemAdapter) element)
                        .getColumnText(columnIndex);
                }
                return ""; //$NON-NLS-1$
            }
        };
        tv.setLabelProvider(labelProvider);
        tv.setInput("root"); //$NON-NLS-1$

        tv.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object o = ((IStructuredSelection) tv.getSelection())
                    .getFirstElement();
                if (o instanceof DispatchSpecimenWrapper) {
                    DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) o;
                    SessionManager.openViewForm(dsa.getSpecimen());
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
                BiobankClipboard.addClipboardCopySupport(tv, menu,
                    labelProvider, 5);
                if (editSpecimensState || editSpecimensComment) {
                    DispatchSpecimenWrapper dsa = getSelectedSpecimen();
                    if (dsa != null) {
                        if (editSpecimensState
                            && DispatchSpecimenState.getState(dsa.getState()) == DispatchSpecimenState.NONE)
                            addSetMissingMenu(menu);
                        if (editSpecimensComment)
                            addModifyCommentMenu(menu);
                    }
                }
            }
        });
    }

    protected DispatchSpecimenWrapper getSelectedSpecimen() {
        IStructuredSelection selection = (IStructuredSelection) tv
            .getSelection();
        if (selection != null && selection.size() > 0
            && selection.getFirstElement() instanceof TreeItemAdapter) {
            return (DispatchSpecimenWrapper) ((TreeItemAdapter) selection
                .getFirstElement()).getSpecimen();
        }
        return null;
    }

    protected void addModifyCommentMenu(Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.DispatchSpecimensTreeTable_modidy_comment_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                modifyCommentAndState((IStructuredSelection) tv.getSelection(),
                    null);
            }
        });
    }

    private void addSetMissingMenu(final Menu menu) {
        MenuItem item;
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.DispatchSpecimensTreeTable_set_missing_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                modifyCommentAndState((IStructuredSelection) tv.getSelection(),
                    DispatchSpecimenState.MISSING);
            }
        });
    }

    private void modifyCommentAndState(
        IStructuredSelection iStructuredSelection,
        DispatchSpecimenState newState) {
        String previousComment = null;
        if (iStructuredSelection.size() == 1) {
            previousComment = ((DispatchSpecimenWrapper) ((TreeItemAdapter) iStructuredSelection
                .getFirstElement()).getSpecimen()).getComment();
        }
        ModifyStateDispatchDialog dialog = new ModifyStateDispatchDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            previousComment, newState);
        int res = dialog.open();
        if (res == Dialog.OK) {
            String comment = dialog.getComment();
            for (Iterator<?> iter = iStructuredSelection.iterator(); iter
                .hasNext();) {
                DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) ((TreeItemAdapter) iter
                    .next()).getSpecimen();
                dsa.setComment(comment);
                if (newState != null) {
                    dsa.setDispatchSpecimenState(newState);
                }
            }
            shipment.resetMap();
            tv.refresh();
            notifyListeners();
        }
    }

    public void refresh() {
        tv.setInput("refresh"); //$NON-NLS-1$
    }

}
