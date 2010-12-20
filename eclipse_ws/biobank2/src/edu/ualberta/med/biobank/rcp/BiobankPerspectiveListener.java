package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.rcp.perspective.MainPerspective;
import edu.ualberta.med.biobank.rcp.perspective.ReportsPerspective;
import edu.ualberta.med.biobank.utils.BindingContextHelper;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveOpened(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        AbstractViewWithAdapterTree view = SessionManager
            .getCurrentAdapterViewWithTree();
        if (view != null && !perspective.getId().equals(MainPerspective.ID)
            && !perspective.getId().equals(ReportsPerspective.ID)) {
            view.reload();
        }
        BindingContextHelper.deactivateContextInWorkbench("not."
            + perspective.getId());
        BindingContextHelper.activateContextInWorkbench(perspective.getId());
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        BindingContextHelper.deactivateContextInWorkbench(perspective.getId());
        BindingContextHelper.activateContextInWorkbench("not."
            + perspective.getId());
    }
}
