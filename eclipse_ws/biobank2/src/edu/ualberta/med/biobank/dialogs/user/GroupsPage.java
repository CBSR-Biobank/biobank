package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.GroupGetOutput;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContextGetAction;
import edu.ualberta.med.biobank.common.action.security.MembershipContextGetInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.widgets.infotables.GroupInfoTable;

public abstract class GroupsPage extends BgcDialogPage {

    private final ManagerContext context;

    private GroupInfoTable groupInfoTable;

    public GroupsPage(BgcDialogWithPages dialog, ManagerContext context) {
        super(dialog);

        this.context = context;
    }

    @Override
    public String getTitle() {
        return Messages.GroupsPage_page_title;
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
                return getCurrentAllGroupsList();
            }

            @Override
            public void setFilteredList(List<Group> filteredObjects) {
                groupInfoTable.setList(filteredObjects);
            }
        };

        groupInfoTable =
            new GroupInfoTable(content, context.getGroups(), context) {
                @Override
                protected boolean deleteGroup(Group group) {
                    boolean deleted = super.deleteGroup(group);
                    if (deleted)
                        getCurrentAllGroupsList().remove(group);
                    return deleted;
                }

                @Override
                protected void duplicate(Group src) {
                    Group newGroup = new Group();
                    newGroup.setName("Copy of " + src.getName());
                    newGroup.setDescription(src.getDescription());

                    for (Membership srcMemb : src.getMemberships()) {
                        Membership newMemb = new Membership();
                        newMemb.setDomain(srcMemb.getDomain());

                        newMemb.setEveryPermission(srcMemb.isEveryPermission());
                        newMemb.setUserManager(srcMemb.isUserManager());

                        newMemb.getRoles().addAll(srcMemb.getRoles());
                        newMemb.getPermissions().addAll(
                            srcMemb.getPermissions());

                        newMemb.setPrincipal(newGroup);
                        newGroup.getMemberships().add(newMemb);
                    }

                    newGroup.setName("CopyOf" + newGroup.getName()); //$NON-NLS-1$
                    addGroup(newGroup);
                }
            };

        setControl(content);
    }

    protected void addGroup(Group group) {
        MembershipContext membershipContext = null;

        try {
            membershipContext =
                SessionManager
                    .getAppService()
                    .doAction(
                        new MembershipContextGetAction(
                            new MembershipContextGetInput())).getContext();
        } catch (Throwable t) {
            TmpUtil.displayException(t);
            return;
        }

        GroupGetOutput output = new GroupGetOutput(group, membershipContext);

        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), output, context);

        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.UserManagementDialog_group_added_title, MessageFormat
                    .format(Messages.UserManagementDialog_group_added_msg,
                        group.getName()));

            getCurrentAllGroupsList().add(group);

            groupInfoTable.reload();
            groupInfoTable.setSelection(group);
        }
    }

    @Override
    public void runAddAction() {
        addGroup(new Group());
    }

    protected abstract List<Group> getCurrentAllGroupsList();
}
