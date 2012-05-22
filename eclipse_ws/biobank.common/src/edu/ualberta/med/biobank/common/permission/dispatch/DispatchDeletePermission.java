package edu.ualberta.med.biobank.common.permission.dispatch;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;

    public DispatchDeletePermission(Integer shipId) {
        this.shipmentId = shipId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (shipmentId != null) {
            Dispatch ship = null;
            try {
                ship = context.load(Dispatch.class, shipmentId);
            } catch (ModelNotFoundException e) {
                return false;
            }
            if (DispatchState.CREATION.equals(ship.getState()))
                return PermissionEnum.DISPATCH_DELETE.isAllowed(
                    context.getUser(),
                    ship.getSenderCenter());
        }
        return false;
    }
}
