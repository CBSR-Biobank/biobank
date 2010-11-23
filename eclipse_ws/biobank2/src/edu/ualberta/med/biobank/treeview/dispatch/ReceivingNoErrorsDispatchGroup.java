package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingNoErrorsDispatchGroup extends AbstractDispatchGroup {

    public ReceivingNoErrorsDispatchGroup(AdapterBase parent, int id,
        SiteWrapper site) {
        super(parent, id, "Receiving", site);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper.getReceivingNoErrorsDispatchCollection(site);
    }

}
