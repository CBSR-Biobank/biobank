package edu.ualberta.med.biobank.permission.dispatch;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Shipment;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.type.ShipmentState;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class DispatchChangeStatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchChangeStatePermission(Integer oiId) {
        this.dispatchId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Shipment dispatch = context.load(Shipment.class, dispatchId);
        User user = context.getUser();
        return (!ShipmentState.PACKED.equals(dispatch.getState())
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
            dispatch.getReceiverCenter()))
            || (ShipmentState.PACKED.equals(dispatch.getState())
            && PermissionEnum.DISPATCH_CHANGE_STATE.isAllowed(user,
                dispatch.getSenderCenter()));
    }

}
