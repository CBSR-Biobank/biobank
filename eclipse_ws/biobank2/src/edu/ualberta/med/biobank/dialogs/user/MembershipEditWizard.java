package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends Wizard {
    private final Membership originalMembership;
    private final Membership membership;
    private final ManagerContext context;

    private MembershipDomainPage domainPage;
    private MembershipPermissionsPage rolesPage;

    public MembershipEditWizard(Membership membership, ManagerContext context) {
        this.originalMembership = membership;

        this.membership = new Membership();
        copyMembership(originalMembership, membership);

        this.context = context;

        updateWindowTitle();
    }

    @Override
    public void addPages() {
        domainPage = new MembershipDomainPage(membership, context);
        rolesPage = new MembershipPermissionsPage(membership, context);

        addPage(domainPage);
        addPage(rolesPage);
    }

    @Override
    public boolean canFinish() {
        return domainPage.isPageComplete() && rolesPage.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        copyMembership(membership, originalMembership);
        return true;
    }

    /**
     * Copy the properties we plan to modify.
     * 
     * @param src source {@link Membership}
     * @param dst destination {@link Membership}
     */
    private void copyMembership(Membership src, Membership dst) {
        dst.setDomain(new Domain(src.getDomain()));

        dst.setUserManager(src.isUserManager());
        dst.setEveryPermission(src.isEveryPermission());

        dst.getPermissions().clear();
        dst.getPermissions().addAll(src.getPermissions());

        dst.getRoles().clear();
        dst.getRoles().addAll(src.getRoles());
    }

    private void updateWindowTitle() {
        String title = null;
        if (membership.isNew()) {
            title = "Add a New Membership";
        } else {
            title = "Editing a Membership";
        }
        setWindowTitle(title);
    }
}
