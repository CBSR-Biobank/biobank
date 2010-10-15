package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SentInTransitDispatchShipmentGroup extends
    AbstractDispatchShipmentGroup {

    public SentInTransitDispatchShipmentGroup(AdapterBase parent, int id) {
        super(parent, id, "In transit");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper site = SessionManager.getCurrentSite();
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            return site.getInTransitSentDispatchShipmentCollection();
        }
        return new ArrayList<ModelWrapper<?>>();
    }

}
