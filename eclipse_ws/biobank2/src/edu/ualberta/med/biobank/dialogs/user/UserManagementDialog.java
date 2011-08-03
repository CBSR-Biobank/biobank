package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementDialog extends BgcDialogWithPages {

    private List<User> currentAllUsersList;
    private List<Group> currentAllGroupsList;

    public UserManagementDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.UserManagementDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.UserManagementDialog_title;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.UserManagementDialog_title;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
            true);
    }

    @Override
    protected List<BgcDialogPage> createPages() {
        List<BgcDialogPage> nodes = new ArrayList<BgcDialogPage>();
        nodes.add(new UsersPage(this) {
            @Override
            protected List<User> getCurrentAllUsersList() {
                return getUsers();
            }

            @Override
            protected List<Group> getGroups() {
                return UserManagementDialog.this.getGroups();
            }
        });
        nodes.add(new GroupsPage(this) {
            @Override
            protected List<Group> getCurrentAllGroupsList() {
                return getGroups();
            }

            @Override
            protected void resetAllGroupsList() {
                currentAllGroupsList = null;
            }
        });
        nodes.add(new TemplatesPage(this));
        return nodes;
    }

    @Override
    protected BgcDialogPage getDefaultSelection() {
        return getPages().get(0);
    }

    protected List<User> getUsers() {
        if (currentAllUsersList == null) {
            try {
                currentAllUsersList = SessionManager.getAppService()
                    .getSecurityUsers(SessionManager.getUser());
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_users_load_error_title, e);
            }
        }
        return currentAllUsersList;
    }

    protected List<Group> getGroups() {
        if (currentAllGroupsList == null)
            try {
                // FIXME voir pour SuperAdmin group a ne pas modifier
                currentAllGroupsList = SessionManager.getAppService()
                    .getSecurityGroups(SessionManager.getUser(), true);
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_groups_load_error_title, e);
            }
        return currentAllGroupsList;
    }
}
