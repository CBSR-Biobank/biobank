package edu.ualberta.med.biobank.treeview.request;

import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.request.AbstractRequestGroup;

public class ReceivingRequestGroup extends AbstractRequestGroup {

    public ReceivingRequestGroup(AdapterBase parent, int id,
        CenterWrapper<?> center) {
        super(parent, id, "Pending Requests", center);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return CenterWrapper.getRequestCollection(
            SessionManager.getAppService(), center);
    }

}
