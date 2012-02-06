package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class OriginInfoSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private OriginInfoSaveInfo oiInfo;

    public OriginInfoSavePermission(OriginInfoSaveInfo oiInfo) {
        this.oiInfo = oiInfo;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.ORIGIN_INFO_UPDATE.isAllowed(context.getUser(),
            context.load(Center.class, oiInfo.siteId));
    }

}
