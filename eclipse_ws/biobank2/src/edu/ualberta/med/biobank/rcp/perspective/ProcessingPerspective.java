package edu.ualberta.med.biobank.rcp.perspective;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.SessionSecurityHelper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.views.ProcessingView;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class ProcessingPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.processing"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
    }

    public static synchronized void appendRightsEnablements(
        Map<String, Map<String, List<String>>> rightsEnablements) {
        Map<String, List<String>> map = rightsEnablements.get(ID);
        if (map == null) {
            map = new LinkedHashMap<String, List<String>>();
            // FIXME not very nice when need to get the wrapper
            map.put(CollectionView.ID, Arrays
                .asList(new CollectionEventWrapper(null).getWrappedClass()
                    .getSimpleName()));
            map.put(ProcessingView.ID, Arrays.asList(
                new ProcessingEventWrapper(null).getWrappedClass()
                    .getSimpleName(), SessionSecurityHelper.SPECIMEN_LINK_KEY_DESC,
                SessionSecurityHelper.SPECIMEN_ASSIGN_KEY_DESC));
            map.put(SpecimenTransitView.ID, Arrays.asList(
                SessionSecurityHelper.DISPATCH_RECEIVE_KEY_DESC,
                SessionSecurityHelper.DISPATCH_SEND_KEY_DESC,
                SessionSecurityHelper.REQUEST_RECEIVE_DESC,
                SessionSecurityHelper.CLINIC_SHIPMENT_KEY_DESC));
            rightsEnablements.put(ID, map);
        }
    }

    public static void appendPreferredView(Map<String, String> preferredViews) {
        String view = preferredViews.get(ID);
        if (view == null) {
            preferredViews.put(ID, CollectionView.ID);
        }
    }
}
