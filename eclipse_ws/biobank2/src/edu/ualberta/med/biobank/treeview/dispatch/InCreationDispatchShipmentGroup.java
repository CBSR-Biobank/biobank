package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class InCreationDispatchShipmentGroup extends
    AbstractDispatchShipmentGroup {

    public InCreationDispatchShipmentGroup(AdapterBase parent, int id) {
        super(parent, id, "Creation");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            site.reload();
            return site.getInCreationDispatchShipmentCollection();
        }
        return new ArrayList<ModelWrapper<?>>();
    }

}
