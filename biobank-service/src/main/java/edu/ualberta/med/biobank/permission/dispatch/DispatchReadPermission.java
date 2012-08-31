package edu.ualberta.med.biobank.permission.dispatch;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class DispatchReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    private Integer centerId;

    public DispatchReadPermission(Integer oiId) {
        this.dispatchId = oiId;
    }

    public DispatchReadPermission(Center center) {
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (dispatchId != null) {
            Shipment dispatch = context.get(Shipment.class, dispatchId,
                new Shipment());
            return PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
                dispatch.getReceiverCenter())
                || PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
                    dispatch.getSenderCenter());
        }
        else if (centerId != null)
            return PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
                context.load(Center.class, centerId));
        return PermissionEnum.DISPATCH_READ.isAllowed(context.getUser());
    }
}
