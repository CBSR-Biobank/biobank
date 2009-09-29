package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.model.Site;

public class SiteSelectionState extends AbstractSourceProvider {

    public final static String SITE_SELECTION_ID = "edu.ualberta.med.biobank.sourceprovider.siteSelectionId";

    private Integer id;

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentStateMap = new HashMap<String, Object>(1);
        currentStateMap.put(SITE_SELECTION_ID, id);
        return currentStateMap;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SITE_SELECTION_ID };
    }

    public void setSiteSelection(Site site) {
        Integer id = null;
        if (site != null) {
            id = site.getId();
        }
        if (this.id == id) {
            return; // no change
        }
        this.id = id;
        fireSourceChanged(ISources.WORKBENCH, SITE_SELECTION_ID, id);
    }

}
