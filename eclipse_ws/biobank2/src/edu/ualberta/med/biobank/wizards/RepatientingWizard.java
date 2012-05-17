package edu.ualberta.med.biobank.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizard;
import edu.ualberta.med.biobank.wizards.pages.EnterCommentPage;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RepatientingWizard extends BgcWizard {
    private static final I18n i18n = I18nFactory
        .getI18n(RepatientingWizard.class);

    private final WritableApplicationService appService;
    private EnterPnumberPage pnumberPage;
    private EnterCommentPage commentPage;
    private String comment;

    private PatientWrapper patient;

    public RepatientingWizard(WritableApplicationService appService) {
        this.appService = appService;
    }

    public PatientWrapper getPatient() {
        return patient;
    }

    @Override
    public void addPages() {
        pnumberPage = new EnterPnumberPage();
        commentPage = new EnterCommentPage();
        addPage(pnumberPage);
        addPage(commentPage);
    }

    @Override
    public boolean canFinish() {
        return getContainer().getCurrentPage() == commentPage
            && commentPage.getComment() != null
            && !commentPage.getComment().trim().isEmpty();
    }

    @Override
    public boolean performFinish() {
        comment = commentPage.getComment();
        return patient != null && comment != null;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean performNext(IWizardPage page) {
        if (page == pnumberPage) {

            if (pnumberPage.getPnumber() != null) {
                try {
                    patient = PatientWrapper.getPatient(appService,
                        pnumberPage.getPnumber());
                } catch (ApplicationException e) {
                }
            }

            if (patient == null) {
                pnumberPage
                    .setErrorMessage(
                    // validation error message.
                    i18n.tr("Patient does not exist."));
                return false;
            }
        }

        return true;
    }

    public String getComment() {
        return comment;
    }

}
