package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.OriginInfo;

public class OriginInfoSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

	private Integer workingCenterId;

    public OriginInfoSavePermission(Integer oiId, Integer workingCenterId) {
        this.oiId = oiId;
        this.workingCenterId=workingCenterId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        OriginInfo oi = context.get(OriginInfo.class, oiId, new OriginInfo());
        return workingCenterId.equals(oi.getReceiverSite().getId()) && PermissionEnum.ORIGIN_INFO_UPDATE.isAllowed(context.getUser(),
            oi.getReceiverSite());
    }

}
