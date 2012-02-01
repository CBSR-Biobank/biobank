package edu.ualberta.med.biobank.common.permission.dispatch;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class DispatchReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchReadPermission(Integer oiId) {
        this.dispatchId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Dispatch dispatch = context.get(Dispatch.class, dispatchId,
            new Dispatch());
        return PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
            dispatch.getReceiverCenter())
            || PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
                dispatch.getSenderCenter());
    }
}
