package edu.ualberta.med.biobank.rcp.perspective;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

public class PerspectiveUtil {
    public static void updateVisibility(String viewId, boolean show,
        IWorkbenchPage activePage) throws PartInitException {
        if (show)
            activePage.showView(viewId);
        else
            for (IViewReference ref : activePage.getViewReferences()) {
                if (viewId.equals(ref.getId())) {
                    activePage.hideView(ref);
                    return;
                }
            }
    }
}
