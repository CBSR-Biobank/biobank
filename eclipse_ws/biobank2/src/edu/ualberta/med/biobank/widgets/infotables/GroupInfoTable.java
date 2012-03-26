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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteAction;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteInput;
import edu.ualberta.med.biobank.common.action.security.GroupGetAction;
import edu.ualberta.med.biobank.common.action.security.GroupGetInput;
import edu.ualberta.med.biobank.common.action.security.GroupGetOutput;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.dialogs.user.GroupEditDialog;
import edu.ualberta.med.biobank.dialogs.user.TmpUtil;
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

    private final ManagerContext context;

    public GroupInfoTable(Composite parent, List<Group> collection,
        ManagerContext context) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(collection);

        this.context = context;

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
        GroupGetOutput output = null;

        try {
            output = SessionManager.getAppService()
                .doAction(new GroupGetAction(new GroupGetInput(group)));
        } catch (Throwable t) {
            TmpUtil.displayException(t);
            return;
        }

        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), output, context);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reload();
            setSelection(group);

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

                SessionManager.getAppService().doAction(
                    new GroupDeleteAction(new GroupDeleteInput(group)));

                // remove the group from the collection
                getList().remove(group);

                reload();

                notifyListeners();
                return true;
            }
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }
        return false;
    }
}