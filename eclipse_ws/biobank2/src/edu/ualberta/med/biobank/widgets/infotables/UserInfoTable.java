package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.Arrays;
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
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.dialogs.user.UserEditDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserInfoTable extends InfoTableWidget<User> {
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS = new String[] {
        Messages.UserInfoTable_login_label, Messages.UserInfoTable_email_label,
        Messages.UserInfoTable_firstname_label,
        Messages.UserInfoTable_lastname_label };
    private static final String LOADING_ROW = Messages.UserInfoTable_loading;
    private static final String GROUPS_LOADING_ERROR = Messages.UserInfoTable_load_error_msg;
    private static final String USER_DELETE_ERROR = Messages.UserInfoTable_delete_error_msg;
    private static final String CANNOT_UNLOCK_USER = Messages.UserInfoTable_unlock_error_msg;
    private static final String CONFIRM_DELETE_TITLE = Messages.UserInfoTable_confirm_delete_title;
    private static final String CONFIRM_DELETE_MESSAGE = Messages.UserInfoTable_confirm_delete_msg;
    private static final String CONFIRM_SUICIDE_MESSAGE = Messages.UserInfoTable_confirm_delete_suicide_msg;

    private MenuItem unlockMenuItem;

    public UserInfoTable(Composite parent, List<User> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, User.class);

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                editUser((User) getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                deleteUser((User) getSelection());
            }
        });

        unlockMenuItem = new MenuItem(menu, SWT.PUSH);
        unlockMenuItem.setText(Messages.UserInfoTable_unlock_label);
        unlockMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                User selectedUser = (User) getSelection();
                String userName = selectedUser.getLogin();
                try {
                    SessionManager.getAppService().unlockUser(
                        SessionManager.getUser(),
                        ((User) getSelection()).getLogin());
                    selectedUser.setLockedOut(false);
                    reloadCollection(getCollection(), selectedUser);
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError(MessageFormat.format(
                        CANNOT_UNLOCK_USER, new Object[] { userName }), e);
                }
            }
        });

        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                unlockMenuItem.setEnabled(((User) getSelection()).isLockedOut());
            }
        });
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof User && o2 instanceof User) {
                    User u1 = (User) o1;
                    User u2 = (User) o2;

                    int cmp = u1.getLogin().compareToIgnoreCase(u2.getLogin());
                    if (cmp != 0) {
                        return cmp;
                    }
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

        User user = (User) o;
        return StringUtils.join(Arrays.asList(user.getLogin(), user.getEmail(),
            user.getFirstName(), user.getLastName()), "\t"); //$NON-NLS-1$
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                User user = (User) ((BiobankCollectionModel) element).o;
                if (user != null && user.isLockedOut() && columnIndex == 0) {
                    return BiobankPlugin.getDefault().getImage(
                        BgcPlugin.IMG_LOCK);
                }
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                User user = (User) ((BiobankCollectionModel) element).o;
                if (user == null) {
                    if (columnIndex == 0) {
                        return LOADING_ROW;
                    }
                    return ""; //$NON-NLS-1$
                }

                switch (columnIndex) {
                case 0:
                    return user.getLogin();
                case 1:
                    return user.getEmail();
                case 2:
                    return user.getFirstName();
                case 3:
                    return user.getLastName();
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
        List<Group> groups = null;
        try {
            groups = SessionManager.getAppService().getSecurityGroups(
                SessionManager.getUser(), true);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(GROUPS_LOADING_ERROR, e);
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

    protected boolean deleteUser(User user) {
        try {
            String loginName = user.getLogin();
            String message;

            if (SessionManager.getUser().equals(user)) {
                message = CONFIRM_SUICIDE_MESSAGE;
            } else {
                message = MessageFormat.format(CONFIRM_DELETE_MESSAGE,
                    new Object[] { loginName });
            }

            if (BgcPlugin.openConfirm(CONFIRM_DELETE_TITLE, message)) {
                SessionManager.getAppService().deleteUser(
                    SessionManager.getUser(), loginName);

                // remove the user from the collection
                getCollection().remove(user);

                reloadCollection(getCollection(), null);
                notifyListeners();
                return true;
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(USER_DELETE_ERROR, e);
        }
        return false;
    }
}