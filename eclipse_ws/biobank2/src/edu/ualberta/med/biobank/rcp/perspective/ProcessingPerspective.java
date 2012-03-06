package edu.ualberta.med.biobank.rcp.perspective;

import java.util.Map;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.views.CollectionView;

public class ProcessingPerspective implements IPerspectiveFactory {

    public static final String ID =
        "edu.ualberta.med.biobank.perspective.processing"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static void appendPreferredView(Map<String, String> preferredViews) {
        String view = preferredViews.get(ID);
        if (view == null) {
            preferredViews.put(ID, CollectionView.ID);
        }
    }
}
