package edu.ualberta.med.biobank.common.permission.dispatch;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

public class DispatchChangeStatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchChangeStatePermission(Integer oiId) {
        this.dispatchId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Dispatch dispatch = ActionUtil.sessionGet(session, Dispatch.class, dispatchId);
        return (!DispatchState.getState(dispatch.getState()).equals(DispatchState.CREATION)
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
            dispatch.getReceiverCenter()))
            || (DispatchState.getState(dispatch.getState()).equals(DispatchState.CREATION)
                && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user, dispatch.getSenderCenter()));
    }

}
