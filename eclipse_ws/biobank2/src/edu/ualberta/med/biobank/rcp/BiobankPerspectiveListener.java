package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        page.closeAllEditors(true);
    }
}
