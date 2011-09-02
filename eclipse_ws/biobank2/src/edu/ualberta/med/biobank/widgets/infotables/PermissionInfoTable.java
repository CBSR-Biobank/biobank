package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.dialogs.user.PermissionAddDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public abstract class PermissionInfoTable extends
    InfoTableWidget<PermissionWrapper> {
    public static final int ROWS_PER_PAGE = 8;
    private static final String[] HEADINGS = new String[] {
        Messages.PermissionInfoTable_right_label,
        Messages.PermissionInfoTable_privileges_label };

    protected static class TableRowData {
        PermissionWrapper rp;
        String right;
        String privileges;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { right, privileges }, "\t"); //$NON-NLS-1$
        }
    }

    public PermissionInfoTable(Composite parent,
        List<PermissionWrapper> rpCollection) {
        super(parent, rpCollection, HEADINGS, ROWS_PER_PAGE,
            PermissionWrapper.class);

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                PermissionWrapper rp = ((TableRowData) getSelection()).rp;
                PermissionAddDialog dlg = new PermissionAddDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), getAlreadyUsedRights());
                int res = dlg.edit(rp);
                if (res == Dialog.OK) {
                    reloadCollection(getCollection(), rp);
                    notifyListeners();
                }
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                PermissionWrapper rp = ((TableRowData) getSelection()).rp;
                removeFromPermissionCollection(Arrays.asList(rp));
                getCollection().remove(rp);
                reloadCollection(getCollection());
            }
        });
    }

    protected abstract List<BbRightWrapper> getAlreadyUsedRights();

    protected abstract void removeFromPermissionCollection(
        List<PermissionWrapper> rpList);

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof PermissionWrapper
                    && o2 instanceof PermissionWrapper) {
                    PermissionWrapper rp1 = (PermissionWrapper) o1;
                    PermissionWrapper rp2 = (PermissionWrapper) o2;
                    return rp1.compareTo(rp2);
                }
                return 0;
            }
        };
    }

    @Override
    public Object getCollectionModelObject(PermissionWrapper rp)
        throws Exception {
        TableRowData info = new TableRowData();
        info.right = rp.getRight() == null ? "" : rp.getRight().getName(); //$NON-NLS-1$
        List<String> privileNames = new ArrayList<String>();
        for (PrivilegeWrapper p : rp.getPrivilegeCollection(true)) {
            privileNames.add(p.getName());
        }
        info.privileges = StringUtils.join(privileNames, ","); //$NON-NLS-1$
        info.rp = rp;
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.PermissionInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.right;
                case 1:
                    return info.privileges;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

}