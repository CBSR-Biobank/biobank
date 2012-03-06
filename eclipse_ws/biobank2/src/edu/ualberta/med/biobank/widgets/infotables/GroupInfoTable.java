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

import edu.ualberta.med.biobank.common.wrappers.GroupWrapper;
import edu.ualberta.med.biobank.dialogs.user.GroupEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;

public abstract class GroupInfoTable extends InfoTableWidget<GroupWrapper> {
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS = new String[] { Messages.GroupInfoTable_name_label };

    public GroupInfoTable(Composite parent, List<GroupWrapper> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, GroupWrapper.class);

        addEditItemListener(new IInfoTableEditItemListener<GroupWrapper>() {
            @Override
            public void editItem(InfoTableEvent<GroupWrapper> event) {
                editGroup((GroupWrapper) getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<GroupWrapper>() {
            @Override
            public void deleteItem(InfoTableEvent<GroupWrapper> event) {
                deleteGroup((GroupWrapper) getSelection());
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.GroupInfoTable_duplicate);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                duplicate((GroupWrapper) getSelection());
            }
        });
    }

    protected abstract void duplicate(GroupWrapper origGroup);

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof GroupWrapper
                    && o2 instanceof GroupWrapper) {
                    GroupWrapper g1 = (GroupWrapper) o1;
                    GroupWrapper g2 = (GroupWrapper) o2;
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

        GroupWrapper group = (GroupWrapper) o;
        return StringUtils.join(Arrays.asList(group.getName()), "\t"); //$NON-NLS-1$
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                GroupWrapper group = (GroupWrapper) ((BiobankCollectionModel) element).o;
                if (group == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
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

    protected void editGroup(GroupWrapper group) {
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getList(), group);
            notifyListeners();
        }
    }

    protected boolean deleteGroup(GroupWrapper group) {
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