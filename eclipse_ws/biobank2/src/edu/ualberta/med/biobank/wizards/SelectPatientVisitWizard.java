package edu.ualberta.med.biobank.wizards;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.wizards.pages.EnterPnumberPage;
import edu.ualberta.med.biobank.wizards.pages.SelectPatientVisitPage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import org.eclipse.jface.wizard.IWizardPage;

public class SelectPatientVisitWizard extends BiobankWizard {
    private final WritableApplicationService appService;
    private EnterPnumberPage pnumberPage;
    private SelectPatientVisitPage pvPage;
    private PatientVisitWrapper visit;

    public SelectPatientVisitWizard(WritableApplicationService appService) {
        this.appService = appService;
    }

    public PatientVisitWrapper getPatientVisit() {
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
        return getContainer().getCurrentPage() == pvPage;
    }

    @Override
    public boolean performFinish() {
        visit = pvPage.getPatientVisit();
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
                pvPage.setPatientVisitList(patient.getPatientVisitCollection(
                    true, true));
            } else {
                pnumberPage.setErrorMessage("Patient does not exist.");
                return false;
            }
        }

        return true;
    }
}
