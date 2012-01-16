package edu.ualberta.med.biobank.common.permission.shipment;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

public class ShipmentDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;

    public ShipmentDeletePermission(Integer shipId) {
        this.shipmentId = shipId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        OriginInfo ship =
            new ActionContext(user, session).load(OriginInfo.class, shipmentId);
        return PermissionEnum.ORIGIN_INFO_DELETE.isAllowed(user,
            ship.getCenter());
    }
}
