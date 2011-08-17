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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.widgets.infotables.GroupInfoTable;

public abstract class GroupsPage extends BgcDialogPage {

    private GroupInfoTable groupInfoTable;

    public GroupsPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return Messages.GroupsPage_page_title;
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<BbGroupWrapper>(content) {
            @Override
            protected boolean accept(BbGroupWrapper group, String text) {
                return contains(group.getName(), text);
            }

            @Override
            public List<BbGroupWrapper> getAllCollection() {
                return getCurrentAllGroupsList();
            }

            @Override
            public void setFilteredList(List<BbGroupWrapper> filteredObjects) {
                groupInfoTable.reloadCollection(filteredObjects);
            }
        };

        groupInfoTable = new GroupInfoTable(content, null) {
            @Override
            protected boolean deleteGroup(BbGroupWrapper group) {
                boolean deleted = super.deleteGroup(group);
                if (deleted)
                    getCurrentAllGroupsList().remove(group);
                return deleted;
            }

            @Override
            protected void duplicate(BbGroupWrapper origGroup) {
                BbGroupWrapper newGroup = origGroup.duplicate();
                newGroup.setName("CopyOf" + newGroup.getName()); //$NON-NLS-1$
                addGroup(newGroup);
            }
        };
        List<BbGroupWrapper> tmpGroups = new ArrayList<BbGroupWrapper>();
        for (int i = 0; i < GroupInfoTable.ROWS_PER_PAGE + 1; i++) {
            BbGroupWrapper group = new BbGroupWrapper(null);
            group.setName(Messages.UserManagementDialog_loading);
            tmpGroups.add(group);
        }
        groupInfoTable.setCollection(tmpGroups);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final List<BbGroupWrapper> groups = getCurrentAllGroupsList();
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

    protected void addGroup(BbGroupWrapper newGroup) {
        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), newGroup);
        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.UserManagementDialog_group_added_title, MessageFormat
                    .format(Messages.UserManagementDialog_group_added_msg,
                        newGroup.getName()));
            getCurrentAllGroupsList().add(newGroup);
            groupInfoTable
                .reloadCollection(getCurrentAllGroupsList(), newGroup);
        }
    }

    @Override
    public void runAddAction() {
        addGroup(new BbGroupWrapper(SessionManager.getAppService()));
    }

    protected abstract List<BbGroupWrapper> getCurrentAllGroupsList();

}
