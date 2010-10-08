package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        AbstractViewWithAdapterTree view = SessionManager
            .getCurrentAdapterViewWithTree();
        if (view != null) {
            view.reload();
            SessionManager.getInstance().getSiteCombo()
                .updateStatusLineMessage(view.getSite());
        }
    }
}
