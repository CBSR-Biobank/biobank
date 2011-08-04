package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.GroupTemplate;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementDialog extends BgcDialogWithPages {

    private List<User> currentAllUsersList;
    private List<Group> currentAllGroupsList;
    private List<GroupTemplate> currentAllTemplatesList;

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

            @Override
            protected List<GroupTemplate> getTemplates() {
                return getGroupTemplates();
            }
        });
        if (SessionManager.getUser().isSuperAdministrator())
            nodes.add(new TemplatesPage(this) {
                @Override
                protected List<GroupTemplate> getCurrentAllTemplatesList() {
                    return getGroupTemplates();
                }
            });
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
                currentAllGroupsList = SessionManager.getAppService()
                    .getSecurityGroups(SessionManager.getUser(), true);
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_groups_load_error_title, e);
            }
        return currentAllGroupsList;
    }

    protected List<GroupTemplate> getGroupTemplates() {
        if (currentAllTemplatesList == null) {
            currentAllTemplatesList = new ArrayList<GroupTemplate>();
            // FIXME should retrieve true information from server

            // Shipt=46
            // cevent=47
            // assign=48
            // dispatch=50
            // link=67
            // printer=74
            // pevent=66
            // reports=65

            GroupTemplate clinic = new GroupTemplate(1L, "Clinic");
            clinic.setCenterFeaturesEnabled(Arrays.asList(47, 50));
            currentAllTemplatesList.add(clinic);
            GroupTemplate site = new GroupTemplate(2L, "Site");
            site.setCenterFeaturesEnabled(Arrays.asList(47, 50, 66, 67, 48));
            currentAllTemplatesList.add(site);
        }
        return currentAllTemplatesList;
    }
}
