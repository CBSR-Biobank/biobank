package edu.ualberta.med.biobank.dialogs;

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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.infotables.GroupInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ManageSecurityDialog extends BiobankDialog {
    private final String TITLE = "Manage Security";
    private final String TITLE_AREA_MESSAGE = "Right-click to modify, delete or unlock users and groups.";
    private UserInfoTable userInfoTable;
    private List<User> currentUserList;
    private GroupInfoTable groupInfoTable;
    private List<Group> currentGroupList;

    private static final String USER_ADDED_TITLE = "User Added";
    private static final String USER_ADDED_MESSAGE = "Successfully added new user \"{0}\".";
    private static final String GROUP_ADDED_TITLE = "Group Added";
    private static final String GROUP_ADDED_MESSAGE = "Successfully added new group \"{0}\".";

    public ManageSecurityDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_AREA_MESSAGE;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Section usersSection = createSection(contents, "Users",
            "Add a new user", new SelectionAdapter() {
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
                    currentUserList.remove(user);
                return deleted;
            }
        };
        usersSection.setClient(userInfoTable);
        addExpansionListener(contents, usersSection, userInfoTable);
        List<User> tmpUsers = new ArrayList<User>();
        for (int i = 0; i < UserInfoTable.ROWS_PER_PAGE + 1; i++) {
            User user = new User();
            user.setLogin("loading...");
            tmpUsers.add(user);
        }
        userInfoTable.setCollection(tmpUsers);

        Section groupsSection = createSection(contents, "Groups",
            "Add a new group", new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addGroup();
                }
            });
        groupInfoTable = new GroupInfoTable(groupsSection, null) {
            @Override
            protected boolean deleteGroup(Group group) {
                boolean deleted = super.deleteGroup(group);
                if (deleted)
                    currentGroupList.remove(group);
                return deleted;
            }
        };
        groupsSection.setClient(groupInfoTable);
        addExpansionListener(contents, groupsSection, groupInfoTable);
        List<Group> tmpGroups = new ArrayList<Group>();
        for (int i = 0; i < GroupInfoTable.ROWS_PER_PAGE + 1; i++) {
            Group group = new Group();
            group.setName("loading...");
            tmpGroups.add(group);
        }
        groupInfoTable.setCollection(tmpGroups);

        Thread t = new Thread() {
            @Override
            public void run() {
                BiobankApplicationService appService = SessionManager
                    .getAppService();
                try {
                    currentUserList = appService.getSecurityUsers();
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            userInfoTable.setCollection(currentUserList);
                        }
                    });
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError("Unable to load users.", e);
                }
                try {
                    currentGroupList = appService.getSecurityGroups();
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            groupInfoTable.setCollection(currentGroupList);
                        }
                    });
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError("Unable to load groups.", e);
                }
            }
        };
        t.start();

    }

    protected void addUser() {
        User user = new User();
        UserEditDialog dlg = new UserEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), user, currentGroupList,
            true);
        int res = dlg.open();
        if (res == Status.OK) {
            BioBankPlugin.openAsyncInformation(USER_ADDED_TITLE,
                MessageFormat.format(USER_ADDED_MESSAGE, user.getLogin()));
            currentUserList.add(user);
            userInfoTable.reloadCollection(currentUserList, user);
        }
    }

    protected void addGroup() {
        Group group = new Group();
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group, true);
        int res = dlg.open();
        if (res == Status.OK) {
            BioBankPlugin.openAsyncInformation(GROUP_ADDED_TITLE,
                MessageFormat.format(GROUP_ADDED_MESSAGE, group.getName()));
            currentGroupList.add(group);
            groupInfoTable.reloadCollection(currentGroupList, group);
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
        titem.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
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
