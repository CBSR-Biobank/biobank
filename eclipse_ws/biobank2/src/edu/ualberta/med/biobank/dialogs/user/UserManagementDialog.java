package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.infotables.GroupInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementDialog extends BgcBaseDialog {
    private UserInfoTable userInfoTable;
    private List<User> currentUserList;
    private GroupInfoTable groupInfoTable;

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
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Section usersSection = createSection(contents,
            Messages.UserManagementDialog_users_label,
            Messages.UserManagementDialog_addUser_label,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addUser();
                }
            });
        userInfoTable = new UserInfoTable(usersSection, null) {
            @Override
            protected int editUser(User user) {
                int res = super.editUser(user);
                if (res == UserEditDialog.CLOSE_PARENT_RETURN_CODE) {
                    close();
                }
                return res;
            }

            @Override
            protected boolean deleteUser(User user) {
                boolean deleted = super.deleteUser(user);
                if (deleted)
                    getUsers().remove(user);
                return deleted;
            }
        };
        usersSection.setClient(userInfoTable);
        addExpansionListener(contents, usersSection, userInfoTable);
        List<User> tmpUsers = new ArrayList<User>();
        for (int i = 0; i < UserInfoTable.ROWS_PER_PAGE + 1; i++) {
            User user = new User();
            user.setLogin(Messages.UserManagementDialog_loading);
            tmpUsers.add(user);
        }
        userInfoTable.setCollection(tmpUsers);

        Section groupsSection = createSection(contents,
            Messages.UserManagementDialog_groups_label,
            Messages.UserManagementDialog_addGroup_label,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addGroup();
                }
            });
        groupInfoTable = new GroupInfoTable(groupsSection, null);
        groupsSection.setClient(groupInfoTable);
        addExpansionListener(contents, groupsSection, groupInfoTable);
        List<Group> tmpGroups = new ArrayList<Group>();
        for (int i = 0; i < GroupInfoTable.ROWS_PER_PAGE + 1; i++) {
            Group group = new Group();
            group.setName(Messages.UserManagementDialog_loading);
            tmpGroups.add(group);
        }
        groupInfoTable.setCollection(tmpGroups);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final List<User> users = getUsers();
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            userInfoTable.setCollection(users);
                        }
                    });
                    final List<Group> groups = getGroups(false);
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            groupInfoTable.setCollection(groups);
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
    }

    protected List<Group> getGroups(boolean includeSuperAdmin) {
        try {
            return SessionManager.getAppService().getSecurityGroups(
                SessionManager.getUser(), includeSuperAdmin);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.UserManagementDialog_groups_load_error_title, e);
        }
        return new ArrayList<Group>();
    }

    protected List<User> getUsers() {
        if (currentUserList == null) {
            try {
                currentUserList = SessionManager.getAppService()
                    .getSecurityUsers(SessionManager.getUser());
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    Messages.UserManagementDialog_users_load_error_title, e);
            }
        }
        return currentUserList;
    }

    protected void addUser() {
        User user = new User();
        UserEditDialog dlg = new UserEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), user, getGroups(true), true);
        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.UserManagementDialog_user_added_title, MessageFormat
                    .format(Messages.UserManagementDialog_user_added_msg,
                        user.getLogin()));
            getUsers().add(user);
            userInfoTable.reloadCollection(getUsers(), user);
        }
    }

    protected void addGroup() {
        Group group = new Group();
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group, true);
        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.UserManagementDialog_group_added_title, MessageFormat
                    .format(Messages.UserManagementDialog_group_added_msg,
                        group.getName()));
            groupInfoTable.reloadCollection(getGroups(false), group);
        }
    }

    private void addExpansionListener(final Composite contents,
        Section usersSection, final Composite insideComposite) {
        usersSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                contents.layout(true, true);
                Point shellSize = getShell().getSize();
                int height = insideComposite.computeSize(SWT.DEFAULT,
                    SWT.DEFAULT).y;

                if (e.getState()) {
                    height = shellSize.y + height;
                } else {
                    height = shellSize.y - height;
                }
                getShell().setSize(shellSize.x, height);
            }
        });

    }

    private Section createSection(final Composite contents, String title,
        String addTooltip, SelectionListener addListener) {
        Section section = new Section(contents, Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
        section.setText(title);
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ToolBar tbar = (ToolBar) section.getTextClient();
        if (tbar == null) {
            tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
            section.setTextClient(tbar);
        }

        ToolItem titem = new ToolItem(tbar, SWT.NULL);
        titem.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        titem.setToolTipText(addTooltip);
        titem.addSelectionListener(addListener);
        return section;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
            true);
    }
}
