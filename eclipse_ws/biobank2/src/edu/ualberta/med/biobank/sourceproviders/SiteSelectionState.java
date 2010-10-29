package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteSelectionState extends AbstractSourceProvider {

    public final static String SITE_SELECTION_ID = "edu.ualberta.med.biobank.sourceprovider.siteSelectionId";
    public final static String CAN_UPDATE_SELECTED_SITE_ID = "edu.ualberta.med.biobank.sourceprovider.canUpdateSiteSelected";

    private Integer id;

    private Boolean canUpdate = false;

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentStateMap = new HashMap<String, Object>(1);
        currentStateMap.put(SITE_SELECTION_ID, id);
        currentStateMap.put(CAN_UPDATE_SELECTED_SITE_ID, canUpdate.toString());
        return currentStateMap;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SITE_SELECTION_ID, CAN_UPDATE_SELECTED_SITE_ID };
    }

    public void setSiteSelection(SiteWrapper site) {
        Integer id = null;
        if (site != null) {
            id = site.getId();
        }
        if (this.id == id) {
            return; // no change
        }
        this.id = id;
        fireSourceChanged(ISources.WORKBENCH, SITE_SELECTION_ID, id);
        canUpdate = SessionManager.getUser().canUpdateSite(id);
        fireSourceChanged(ISources.WORKBENCH, CAN_UPDATE_SELECTED_SITE_ID,
            canUpdate);
    }

}
