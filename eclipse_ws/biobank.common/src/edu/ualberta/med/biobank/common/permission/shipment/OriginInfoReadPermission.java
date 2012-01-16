package edu.ualberta.med.biobank.common.permission.shipment;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

public class OriginInfoReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

    public OriginInfoReadPermission(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        OriginInfo oi =
            new ActionContext(user, session).load(OriginInfo.class, oiId);
        return PermissionEnum.ORIGIN_INFO_READ.isAllowed(user,
            oi.getReceiverSite())
            || PermissionEnum.ORIGIN_INFO_READ.isAllowed(user, oi.getCenter());
    }

}
