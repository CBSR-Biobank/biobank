package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;

public abstract class UsersPage extends BgcDialogPage {

    private UserInfoTable userInfoTable;

    public UsersPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return Messages.UsersPage_page_title;
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<UserWrapper>(content) {
            @Override
            protected boolean accept(UserWrapper user, String text) {
                return contains(user.getLogin(), text)
                    || contains(user.getEmail(), text)
                    || contains(user.getFirstName(), text)
                    || contains(user.getLastName(), text);
            }

            @Override
            public List<UserWrapper> getAllCollection() {
                return getCurrentAllUsersList();
            }

            @Override
            public void setFilteredList(List<UserWrapper> filteredObjects) {
                userInfoTable.reloadCollection(filteredObjects);
            }
        };

        userInfoTable = new UserInfoTable(content, null) {
            @Override
            protected int editUser(UserWrapper user) {
                int res = super.editUser(user);
                // when user modify itself. Close everything to log again
                if (res == UserEditDialog.CLOSE_PARENT_RETURN_CODE) {
                    dialog.close();
                }
                return res;
            }

            @Override
            protected boolean deleteUser(UserWrapper user) {
                boolean deleted = super.deleteUser(user);
                if (deleted)
                    getCurrentAllUsersList().remove(user);
                return deleted;
            }
        };
        List<UserWrapper> tmpUsers = new ArrayList<UserWrapper>();
        for (int i = 0; i < UserInfoTable.ROWS_PER_PAGE + 1; i++) {
            UserWrapper user = new UserWrapper(null);
            user.setLogin(Messages.UserManagementDialog_loading);
            tmpUsers.add(user);
        }
        userInfoTable.setCollection(tmpUsers);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final List<UserWrapper> users = getCurrentAllUsersList();
                    sleep(200);
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            userInfoTable.setCollection(users);
                        }
                    });
                } catch (final Exception ex) {
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            BgcPlugin
                                .openAsyncError(
                                    Messages.UserManagementDialog_get_users_groups_error_title,
                                    ex);
                        }
                    });
                }
            }
        };
        t.start();
        setControl(content);
    }

    protected abstract List<UserWrapper> getCurrentAllUsersList();

    protected void addUser() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                final UserWrapper user = new UserWrapper(SessionManager
                    .getAppService());
                user.setBulkEmails(true);

                UserEditDialog dlg = new UserEditDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    user, null, true);
                int res = dlg.open();
                if (res == Status.OK) {
                    BgcPlugin.openAsyncInformation(
                        Messages.UserManagementDialog_user_added_title,
                        MessageFormat.format(
                            Messages.UserManagementDialog_user_added_msg,
                            user.getLogin()));
                    getCurrentAllUsersList().add(user);
                    userInfoTable.reloadCollection(getCurrentAllUsersList(),
                        user);
                }
            }
        });
    }

    @Override
    public void runAddAction() {
        addUser();
    }

}
