package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class UserManagementDialog extends BgcDialogWithPages {
    private final ManagerContext context;

    public UserManagementDialog(Shell parentShell, ManagerContext context) {
        super(parentShell);

        this.context = context;
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
        nodes.add(new UsersPage(this, context) {
            @Override
            protected List<User> getCurrentAllUsersList() {
                return context.getUsers();
            }

        });
        nodes.add(new GroupsPage(this, context) {
            @Override
            protected List<Group> getCurrentAllGroupsList() {
                return context.getGroups();
            }
        });

        if (context.isRoleManager()) {
            nodes.add(new RolesPage(this) {
                @Override
                protected List<Role> getCurrentAllRolesList() {
                    return context.getRoles();
                }
            });
        }

        return nodes;
    }

    @Override
    protected BgcDialogPage getDefaultSelection() {
        return getPages().get(0);
    }
}
