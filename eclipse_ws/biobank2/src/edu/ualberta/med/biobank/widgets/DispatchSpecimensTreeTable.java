package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;

public class DispatchSpecimensTreeTable extends BiobankWidget {

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
        tc.setText("Inventory Id");
        tc.setWidth(200);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Type");
        tc.setWidth(100);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Patient Number");
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Activity Status");
        tc.setWidth(120);

        tc = new TreeColumn(tree, SWT.LEFT);
        tc.setText("Dispatch comment");
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
                addClipboardCopySupport(menu, labelProvider);
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
        item.setText("Modify comment");
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
        item.setText("Set as missing");
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
        ModifyStateDispatchDialog dialog = new ModifyStateDispatchDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            newState);
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
        tv.setInput("refresh");
    }

    private void addClipboardCopySupport(Menu menu,
        final BiobankLabelProvider labelProvider) {
        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent event) {
                int numCols = tv.getTree().getColumnCount();
                List<Object> selectedRows = new ArrayList<Object>();
                IStructuredSelection sel = (IStructuredSelection) tv
                    .getSelection();
                for (Iterator<Object> iterator = sel.iterator(); iterator
                    .hasNext();) {
                    Object item = iterator.next();
                    List<String> row = new ArrayList<String>();
                    for (int i = 0; i < numCols; i++) {
                        String text = labelProvider.getColumnText(item, i);
                        if (text != null) {
                            row.add(text);
                        } else {
                            row.add("");
                        }
                    }
                    selectedRows.add(StringUtils.join(row, ","));
                }
                if (selectedRows.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Object row : selectedRows) {
                        if (sb.length() != 0) {
                            sb.append(System.getProperty("line.separator"));
                        }
                        sb.append(row.toString());
                    }
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    Clipboard cb = new Clipboard(Display.getDefault());
                    cb.setContents(new Object[] { sb.toString() },
                        new Transfer[] { textTransfer });
                }
            }
        });
    }

}
