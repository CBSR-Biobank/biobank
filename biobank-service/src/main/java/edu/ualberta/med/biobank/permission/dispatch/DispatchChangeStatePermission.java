package edu.ualberta.med.biobank.permission.dispatch;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

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
        return (!DispatchState.CREATION.equals(dispatch.getState())
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
            dispatch.getReceiverCenter()))
            || (DispatchState.CREATION.equals(dispatch.getState())
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
                dispatch.getSenderCenter()));
    }

}
