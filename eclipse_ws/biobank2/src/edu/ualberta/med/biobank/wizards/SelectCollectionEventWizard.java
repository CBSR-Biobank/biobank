package edu.ualberta.med.biobank.wizards;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.wizards.pages.EnterCommentPage;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import edu.ualberta.med.biobank.wizards.pages.SelectCollectionEventPage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import org.eclipse.jface.wizard.IWizardPage;

public class SelectCollectionEventWizard extends BiobankWizard {
    private final WritableApplicationService appService;
    private EnterPnumberPage pnumberPage;
    private SelectCollectionEventPage cePage;
    private EnterCommentPage commentPage;
    private CollectionEventWrapper cevent;
    private String comment;

    public SelectCollectionEventWizard(WritableApplicationService appService) {
        this.appService = appService;
    }

    public CollectionEventWrapper getCollectionEvent() {
        return cevent;
    }

    @Override
    public void addPages() {
        pnumberPage = new EnterPnumberPage();
        cePage = new SelectCollectionEventPage();
        commentPage = new EnterCommentPage();
        addPage(pnumberPage);
        addPage(cePage);
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
        cevent = cePage.getCollectionEvent();
        comment = commentPage.getComment();
        return cevent != null && comment != null;
    }

    @Override
    public boolean performNext(IWizardPage page) {
        if (page == pnumberPage) {
            PatientWrapper patient = null;

            if (pnumberPage.getPnumber() != null) {
                try {
                    patient = PatientWrapper.getPatient(appService,
                        pnumberPage.getPnumber());
                } catch (ApplicationException e) {
                }
            }

            if (patient != null) {
                cePage.setCollectionEventList(patient
                    .getCollectionEventCollection(true));
            } else {
                pnumberPage.setErrorMessage("Patient does not exist.");
                return false;
            }
        }

        return true;
    }

    public String getComment() {
        return comment;
    }
}
