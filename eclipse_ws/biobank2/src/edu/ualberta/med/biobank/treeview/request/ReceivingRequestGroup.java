package edu.ualberta.med.biobank.treeview.request;

import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.request.RequestRetrievalAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingRequestGroup extends AbstractRequestGroup {

    public ReceivingRequestGroup(AdapterBase parent, int id) {
        super(parent, id, Messages.ReceivingRequestGroup_node_label);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(),
            SessionManager
                .getAppService()
                .doAction(
                    new RequestRetrievalAction(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId())).getList(),
            RequestWrapper.class);
    }

}
