package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.UserDeleteAction;
import edu.ualberta.med.biobank.common.action.security.UserDeleteInput;
import edu.ualberta.med.biobank.common.action.security.UserGetAction;
import edu.ualberta.med.biobank.common.action.security.UserGetInput;
import edu.ualberta.med.biobank.common.action.security.UserGetOutput;
import edu.ualberta.med.biobank.dialogs.user.TmpUtil;
import edu.ualberta.med.biobank.dialogs.user.UserEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.util.NullHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class UserInfoTable extends
    DefaultAbstractInfoTableWidget<User> {

    public static final int ROWS_PER_PAGE = 12;

    private static final String[] HEADINGS = new String[] {
        Messages.UserInfoTable_login_label,
        Messages.UserInfoTable_fullname_label,
        Messages.UserInfoTable_email_label };

    private final ManagerContext managerContext;
    private MenuItem unlockMenuItem;

    public UserInfoTable(Composite parent, List<User> users,
        ManagerContext managerContext) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(users);
        update();

        this.managerContext = managerContext;

        addEditItemListener(new IInfoTableEditItemListener<User>() {
            @Override
            public void editItem(InfoTableEvent<User> event) {
                User user = getSelection();
                editUser(user);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<User>() {
            @Override
            public void deleteItem(InfoTableEvent<User> event) {
                User user = getSelection();
                deleteUser(user);
            }
        });

        addClickListener(new IInfoTableDoubleClickItemListener<User>() {
            @Override
            public void doubleClick(InfoTableEvent<User> event) {
                User u = getSelection();
                if (u != null) editUser(u);
            }
        });

        unlockMenuItem = new MenuItem(menu, SWT.PUSH);
        unlockMenuItem.setText(Messages.UserInfoTable_unlock_label);
        unlockMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                User user = getSelection();
                String userName = user.getLogin();
                try {
                    SessionManager.getAppService().unlockUser(userName);
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError(MessageFormat.format(
                        Messages.UserInfoTable_unlock_error_msg,
                        new Object[] { userName }), e);
                }
            }
        });

        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                User user = getSelection();
                unlockMenuItem.setEnabled(isLockedOut(user));
            }
        });
    }

    private static boolean isLockedOut(User user) {
        boolean lockedOut = false;
        try {
            Long csmUserId = user.getCsmUserId();
            if (csmUserId != null) {
                lockedOut = SessionManager.getAppService()
                    .isUserLockedOut(user.getCsmUserId());
            }
        } catch (ApplicationException e) {
        }
        return lockedOut;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                User user = (User) element;
                switch (columnIndex) {
                case 0:
                    return user.getLogin();
                case 1:
                    return user.getFullName();
                case 2:
                    return user.getEmail();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    /**
     * return an integer representing the type of result
     */
    protected int editUser(User user) {
        UserGetOutput output = null;

        try {
            output = SessionManager.getAppService()
                .doAction(new UserGetAction(new UserGetInput(user)));
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }

        UserEditDialog dlg = new UserEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), output, managerContext);
        int res = dlg.open();
        if (res == Dialog.OK) {
            User modifiedUser = output.getUser();

            List<User> tmp = new ArrayList<User>(getList());
            tmp.remove(user);
            tmp.add(modifiedUser);
            Collections.sort(tmp, new UserComparator());
            setList(tmp);

            setSelection(modifiedUser);

            notifyListeners();
        }
        return res;
    }

    protected boolean deleteUser(User user) {
        try {
            String loginName = user.getLogin();
            String message;

            if (SessionManager.getUser().equals(user)) {
                BgcPlugin.openAsyncError(
                    Messages.UserInfoTable_delete_error_msg,
                    Messages.UserInfoTable_confirm_delete_suicide_msg);
                return false;
            }
            message = MessageFormat.format(
                Messages.UserInfoTable_confirm_delete_msg,
                new Object[] { loginName });

            if (BgcPlugin.openConfirm(
                Messages.UserInfoTable_confirm_delete_title, message)) {

                SessionManager.getAppService().doAction(
                    new UserDeleteAction(new UserDeleteInput(user)));

                // remove the user from the collection
                getList().remove(user);

                reload();

                notifyListeners();
                return true;
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(Messages.UserInfoTable_delete_error_msg, e);
        }
        return false;
    }

    public static class UserComparator implements Comparator<User> {
        @Override
        public int compare(User a, User b) {
            return NullHelper.safeCompareTo(a.getLogin(), b.getLogin(),
                String.CASE_INSENSITIVE_ORDER);
        }
    }
}