package edu.ualberta.med.biobank.common.permission.dispatch;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class DispatchChangeStatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchChangeStatePermission(Integer oiId) {
        this.dispatchId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Dispatch dispatch = context.load(Dispatch.class, dispatchId);
        User user = context.getUser();
        return (!DispatchState.getState(dispatch.getState()).equals(
            DispatchState.CREATION)
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
            dispatch.getReceiverCenter()))
            || (DispatchState.getState(dispatch.getState()).equals(
                DispatchState.CREATION)
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
                dispatch.getSenderCenter()));
    }

}
