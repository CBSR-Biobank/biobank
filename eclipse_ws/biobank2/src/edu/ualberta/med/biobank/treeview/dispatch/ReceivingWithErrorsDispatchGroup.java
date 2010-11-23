package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingWithErrorsDispatchGroup extends AbstractDispatchGroup {

    public ReceivingWithErrorsDispatchGroup(AdapterBase parent, int id,
        SiteWrapper site) {
        super(parent, id, "Errors - Not Closed", site);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper.getReceivingWithErrorsDispatchCollection(site);
    }

}
