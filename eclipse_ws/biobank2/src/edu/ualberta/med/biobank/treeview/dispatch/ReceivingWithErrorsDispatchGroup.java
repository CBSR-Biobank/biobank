package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingWithErrorsDispatchGroup extends AbstractDispatchGroup {

    public ReceivingWithErrorsDispatchGroup(AdapterBase parent, int id) {
        super(parent, id, "Errors - Not Closed");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return SiteWrapper
            .getUsersReceivingWithErrorsDispatchCollection(SessionManager
                .getAppService());
    }

}
