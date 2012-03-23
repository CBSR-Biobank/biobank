package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.GroupGetAllAction;
import edu.ualberta.med.biobank.common.action.security.GroupGetAllInput;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllAction;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllInput;
import edu.ualberta.med.biobank.common.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.common.wrappers.GroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Role;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementDialog extends BgcDialogWithPages {

    private List<UserWrapper> currentAllUsersList;
    private List<GroupWrapper> currentAllGroupsList;
    private List<RoleWrapper> currentAllRolesList;

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
            protected List<UserWrapper> getCurrentAllUsersList() {
                return getUsers();
            }

        });
        nodes.add(new GroupsPage(this) {
            @Override
            protected List<GroupWrapper> getCurrentAllGroupsList() {
                return getGroups();
            }
        });

        try {
            boolean isAllowedRoleMan =
                SessionManager.getAppService().isAllowed(
                    new RoleManagementPermission());
            if (isAllowedRoleMan) {
                nodes.add(new RolesPage(this) {
                    @Override
                    protected List<RoleWrapper> getCurrentAllRolesList() {
                        return getRoles();
                    }
                });
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nodes;
    }

    @Override
    protected BgcDialogPage getDefaultSelection() {
        return getPages().get(0);
    }

    protected List<UserWrapper> getUsers() {
        if (currentAllUsersList == null) {
            try {
                currentAllUsersList = UserWrapper.getAllUsers(SessionManager
                    .getAppService());
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_users_load_error_title, e);
            }
        }
        return currentAllUsersList;
    }

    protected List<GroupWrapper> getGroups() {
        if (currentAllGroupsList == null)
            try {
                SortedSet<Group> allGroups = SessionManager.getAppService()
                    .doAction(new GroupGetAllAction(new GroupGetAllInput()))
                    .getAllManageableGroups();
                List<GroupWrapper> allWrappedGroups =
                    ModelWrapper.wrapModelCollection(
                        SessionManager.getAppService(),
                        allGroups, GroupWrapper.class);
                currentAllGroupsList = allWrappedGroups;
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_groups_load_error_title, e);
            }
        return currentAllGroupsList;
    }

    protected List<RoleWrapper> getRoles() {
        if (currentAllRolesList == null)
            try {
                SortedSet<Role> allRoles = SessionManager.getAppService()
                    .doAction(new RoleGetAllAction(new RoleGetAllInput()))
                    .getAllRoles();
                List<RoleWrapper> allWrappedRoles =
                    ModelWrapper.wrapModelCollection(
                        SessionManager.getAppService(),
                        allRoles, RoleWrapper.class);
                currentAllRolesList = allWrappedRoles;
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_groups_load_error_title, e);
            }
        return currentAllRolesList;
    }
}
