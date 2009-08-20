package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SiteSelectionState extends AbstractSourceProvider {

    public final static String SITE_SELECTION_STATE = "edu.ualberta.med.biobank.sourceprovider.siteSelectionState";

    private Boolean selected = false;

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentStateMap = new HashMap<String, Object>(1);
        currentStateMap.put(SITE_SELECTION_STATE, selected);
        return currentStateMap;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SITE_SELECTION_STATE };
    }

    public void setSiteSelectionState(boolean selected) {
        if (this.selected == selected)
            return; // no change
        this.selected = selected;
        fireSourceChanged(ISources.WORKBENCH, SITE_SELECTION_STATE, selected);
    }

}
