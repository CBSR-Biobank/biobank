package edu.ualberta.med.biobank.rcp.perspective;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.views.AdvancedReportsView;
import edu.ualberta.med.biobank.views.LoggingView;
import edu.ualberta.med.biobank.views.ReportsView;

public class ReportsPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.reports"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static synchronized void appendFeatureEnablements(
        Map<String, Map<String, List<SecurityFeature>>> featureEnablements) {
        Map<String, List<SecurityFeature>> map = featureEnablements.get(ID);
        if (map == null) {
            map = new LinkedHashMap<String, List<SecurityFeature>>();
            map.put(ReportsView.ID, Arrays.asList(SecurityFeature.REPORTS));
            map.put(AdvancedReportsView.ID,
                Arrays.asList(SecurityFeature.REPORTS));
            map.put(LoggingView.ID, Arrays.asList(SecurityFeature.LOGGING));
            featureEnablements.put(ID, map);
        }
    }

    public static void appendPreferredView(Map<String, String> preferredViews) {
        String view = preferredViews.get(ID);
        if (view == null) {
            preferredViews.put(ID, ReportsView.ID);
        }
    }
}
