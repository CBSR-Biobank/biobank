package edu.ualberta.med.biobank.rcp.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.views.AdvancedReportsView;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.views.LoggingView;
import edu.ualberta.med.biobank.views.ReportsView;

public class ReportsPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.reports";

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static void updateVisibility(User user, IWorkbenchPage page)
        throws PartInitException {
        if (page.getPerspective().getId().equals(ID)) {
            PerspectiveUtil.updateVisibility(ReportsView.ID,
                user.canPerformActions(SecurityFeature.REPORTS), page);
            PerspectiveUtil.updateVisibility(AdvancedReportsView.ID,
                user.canPerformActions(SecurityFeature.REPORTS), page);
            PerspectiveUtil.updateVisibility(LoggingView.ID,
                user.canPerformActions(SecurityFeature.LOGGING), page);
            // want to display patient view on top
            for (IViewReference ref : page.getViewReferences()) {
                if (ref.getId().equals(CollectionView.ID)) {
                    page.bringToTop(ref.getView(false));
                }
            }
        }
    }
}
