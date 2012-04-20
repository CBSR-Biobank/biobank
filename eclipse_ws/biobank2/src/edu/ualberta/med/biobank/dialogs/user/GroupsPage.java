package edu.ualberta.med.biobank.dialogs.user;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class GroupsPage extends BgcDialogPage {
    private static final I18n i18n = I18nFactory.getI18n(GroupsPage.class);

    private final ManagerContext context;

    private GroupInfoTable groupInfoTable;

    public GroupsPage(BgcDialogWithPages dialog, ManagerContext context) {
        super(dialog);

        this.context = context;
    }

    @SuppressWarnings("nls")
    @Override
    public String getTitle() {
        // TR: groups page title
        return i18n.tr("Groups");
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

                @SuppressWarnings("nls")
                @Override
                protected void duplicate(Group src) {
                    Group newGroup = new Group();
                    // TR: Group name copy prefix, e.g. "Copy of Admin Group"
                    String prefix = i18n.tr("Copy of ");
                    newGroup.setName(prefix + src.getName());
                    newGroup.setDescription(src.getDescription());

                    for (Membership srcMemb : src.getMemberships()) {
                        new Membership(srcMemb, newGroup);
                    }

                    addGroup(newGroup);
                }

                @Override
                protected Boolean canEdit(Group target)
                    throws ApplicationException {
                    return true;
                }

                @Override
                protected Boolean canDelete(Group target)
                    throws ApplicationException {
                    return true;
                }

                @Override
                protected Boolean canView(Group target)
                    throws ApplicationException {
                    return true;
                }
            };

        setControl(content);
    }

    @SuppressWarnings("nls")
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
                // TR: information dialog title
                i18n.tr("Group Added"),
                // TR: information dialog meessage
                i18n.tr("Successfully added new group {0}.", group.getName()));

            List<Group> allCurrent = getCurrentAllGroupsList();
            allCurrent.add(group);
            Collections.sort(allCurrent, new GroupInfoTable.GroupComparator());

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
