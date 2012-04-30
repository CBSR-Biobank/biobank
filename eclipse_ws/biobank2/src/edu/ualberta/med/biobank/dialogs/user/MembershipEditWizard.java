package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends Wizard {
    private final Membership originalMembership;
    private final Membership membership;
    private final ManagerContext context;

    private MembershipDomainPage domainPage;
    private MembershipPermissionsPage permsPage;

    public MembershipEditWizard(Membership membership, ManagerContext context) {
        this.originalMembership = membership;

        this.membership = new Membership();
        copyMembership(originalMembership, this.membership);

        this.context = context;

        updateWindowTitle();
    }

    @Override
    public void addPages() {
        domainPage = new MembershipDomainPage(membership, context);
        permsPage = new MembershipPermissionsPage(membership, context);

        addPage(domainPage);
        addPage(permsPage);
    }

    @Override
    public boolean canFinish() {
        boolean onLastPage = getContainer() != null && permsPage != null
            && permsPage.equals(getContainer().getCurrentPage());
        boolean hasPerms = !membership.getAllPermissions().isEmpty();
        return onLastPage && hasPerms;
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
        dst.setId(src.getId()); // necessary for isNew()

        dst.getDomain().setAllCenters(src.getDomain().isAllCenters());
        dst.getDomain().getCenters().clear();
        dst.getDomain().getCenters().addAll(src.getDomain().getCenters());

        dst.getDomain().setAllStudies(src.getDomain().isAllStudies());
        dst.getDomain().getStudies().clear();
        dst.getDomain().getStudies().addAll(src.getDomain().getStudies());

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
            title = "Add Membership";
        } else {
            title = "Edit Membership";
        }
        setWindowTitle(title);
    }
}
