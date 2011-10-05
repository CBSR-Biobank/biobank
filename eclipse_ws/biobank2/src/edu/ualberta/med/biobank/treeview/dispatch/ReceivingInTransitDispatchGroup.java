package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingInTransitDispatchGroup extends AbstractDispatchGroup {

    public ReceivingInTransitDispatchGroup(AdapterBase parent, int id,
        CenterWrapper<?> center) {
        super(parent, id,
            Messages.ReceivingInTransitDispatchGroup_transit_node_label, center);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper.getInTransitReceiveDispatchCollection(center);
    }

}
