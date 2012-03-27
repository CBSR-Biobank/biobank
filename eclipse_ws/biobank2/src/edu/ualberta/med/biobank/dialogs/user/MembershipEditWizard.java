package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends Wizard {
    private final Membership membership;
    private final ManagerContext context;

    public MembershipEditWizard(Membership membership, ManagerContext context) {
        this.membership = membership;
        this.context = context;
    }

    @Override
    public void addPages() {
        addPage(new MembershipDomainPage(membership, context));
        addPage(new MembershipRolesPage(membership, context));
    }

    @Override
    public boolean canFinish() {
        return true;
    }

    @Override
    public boolean performFinish() {
        return true;
    }
}
