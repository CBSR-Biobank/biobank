package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.views.AbstractViewWithTree;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        AbstractViewWithTree view = SessionManager.getCurrentViewWithTree();
        if (view != null)
            view.reload();
    }
}
