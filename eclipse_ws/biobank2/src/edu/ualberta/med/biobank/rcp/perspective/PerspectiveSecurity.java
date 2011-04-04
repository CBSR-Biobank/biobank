package edu.ualberta.med.biobank.rcp.perspective;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;

public class PerspectiveSecurity {

    /**
     * perspectiveId=[View=List of features]
     */
    private static Map<String, Map<String, List<SecurityFeature>>> featureEnablements;

    private static Map<String, String> preferredViews;

    static {
        featureEnablements = new LinkedHashMap<String, Map<String, List<SecurityFeature>>>();
        ProcessingPerspective.appendFeatureEnablements(featureEnablements);
        ReportsPerspective.appendFeatureEnablements(featureEnablements);
        preferredViews = new HashMap<String, String>();
        ProcessingPerspective.appendPreferredView(preferredViews);
        ReportsPerspective.appendPreferredView(preferredViews);
    }

    public static synchronized void updateVisibility(User user,
        IWorkbenchPage page) throws PartInitException {
        String perspectiveId = page.getPerspective().getId();
        Map<String, List<SecurityFeature>> map = featureEnablements
            .get(perspectiveId);
        if (map != null) {
            for (Entry<String, List<SecurityFeature>> entry : map.entrySet()) {
                updateVisibility(entry.getKey(),
                    user.canPerformActions(entry.getValue()), page);
            }
            // want to display preferred view on top
            String preferredView = preferredViews.get(perspectiveId);
            if (preferredView != null)
                for (IViewReference ref : page.getViewReferences()) {
                    if (ref.getId().equals(preferredView)) {
                        page.bringToTop(ref.getView(false));
                    }
                }
        }
    }

    private static void updateVisibility(String viewId, boolean show,
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
