package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class UserManagementDialog extends BgcDialogWithPages {
    private static final I18n i18n = I18nFactory.getI18n(UserEditDialog.class);

    private final ManagerContext context;

    public UserManagementDialog(Shell parentShell, ManagerContext context) {
        super(parentShell);

        this.context = context;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: user management dialog title area message
        return i18n.tr("Select the security information to display");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: user management dialog title area title
        return i18n.tr("User/Group Management");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: user management dialog shell title
        return i18n.tr("User/Group Management");
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
