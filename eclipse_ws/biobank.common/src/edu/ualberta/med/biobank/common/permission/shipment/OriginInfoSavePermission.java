package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.OriginInfo;

public class OriginInfoSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

    public OriginInfoSavePermission(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        OriginInfo oi = context.get(OriginInfo.class, oiId, new OriginInfo());
        return PermissionEnum.ORIGIN_INFO_UPDATE.isAllowed(context.getUser(),
            oi.getReceiverSite());
    }

}
