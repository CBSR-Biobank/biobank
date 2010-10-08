package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.ModifyStateDispatchShipmentDialog;
import edu.ualberta.med.biobank.forms.utils.DispatchTableGroup;

public class DispatchAliquotsTreeTable extends BiobankWidget {

    private TreeViewer tv;
    private DispatchShipmentWrapper shipment;

    public DispatchAliquotsTreeTable(Composite parent,
        final DispatchShipmentWrapper shipment,
        final boolean editAliquotsState, final boolean editAliquotsComment) {
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
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return DispatchTableGroup.getGroupsForShipment(shipment)
                    .toArray();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof DispatchTableGroup)
                    return ((DispatchTableGroup) parentElement).getChildren(
                        shipment).toArray();
                return null;
            }

            @Override
            public Object getParent(Object element) {
                if (element instanceof DispatchShipmentAliquotWrapper)
                    return DispatchTableGroup
                        .findParent((DispatchShipmentAliquotWrapper) element);
                return null;
            }

            @Override
            public boolean hasChildren(Object element) {
                return element instanceof DispatchTableGroup;
            }
        };
        tv.setContentProvider(contentProvider);

        final BiobankLabelProvider labelProvider = new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof DispatchTableGroup) {
                    if (columnIndex == 0)
                        return ((DispatchTableGroup) element)
                            .getTitle(shipment);
                    return "";
                }
                return super.getColumnText(element, columnIndex);
            }
        };
        tv.setLabelProvider(labelProvider);
        tv.setInput("root");

        tv.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object o = ((IStructuredSelection) tv.getSelection())
                    .getFirstElement();
                if (o instanceof DispatchShipmentAliquotWrapper) {
                    DispatchShipmentAliquotWrapper dsa = (DispatchShipmentAliquotWrapper) o;
                    SessionManager.openViewForm(dsa.getAliquot());
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
                if (editAliquotsState || editAliquotsComment) {
                    DispatchShipmentAliquotWrapper dsa = getSelectedAliquot();
                    if (dsa != null) {
                        if (editAliquotsState
                            && DispatchAliquotState.getState(dsa.getState()) == DispatchAliquotState.NONE_STATE)
                            addSetMissingMenu(menu);
                        if (editAliquotsComment)
                            addModifyCommentMenu(menu);
                    }
                }
            }
        });
    }

    protected DispatchShipmentAliquotWrapper getSelectedAliquot() {
        IStructuredSelection selection = (IStructuredSelection) tv
            .getSelection();
        if (selection != null && selection.size() > 0) {
            return (DispatchShipmentAliquotWrapper) selection.getFirstElement();
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
                    DispatchAliquotState.MISSING);
            }
        });
    }

    private void modifyCommentAndState(
        IStructuredSelection iStructuredSelection, DispatchAliquotState newState) {
        ModifyStateDispatchShipmentDialog dialog = new ModifyStateDispatchShipmentDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            newState);
        int res = dialog.open();
        if (res == Dialog.OK) {
            String comment = dialog.getComment();
            for (@SuppressWarnings("rawtypes")
            Iterator iter = iStructuredSelection.iterator(); iter.hasNext();) {
                DispatchShipmentAliquotWrapper dsa = (DispatchShipmentAliquotWrapper) iter
                    .next();
                dsa.setComment(comment);
                if (newState != null)
                    dsa.setState(newState.ordinal());
            }
            shipment.resetStateLists();
            tv.refresh();
            notifyListeners();
        }
    }

    public void refresh() {
        tv.refresh();
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
                    String row = "";
                    for (int i = 0; i < numCols; i++) {
                        String text = labelProvider.getColumnText(item, i);
                        if (text != null)
                            row += text;
                        if (i < numCols - 1)
                            row += ", ";
                    }
                    selectedRows.add(row);
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
