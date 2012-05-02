package edu.ualberta.med.biobank.wizards;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenListInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizard;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.wizards.pages.EnterCommentPage;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import edu.ualberta.med.biobank.wizards.pages.SelectCollectionEventPage;
import edu.ualberta.med.biobank.wizards.pages.SelectParentPage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ReparentingWizard extends BgcWizard {
    private static final I18n i18n = I18nFactory
        .getI18n(ReparentingWizard.class);

    private final WritableApplicationService appService;
    private EnterPnumberPage pnumberPage;
    private SelectCollectionEventPage cePage;
    private SelectParentPage parentPage;
    private EnterCommentPage commentPage;
    private CollectionEventWrapper cevent;
    private String comment;
    private SpecimenInfo specimenInfo;
    private final Specimen original;

    public ReparentingWizard(WritableApplicationService appService,
        Specimen original) {
        this.appService = appService;
        this.original = original;
    }

    public CollectionEventWrapper getCollectionEvent() {
        return cevent;
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
        return getContainer().getCurrentPage() == commentPage
            && commentPage.getComment() != null
            && !commentPage.getComment().trim().isEmpty();
    }

    @Override
    public boolean performFinish() {
        cevent = cePage.getCollectionEvent();
        specimenInfo = parentPage.getSpecimen();
        comment = commentPage.getComment();
        return cevent != null && comment != null;
    }

    @SuppressWarnings("nls")
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
                pnumberPage
                    .setErrorMessage(
                    // validation error message.
                    i18n.tr("Patient does not exist."));
                return false;
            }
        }

        else if (page == cePage) {
            CollectionEventGetSourceSpecimenListInfoAction action =
                new CollectionEventGetSourceSpecimenListInfoAction(cePage
                    .getCollectionEvent().getId());
            try {
                List<SpecimenInfo> specs = SessionManager.getAppService()
                    .doAction(action).getList();
                for (SpecimenInfo spec : specs)
                    if (spec.specimen.equals(original)) {
                        specs.remove(spec);
                        break;
                    }
                parentPage.setParentSpecimenList(specs);
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    // dialog message.
                    i18n.tr("Unable to retrieve specimens"));
                return false;
            }
        }

        return true;
    }

    public String getComment() {
        return comment;
    }

    public SpecimenInfo getSpecimen() {
        return specimenInfo;
    }
}
