package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

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
