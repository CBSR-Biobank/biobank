package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.dialogs.user.UserEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserInfoTable extends InfoTableWidget<UserWrapper> {
    public static final int ROWS_PER_PAGE = 12;

    private static final String[] HEADINGS = new String[] {
        Messages.UserInfoTable_login_label,
        Messages.UserInfoTable_firstname_label,
        Messages.UserInfoTable_lastname_label,
        Messages.UserInfoTable_email_label, "Super administrator" };

    private MenuItem unlockMenuItem;

    protected static class TableRowData {
        UserWrapper user;
        String login;
        String email;
        String firstName;
        String lastName;
        boolean isSuperAdmin;
        boolean lockedOut;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { login, email, firstName, lastName,
                    String.valueOf(isSuperAdmin), String.valueOf(lockedOut) },
                "\t"); //$NON-NLS-1$
        }
    }

    public UserInfoTable(Composite parent, List<UserWrapper> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, UserWrapper.class);
        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                UserWrapper user = ((TableRowData) getSelection()).user;
                editUser(user);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                UserWrapper user = ((TableRowData) getSelection()).user;
                deleteUser(user);
            }
        });

        unlockMenuItem = new MenuItem(menu, SWT.PUSH);
        unlockMenuItem.setText(Messages.UserInfoTable_unlock_label);
        unlockMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                UserWrapper user = ((TableRowData) getSelection()).user;
                String userName = user.getLogin();
                try {
                    SessionManager.getAppService().unlockUser(userName);
                    user.setLockedOut(false);
                    reloadCollection(getCollection(), user);
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
                UserWrapper user = ((TableRowData) getSelection()).user;
                unlockMenuItem.setEnabled(user.isLockedOut());
            }
        });
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof UserWrapper && o2 instanceof UserWrapper) {
                    return ((UserWrapper) o1).compareTo((UserWrapper) o2);
                }
                return 0;
            }
        };
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info != null && info.lockedOut && columnIndex == 0) {
                    return BiobankPlugin.getDefault().getImage(
                        BgcPlugin.IMG_LOCK);
                }
                return null;
            }

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
                    return info.login;
                case 1:
                    return info.firstName;
                case 2:
                    return info.lastName;
                case 3:
                    return info.email;
                case 4:
                    return String.valueOf(info.isSuperAdmin);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    /**
     * return an integer representing the type of result
     */
    protected int editUser(UserWrapper user) {
        List<Group> groups = Collections.emptyList(); // getGroups();
        if (groups == null) {
            BgcPlugin.openAsyncError(Messages.UserInfoTable_error_title,
                Messages.UserInfoTable_nogroups_msg);
            return Dialog.CANCEL;
        }

        UserEditDialog dlg = new UserEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), user, groups, false);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getCollection(), user);
            notifyListeners();
        }
        return res;
    }

    protected boolean deleteUser(UserWrapper user) {
        try {
            String loginName = user.getLogin();
            String message;

            if (SessionManager.getUser().equals(user)) {
                message = Messages.UserInfoTable_confirm_delete_suicide_msg;
            } else {
                message = MessageFormat.format(
                    Messages.UserInfoTable_confirm_delete_msg,
                    new Object[] { loginName });
            }

            if (BgcPlugin.openConfirm(
                Messages.UserInfoTable_confirm_delete_title, message)) {
                user.delete();

                // remove the user from the collection
                getCollection().remove(user);

                reloadCollection(getCollection(), null);
                notifyListeners();
                return true;
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(Messages.UserInfoTable_delete_error_msg, e);
        }
        return false;
    }

    @Override
    public Object getCollectionModelObject(UserWrapper user) throws Exception {
        TableRowData info = new TableRowData();
        info.email = user.getEmail();
        info.firstName = user.getFirstName();
        info.lastName = user.getLastName();
        info.isSuperAdmin = user.isSuperAdmin();
        info.login = user.getLogin();
        info.lockedOut = user.isLockedOut();
        info.user = user;
        user.reload();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }
}