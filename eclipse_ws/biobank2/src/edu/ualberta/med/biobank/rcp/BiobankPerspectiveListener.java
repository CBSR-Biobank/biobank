package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.utils.BindingContextHelper;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveOpened(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        //
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        BindingContextHelper.deactivateContextInWorkbench("not." //$NON-NLS-1$
            + perspective.getId());
        BindingContextHelper.activateContextInWorkbench(perspective.getId());

        SessionManager.updateViewsVisibility(page, false);
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        BindingContextHelper.deactivateContextInWorkbench(perspective.getId());
        BindingContextHelper.activateContextInWorkbench("not." //$NON-NLS-1$
            + perspective.getId());
    }
}
