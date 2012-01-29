package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ShipmentDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;

    public ShipmentDeletePermission(Integer shipId) {
        this.shipmentId = shipId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        OriginInfo ship = context.load(OriginInfo.class, shipmentId);
        return PermissionEnum.ORIGIN_INFO_DELETE.isAllowed(context.getUser(),
            ship.getCenter());
    }
}
