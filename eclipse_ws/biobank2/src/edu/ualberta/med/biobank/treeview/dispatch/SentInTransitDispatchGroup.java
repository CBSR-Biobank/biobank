package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SentInTransitDispatchGroup extends AbstractDispatchGroup {

    public SentInTransitDispatchGroup(AdapterBase parent, int id,
        CenterWrapper<?> center) {
        super(parent, id, Messages.SentInTransitDispatchGroup_sent_node_label, center);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper.getInTransitSentDispatchCollection(center);
    }
}
