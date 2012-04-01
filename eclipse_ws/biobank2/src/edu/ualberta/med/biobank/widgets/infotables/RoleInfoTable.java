package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.RoleDeleteAction;
import edu.ualberta.med.biobank.common.action.security.RoleDeleteInput;
import edu.ualberta.med.biobank.dialogs.user.RoleEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Role;

public abstract class RoleInfoTable extends
    DefaultAbstractInfoTableWidget<Role> {
    public static final int ROWS_PER_PAGE = 12;

    private static final String[] HEADINGS =
        new String[] { Messages.RoleInfoTable_name_label };

    @SuppressWarnings("unused")
    public RoleInfoTable(Composite parent, List<Role> collection) {
        super(parent, HEADINGS, ROWS_PER_PAGE);
        addEditItemListener(new IInfoTableEditItemListener<Role>() {
            @Override
            public void editItem(InfoTableEvent<Role> event) {
                Role role = getSelection();
                editRole(role);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Role>() {
            @Override
            public void deleteItem(InfoTableEvent<Role> event) {
                Role role = getSelection();
                deleteRole(role);
            }
        });

        addClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Role r = getSelection();
                if (r != null) editRole(r);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.RoleInfoTable_duplicate_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Role role = getSelection();
                duplicate(role);
            }
        });
    }

    protected abstract void duplicate(Role origRole);

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Role role = (Role) element;
                switch (columnIndex) {
                case 0:
                    return role.getName();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    protected void editRole(Role role) {
        RoleEditDialog dlg = new RoleEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), role);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reload();
            notifyListeners();
        }
    }

    protected boolean deleteRole(Role role) {
        try {
            String name = role.getName();
            String message = MessageFormat.format(
                Messages.RoleInfoTable_delete_confirm_msg,
                new Object[] { name });

            if (BgcPlugin.openConfirm(
                Messages.RoleInfoTable_delete_confirm_title, message)) {

                SessionManager.getAppService().doAction(
                    new RoleDeleteAction(new RoleDeleteInput(role)));

                getList().remove(role);
                reload();

                notifyListeners();
                return true;
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(Messages.RoleInfoTable_delete_error_msg, e);
        }
        return false;
    }
}