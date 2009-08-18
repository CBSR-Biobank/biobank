package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        if (perspective.getId().equals(PatientsAdministrationPerspective.ID)) {
            // close all the editors opened in the patient perspective when the
            // perspective is left
            page.closeAllEditors(true);
        }
    }
}
