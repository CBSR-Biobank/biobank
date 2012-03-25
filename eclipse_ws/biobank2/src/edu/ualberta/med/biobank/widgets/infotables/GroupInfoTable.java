package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.dialogs.user.GroupEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Group;

public abstract class GroupInfoTable extends
    DefaultAbstractInfoTableWidget<Group> {
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS =
        new String[] { Messages.GroupInfoTable_name_label };

    public GroupInfoTable(Composite parent, List<Group> collection) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(collection);

        addEditItemListener(new IInfoTableEditItemListener<Group>() {
            @Override
            public void editItem(InfoTableEvent<Group> event) {
                editGroup((Group) getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Group>() {
            @Override
            public void deleteItem(InfoTableEvent<Group> event) {
                deleteGroup((Group) getSelection());
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.GroupInfoTable_duplicate);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                duplicate((Group) getSelection());
            }
        });
    }

    protected abstract void duplicate(Group original);

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Group group = (Group) element;
                switch (columnIndex) {
                case 0:
                    return group.getName();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    protected void editGroup(Group group) {
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getList(), group);
            notifyListeners();
        }
    }

    protected boolean deleteGroup(Group group) {
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