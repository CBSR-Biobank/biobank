package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PatientsAdministrationPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.patients";

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    // public static void showOnlyPatientView() {
    // IWorkbenchPage activePage = PlatformUI.getWorkbench()
    // .getActiveWorkbenchWindow().getActivePage();
    // if (activePage != null
    // && activePage.findView(PatientAdministrationView.ID) == null) {
    // // do that only if the patient view is hidden:
    // if (activePage != null) {
    // for (IViewReference ref : activePage.getViewReferences()) {
    // activePage.hideView(ref);
    // }
    // try {
    // activePage.showView(PatientAdministrationView.ID);
    // } catch (PartInitException e) {
    // LOGGER.error(
    // "Failed to show Administraction View", e);
    // }
    // }
    // }
    // }

}
