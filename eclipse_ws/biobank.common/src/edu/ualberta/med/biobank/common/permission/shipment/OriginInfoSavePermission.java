package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class OriginInfoSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private OriginInfoSaveInfo oiInfo;

    private Integer workingCenterId;

    public OriginInfoSavePermission(OriginInfoSaveInfo oiInfo,
        Integer workingCenterId) {
        this.oiInfo = oiInfo;
        this.workingCenterId = workingCenterId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return workingCenterId.equals(oiInfo.siteId)
            && PermissionEnum.ORIGIN_INFO_UPDATE.isAllowed(context.getUser(),
                context.load(Center.class, oiInfo.siteId));
    }

}
