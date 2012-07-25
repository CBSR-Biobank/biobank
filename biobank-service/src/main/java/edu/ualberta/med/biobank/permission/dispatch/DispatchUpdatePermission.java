package edu.ualberta.med.biobank.permission.dispatch;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class DispatchUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchUpdatePermission(Integer id) {
        this.dispatchId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Dispatch dispatch =
            context.get(Dispatch.class, dispatchId, new Dispatch());
        return PermissionEnum.DISPATCH_UPDATE.isAllowed(context.getUser(),
            dispatch.getReceiverCenter())
            || PermissionEnum.DISPATCH_UPDATE.isAllowed(context.getUser(),
                dispatch.getSenderCenter());
    }

}
