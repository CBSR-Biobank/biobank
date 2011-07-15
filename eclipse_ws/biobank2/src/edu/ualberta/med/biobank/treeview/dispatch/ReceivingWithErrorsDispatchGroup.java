package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingWithErrorsDispatchGroup extends AbstractDispatchGroup {

    public ReceivingWithErrorsDispatchGroup(AdapterBase parent, int id,
        CenterWrapper<?> center) {
        super(parent, id, Messages.ReceivingWithErrorsDispatchGroup_error_node_label, center);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper.getReceivingWithErrorsDispatchCollection(center);
    }

}
