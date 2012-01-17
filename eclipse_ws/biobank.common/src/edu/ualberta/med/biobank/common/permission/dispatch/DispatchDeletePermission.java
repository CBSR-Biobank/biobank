package edu.ualberta.med.biobank.common.permission.dispatch;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;

public class DispatchDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;

    public DispatchDeletePermission(Integer shipId) {
        this.shipmentId = shipId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Dispatch ship = context.load(Dispatch.class, shipmentId);
        if (DispatchState.getState(ship.getState()).equals(
            DispatchState.CREATION))
            return PermissionEnum.DISPATCH_DELETE.isAllowed(context.getUser(),
                ship.getSenderCenter());
        return false;
    }
}
