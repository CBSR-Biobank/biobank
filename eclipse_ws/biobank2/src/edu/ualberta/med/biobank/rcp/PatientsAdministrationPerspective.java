package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientsAdministrationPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.patients";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        // layout.setEditorAreaVisible(false);
    }

    public static void showOnlyPatientView() throws PartInitException {
        IWorkbenchPage activePage = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            for (IViewReference ref : activePage.getViewReferences()) {
                activePage.hideView(ref);
            }
            activePage.showView(PatientAdministrationView.ID);
        }
    }

}
