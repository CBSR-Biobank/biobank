package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchRetrievalAction;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingInTransitDispatchGroup extends AbstractDispatchGroup {

    public ReceivingInTransitDispatchGroup(AdapterBase parent, int id) {
        super(parent, id, DispatchState.IN_TRANSIT.getLabel());
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            SessionManager.getAppService().doAction(
                new DispatchRetrievalAction(DispatchState.IN_TRANSIT,
                    SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    false, true)).getList(), DispatchWrapper.class);
    }
}
