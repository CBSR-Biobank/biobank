package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ShipmentDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;
    private Integer workingCenter;

    public ShipmentDeletePermission(Integer shipId, Integer workingCenter) {
        this.shipmentId = shipId;
        this.workingCenter = workingCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (shipmentId != null) {
            OriginInfo ship = null;
            try {
                ship = context.load(OriginInfo.class, shipmentId);
                return PermissionEnum.ORIGIN_INFO_DELETE.isAllowed(
                    context.getUser(),
                    ship.getReceiverSite())
                    && PermissionEnum.ORIGIN_INFO_CREATE.isAllowed(
                        context.getUser(),
                        context.load(Center.class, workingCenter));
            } catch (ModelNotFoundException e) {
                return false;
            }
        }
        return false;
    }
}
