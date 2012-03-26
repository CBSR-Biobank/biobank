package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.wizard.IWizardPage;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizard;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.wizards.pages.EnterCommentPage;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import edu.ualberta.med.biobank.wizards.pages.SelectCollectionEventPage;
import edu.ualberta.med.biobank.wizards.pages.SelectParentPage;

public class MembershipEditWizard extends BgcWizard {
    private EnterPnumberPage pnumberPage;
    private SelectCollectionEventPage cePage;
    private SelectParentPage parentPage;
    private EnterCommentPage commentPage;
    private CollectionEventWrapper cevent;

    private final Membership membership;
    private final ManagerContext context;

    public MembershipEditWizard(Membership membership, ManagerContext context) {
        this.membership = membership;
        this.context = context;
    }

    @Override
    public void addPages() {
        pnumberPage = new EnterPnumberPage();
        cePage = new SelectCollectionEventPage();
        parentPage = new SelectParentPage();
        commentPage = new EnterCommentPage();
        addPage(pnumberPage);
        addPage(cePage);
        addPage(parentPage);
        addPage(commentPage);
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
