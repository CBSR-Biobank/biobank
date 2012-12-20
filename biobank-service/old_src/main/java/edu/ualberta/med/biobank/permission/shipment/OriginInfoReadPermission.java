package edu.ualberta.med.biobank.permission.shipment;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class OriginInfoReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

    private Integer centerId;

    public OriginInfoReadPermission(Integer oiId) {
        this.oiId = oiId;
    }

    public OriginInfoReadPermission(Center center) {
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        User user = context.getUser();
        if (oiId != null) {
            OriginInfo oi = context.load(OriginInfo.class, oiId);
            return PermissionEnum.ORIGIN_INFO_READ.isAllowed(user,
                oi.getReceiverCenter())
                || PermissionEnum.ORIGIN_INFO_READ.isAllowed(user,
                    oi.getCenter());
        }
        else if (centerId != null)
            return PermissionEnum.ORIGIN_INFO_READ.isAllowed(user,
                context.load(Center.class, centerId));
        return PermissionEnum.ORIGIN_INFO_READ.isAllowed(user);
    }

}
