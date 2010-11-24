package edu.ualberta.med.biobank.widgets.infotables;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.dialogs.UserEditDialog;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserInfoTable extends InfoTableWidget<User> {
    public static final int ROWS_PER_PAGE = 10;
    private static final String[] HEADINGS = new String[] { "Login", "Email",
        "First Name", "Last Name" };
    private static final String LOADING_ROW = "loading...";
    private static final String GROUPS_LOADING_ERROR = "Unable to load groups.";
    private static final String USER_DELETE_ERROR = "Unable to delete user.";
    private static final String CANNOT_UNLOCK_USER = "Cannot unlock user {0}.";
    private static final String CONFIRM_DELETE_TITLE = "Confirm Deletion";
    private static final String CONFIRM_DELETE_MESSAGE = "Are you certain you want to delete \"{0}\"?";
    private static final String CONFIRM_SUICIDE_MESSAGE = "Are you certain you want to delete yourself as a user?";

    private Window parentWindow;
    private MenuItem unlockMenuItem;

    public UserInfoTable(Composite parent, List<User> collection,
        Window parentWindow) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE);

        this.parentWindow = parentWindow;

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
        unlockMenuItem.setText("Unlock User");
        unlockMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                User selectedUser = (User) getSelection();
                String userName = selectedUser.getLogin();
                try {
                    SessionManager.getAppService().unlockUser(
                        ((User) getSelection()).getLogin());
                    selectedUser.setLockedOut(false);
                    reloadCollection(getCollection(), selectedUser);
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError(MessageFormat.format(
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
            user.getFirstName(), user.getLastName()), "\t");
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                User user = (User) ((BiobankCollectionModel) element).o;
                if (user != null && user.isLockedOut() && columnIndex == 0) {
                    return BioBankPlugin.getDefault().getImage(
                        BioBankPlugin.IMG_LOCK);
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
                    return "";
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
                    return "";
                }
            }
        };
    }

    private void editUser(User user) {
        List<Group> groups = null;

        try {
            groups = SessionManager.getAppService().getSecurityGroups();
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError(GROUPS_LOADING_ERROR, e);
            return;
        }

        UserEditDialog dlg = new UserEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), user, groups, false);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getCollection(), user);
            notifyListeners();
        } else if (res == UserEditDialog.CLOSE_PARENT_RETURN_CODE) {
            parentWindow.close();
        }
    }

    private void deleteUser(User user) {
        try {
            String loginName = user.getLogin();
            String message;

            if (SessionManager.getUser().equals(loginName)) {
                message = CONFIRM_SUICIDE_MESSAGE;
            } else {
                message = MessageFormat.format(CONFIRM_DELETE_MESSAGE,
                    new Object[] { loginName });
            }

            if (BioBankPlugin.openConfirm(CONFIRM_DELETE_TITLE, message)) {
                SessionManager.getAppService().deleteUser(loginName);

                // remove the user from the collection
                getCollection().remove(user);

                reloadCollection(getCollection(), null);
                notifyListeners();
            }
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError(USER_DELETE_ERROR, e);
            return;
        }
    }
}