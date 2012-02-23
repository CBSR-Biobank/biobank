package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ShippingMethodPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = -2626364132765997198L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.ADMINISTRATION.isAllowed(context.getUser());
    }

}
