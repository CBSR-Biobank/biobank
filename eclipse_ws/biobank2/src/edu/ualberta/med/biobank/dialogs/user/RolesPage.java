package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.widgets.infotables.RoleInfoTable;

public abstract class RolesPage extends BgcDialogPage {

    private RoleInfoTable roleInfoTable;

    public RolesPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return Messages.RolesPage_title;
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<RoleWrapper>(content) {
            @Override
            protected boolean accept(RoleWrapper role, String text) {
                return contains(role.getName(), text);
            }

            @Override
            public List<RoleWrapper> getAllCollection() {
                return getCurrentAllRolesList();
            }

            @Override
            public void setFilteredList(List<RoleWrapper> filteredObjects) {
                roleInfoTable.reloadCollection(filteredObjects);
            }
        };

        roleInfoTable = new RoleInfoTable(content, null) {
            @Override
            protected boolean deleteRole(RoleWrapper role) {
                boolean deleted = super.deleteRole(role);
                if (deleted)
                    getCurrentAllRolesList().remove(role);
                return deleted;
            }

            @Override
            protected void duplicate(RoleWrapper origRole) {
                RoleWrapper newRole = origRole.duplicate();
                newRole.setName("CopyOf" + newRole.getName()); //$NON-NLS-1$
                addRole(newRole);
            }
        };
        roleInfoTable.setList(getCurrentAllRolesList());
        setControl(content);
    }

    protected abstract List<RoleWrapper> getCurrentAllRolesList();

    @Override
    public void runAddAction() {
        addRole(new RoleWrapper(SessionManager.getAppService()));
    }

    protected void addRole(RoleWrapper newRole) {
        RoleEditDialog dlg = new RoleEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), newRole);
        int res = dlg.open();
        if (res == Status.OK) {
            BgcPlugin.openAsyncInformation(
                Messages.RolesPage_role_added_title,
                MessageFormat.format(Messages.RolesPage_role_added_msg,
                    newRole.getName()));
            getCurrentAllRolesList().add(newRole);
            roleInfoTable.reloadCollection(getCurrentAllRolesList(), newRole);
        }
    }
}
