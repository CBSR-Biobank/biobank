package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends Wizard {
    private final Membership membership;
    private final ManagerContext context;

    private MembershipDomainPage domainPage;
    private MembershipRolesPage rolesPage;

    public MembershipEditWizard(Membership membership, ManagerContext context) {
        this.membership = membership;
        this.context = context;
    }

    @Override
    public void addPages() {
        domainPage = new MembershipDomainPage(membership, context);
        rolesPage = new MembershipRolesPage(membership, context);

        addPage(domainPage);
        addPage(rolesPage);
    }

    @Override
    public boolean canFinish() {
        return domainPage.isPageComplete() && rolesPage.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        return true;
    }
}
