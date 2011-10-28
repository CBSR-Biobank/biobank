package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.dialogs.user.GroupEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;

public abstract class GroupInfoTable extends InfoTableWidget {
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS = new String[] { Messages.GroupInfoTable_name_label };

    public GroupInfoTable(Composite parent, List<BbGroupWrapper> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, BbGroupWrapper.class);

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                editGroup((BbGroupWrapper) getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                deleteGroup((BbGroupWrapper) getSelection());
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.GroupInfoTable_duplicate);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                duplicate((BbGroupWrapper) getSelection());
            }
        });
    }

    protected abstract void duplicate(BbGroupWrapper origGroup);

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof BbGroupWrapper
                    && o2 instanceof BbGroupWrapper) {
                    BbGroupWrapper g1 = (BbGroupWrapper) o1;
                    BbGroupWrapper g2 = (BbGroupWrapper) o2;
                    return g1.compareTo(g2);
                }
                return 0;
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null) {
            return null;
        }

        BbGroupWrapper group = (BbGroupWrapper) o;
        return StringUtils.join(Arrays.asList(group.getName()), "\t"); //$NON-NLS-1$
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                BbGroupWrapper group = (BbGroupWrapper) ((BiobankCollectionModel) element).o;
                if (group == null) {
                    if (columnIndex == 0) {
                        return Messages.GroupInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }

                switch (columnIndex) {
                case 0:
                    return group.getName();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    protected void editGroup(BbGroupWrapper group) {
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getList(), group);
            notifyListeners();
        }
    }

    protected boolean deleteGroup(BbGroupWrapper group) {
        try {
            String name = group.getName();
            String message = MessageFormat.format(
                Messages.GroupInfoTable_delete_confirm_msg,
                new Object[] { name });

            if (BgcPlugin.openConfirm(
                Messages.GroupInfoTable_delete_confirm_title, message)) {
                group.delete();
                // remove the group from the collection
                getList().remove(group);
                reloadCollection(getList(), null);
                notifyListeners();
                return true;
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.GroupInfoTable_delete_error_msg,
                e);
        }
        return false;
    }
}