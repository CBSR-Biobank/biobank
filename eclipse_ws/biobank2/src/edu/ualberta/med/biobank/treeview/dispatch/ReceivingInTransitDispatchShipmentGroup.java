package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingInTransitDispatchShipmentGroup extends
    AbstractDispatchShipmentGroup {

    public ReceivingInTransitDispatchShipmentGroup(AdapterBase parent, int id) {
        super(parent, id, "In transit");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper site = SessionManager.getCurrentSite();
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            return site.getInTransitReceiveDispatchShipmentCollection();
        }
        return new ArrayList<ModelWrapper<?>>();
    }

}
