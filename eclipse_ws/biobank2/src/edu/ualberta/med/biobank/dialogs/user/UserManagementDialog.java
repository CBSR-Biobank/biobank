package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementDialog extends BgcDialogWithPages {

    private List<UserWrapper> currentAllUsersList;
    private List<BbGroupWrapper> currentAllGroupsList;

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
            protected List<BbGroupWrapper> getCurrentAllGroupsList() {
                return getGroups();
            }
        });
        nodes.add(new RolesPage(this));
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

    protected List<BbGroupWrapper> getGroups() {
        if (currentAllGroupsList == null)
            try {
                currentAllGroupsList = BbGroupWrapper
                    .getAllGroups(SessionManager.getAppService());
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_groups_load_error_title, e);
            }
        return currentAllGroupsList;
    }

}
