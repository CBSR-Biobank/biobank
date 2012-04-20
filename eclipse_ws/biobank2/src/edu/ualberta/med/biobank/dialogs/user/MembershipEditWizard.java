package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.Wizard;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipEditWizard extends Wizard {
    private static final I18n i18n = I18nFactory
        .getI18n(MembershipEditWizard.class);

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

    @SuppressWarnings("nls")
    private void updateWindowTitle() {
        String title = null;
        if (membership.isNew()) {
            // TR: add membership wizard title
            title = i18n.tr("Add Membership");
        } else {
            // TR: edit membership wizard title
            title = i18n.tr("Edit Membership");
        }
        setWindowTitle(title);
    }
}
