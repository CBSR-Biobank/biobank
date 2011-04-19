package edu.ualberta.med.biobank.wizards;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import edu.ualberta.med.biobank.wizards.pages.SelectPatientVisitPage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import org.eclipse.jface.wizard.IWizardPage;

public class SelectCollectionEventWizard extends BiobankWizard {
    private final WritableApplicationService appService;
    private EnterPnumberPage pnumberPage;
    private SelectPatientVisitPage pvPage;
    private CollectionEventWrapper visit;

    public SelectCollectionEventWizard(WritableApplicationService appService) {
        this.appService = appService;
    }

    public CollectionEventWrapper getCollectionEvent() {
        return visit;
    }

    @Override
    public void addPages() {
        pnumberPage = new EnterPnumberPage();
        pvPage = new SelectPatientVisitPage();

        addPage(pnumberPage);
        addPage(pvPage);
    }

    @Override
    public boolean canFinish() {
        return getContainer().getCurrentPage() == pvPage
            && pvPage.getCollectionEvent() != null;
    }

    @Override
    public boolean performFinish() {
        visit = pvPage.getCollectionEvent();
        return visit != null;
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
                pvPage.setCollectionEventList(patient
                    .getCollectionEventCollection(true));
            } else {
                pnumberPage.setErrorMessage("Patient does not exist.");
                return false;
            }
        }

        return true;
    }
}
