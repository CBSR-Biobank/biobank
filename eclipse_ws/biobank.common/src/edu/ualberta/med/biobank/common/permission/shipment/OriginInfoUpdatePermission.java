package edu.ualberta.med.biobank.common.permission.shipment;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;

public class OriginInfoUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer oiId;

    public OriginInfoUpdatePermission(Integer id) {
        this.oiId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return false;
    }

}
