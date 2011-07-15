package edu.ualberta.med.biobank.rcp.perspective;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.views.ProcessingView;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class ProcessingPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.processing"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static synchronized void appendFeatureEnablements(
        Map<String, Map<String, List<SecurityFeature>>> featureEnablements) {
        Map<String, List<SecurityFeature>> map = featureEnablements.get(ID);
        if (map == null) {
            map = new LinkedHashMap<String, List<SecurityFeature>>();
            map.put(CollectionView.ID,
                Arrays.asList(SecurityFeature.COLLECTION_EVENT));
            map.put(ProcessingView.ID, Arrays.asList(
                SecurityFeature.PROCESSING_EVENT, SecurityFeature.LINK,
                SecurityFeature.ASSIGN));
            map.put(SpecimenTransitView.ID, Arrays.asList(
                SecurityFeature.DISPATCH_REQUEST,
                SecurityFeature.CLINIC_SHIPMENT));
            featureEnablements.put(ID, map);
        }
    }

    public static void appendPreferredView(Map<String, String> preferredViews) {
        String view = preferredViews.get(ID);
        if (view == null) {
            preferredViews.put(ID, CollectionView.ID);
        }
    }
}
