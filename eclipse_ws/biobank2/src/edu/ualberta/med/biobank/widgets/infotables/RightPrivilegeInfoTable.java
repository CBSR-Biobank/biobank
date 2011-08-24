package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.dialogs.user.RightPrivilegeAddDialog;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class RightPrivilegeInfoTable extends
    InfoTableWidget<RightPrivilegeWrapper> {
    public static final int ROWS_PER_PAGE = 8;
    private static final String[] HEADINGS = new String[] {
        Messages.RightPrivilegeInfoTable_right_label,
        Messages.RightPrivilegeInfoTable_privileges_label };

    protected static class TableRowData {
        RightPrivilegeWrapper rp;
        String right;
        String privileges;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { right, privileges }, "\t"); //$NON-NLS-1$
        }
    }

    public RightPrivilegeInfoTable(Composite parent, final RoleWrapper role) {
        super(parent, role.getRightPrivilegeCollection(true), HEADINGS,
            ROWS_PER_PAGE, RightPrivilegeWrapper.class);

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                RightPrivilegeWrapper rp = ((TableRowData) getSelection()).rp;
                RightPrivilegeAddDialog dlg = new RightPrivilegeAddDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), role);
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
                RightPrivilegeWrapper rp = ((TableRowData) getSelection()).rp;
                role.removeFromRightPrivilegeCollection(Arrays.asList(rp));
                getCollection().remove(rp);
                reloadCollection(getCollection());
            }
        });
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof RightPrivilegeWrapper
                    && o2 instanceof RightPrivilegeWrapper) {
                    RightPrivilegeWrapper rp1 = (RightPrivilegeWrapper) o1;
                    RightPrivilegeWrapper rp2 = (RightPrivilegeWrapper) o2;
                    return rp1.compareTo(rp2);
                }
                return 0;
            }
        };
    }

    @Override
    public Object getCollectionModelObject(RightPrivilegeWrapper rp)
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
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
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