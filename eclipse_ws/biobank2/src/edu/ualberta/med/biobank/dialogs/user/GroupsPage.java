package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.widgets.infotables.GroupInfoTable;

public abstract class GroupsPage extends BgcDialogPage {

    private GroupInfoTable groupInfoTable;
    private ArrayList<Group> internalGroupList;

    public GroupsPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return "Groups";
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<Group>(content) {
            @Override
            protected boolean accept(Group group, String text) {
                return contains(group.getName(), text);
            }

            @Override
            public List<Group> getAllCollection() {
                return getInternalAllGroupsList();
            }

            @Override
            public void setFilteredList(List<Group> filteredObjects) {
                groupInfoTable.reloadCollection(filteredObjects);
            }
        };

        groupInfoTable = new GroupInfoTable(content, null);
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
                    final List<Group> groups = getInternalAllGroupsList();
                    sleep(200); // FIXME for some reason, if the group list is
                                // already loaded and therefore is retrieved
                                // right away, the setCollection method is not
                                // working because the current thread is still
                                // alive (see setCollection implementation).
                                // With a small pause, it is ok.
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            groupInfoTable.setCollection(groups);
                        }
                    });
                } catch (final Exception ex) {
                    Display.getDefault().syncExec(new Runnable() {
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

    private List<Group> getInternalAllGroupsList() {
        if (internalGroupList == null) {
            internalGroupList = new ArrayList<Group>();
            for (Group g : getCurrentAllGroupsList()) {
                if (!g.isSuperAdministratorGroup())
                    internalGroupList.add(g);
            }
        }
        return internalGroupList;
    }

    protected abstract List<Group> getCurrentAllGroupsList();

    protected void addGroup() {
        final Group group = new Group();
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), group, true);
        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.UserManagementDialog_group_added_title, MessageFormat
                    .format(Messages.UserManagementDialog_group_added_msg,
                        group.getName()));
            resetAllGroupsList();
            internalGroupList = null;
            groupInfoTable.reloadCollection(getInternalAllGroupsList(), group);
        }
    }

    protected abstract void resetAllGroupsList();

    @Override
    public void runAddAction() {
        addGroup();
    }

}
