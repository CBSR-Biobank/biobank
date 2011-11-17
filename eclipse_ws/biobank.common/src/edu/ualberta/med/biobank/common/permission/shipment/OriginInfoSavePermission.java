package edu.ualberta.med.biobank.common.permission.shipment;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

public class OriginInfoSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

    public OriginInfoSavePermission(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        OriginInfo oi = new SessionUtil(session).get(OriginInfo.class, oiId, new OriginInfo());
        return PermissionEnum.ORIGIN_INFO_UPDATE.isAllowed(user,
            oi.getReceiverSite());
    }

}
