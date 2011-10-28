package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.dialogs.user.RoleEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;

public abstract class RoleInfoTable extends InfoTableWidget {
    public static final int ROWS_PER_PAGE = 12;

    private static final String[] HEADINGS = new String[] { Messages.RoleInfoTable_name_label };

    protected static class TableRowData {
        RoleWrapper role;
        String name;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name }, "\t"); //$NON-NLS-1$
        }
    }

    public RoleInfoTable(Composite parent, List<RoleWrapper> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, RoleWrapper.class);
        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                RoleWrapper role = ((TableRowData) getSelection()).role;
                editRole(role);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                RoleWrapper role = ((TableRowData) getSelection()).role;
                deleteRole(role);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.RoleInfoTable_duplicate_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                RoleWrapper role = ((TableRowData) getSelection()).role;
                duplicate(role);
            }
        });
    }

    protected abstract void duplicate(RoleWrapper origRole);

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof RoleWrapper && o2 instanceof RoleWrapper) {
                    return ((RoleWrapper) o1).compareTo((RoleWrapper) o2);
                }
                return 0;
            }
        };
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.UserInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    protected void editRole(RoleWrapper role) {
        RoleEditDialog dlg = new RoleEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), role);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getList(), role);
            notifyListeners();
        }
    }

    protected boolean deleteRole(RoleWrapper role) {
        try {
            String name = role.getName();
            String message = MessageFormat.format(
                Messages.RoleInfoTable_delete_confirm_msg,
                new Object[] { name });

            if (BgcPlugin.openConfirm(
                Messages.RoleInfoTable_delete_confirm_title, message)) {
                role.delete();
                // remove the role from the collection
                getList().remove(role);
                reloadCollection(getList(), null);
                notifyListeners();
                return true;
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(Messages.RoleInfoTable_delete_error_msg, e);
        }
        return false;
    }

    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        info.role = (RoleWrapper) o;
        info.name = info.role.getName();
        info.role.reload();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }
}