package edu.ualberta.med.biobank.treeview.request;

import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingRequestGroup extends AbstractRequestGroup {

    public ReceivingRequestGroup(AdapterBase parent, int id,
        CenterWrapper<?> center) {
        super(parent, id, Messages.ReceivingRequestGroup_node_label, center);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return CenterWrapper.getRequestCollection(
            SessionManager.getAppService(), center);
    }

}
