package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.IWizardPage;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizard;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends BgcWizard {
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

    @Override
    public boolean performNext(IWizardPage page) {
        return true;
    }
}
