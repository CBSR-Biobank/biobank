package edu.ualberta.med.biobank.rcp.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.views.ProcessingView;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class ProcessingPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.processing";

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static void updateVisibility(User user, IWorkbenchPage page)
        throws PartInitException {
        if (page.getPerspective().getId().equals(ID)) {
            PerspectiveUtil.updateVisibility(CollectionView.ID,
                user.canPerformActions(SecurityFeature.COLLECTION_EVENT), page);
            PerspectiveUtil.updateVisibility(ProcessingView.ID, user
                .canPerformActions(SecurityFeature.PROCESSING_EVENT, SecurityFeature.LINK,
                    SecurityFeature.ASSIGN), page);
            PerspectiveUtil.updateVisibility(SpecimenTransitView.ID, user
                .canPerformActions(SecurityFeature.DISPATCH_REQUEST,
                    SecurityFeature.CLINIC_SHIPMENT), page);
            // want to display patient view on top
            for (IViewReference ref : page.getViewReferences()) {
                if (ref.getId().equals(CollectionView.ID)) {
                    page.bringToTop(ref.getView(false));
                }
            }
        }
    }

}
