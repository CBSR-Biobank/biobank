package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
        groupInfoTable.setCollection(getCurrentAllGroupsList());
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
