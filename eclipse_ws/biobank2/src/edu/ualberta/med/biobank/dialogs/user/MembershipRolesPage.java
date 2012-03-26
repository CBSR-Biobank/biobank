package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipRolesPage extends BgcWizardPage {
    private final Membership membership;
    private final ManagerContext context;

    MembershipRolesPage(Membership membership, ManagerContext context) {
        super("Roles and Permissions",
            "Select roles and permissions for this membership", null);

        this.membership = membership;
        this.context = context;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        // TODO Auto-generated method stub

    }
}
