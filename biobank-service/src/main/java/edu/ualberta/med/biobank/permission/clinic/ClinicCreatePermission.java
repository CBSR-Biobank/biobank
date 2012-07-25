package edu.ualberta.med.biobank.permission.clinic;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ClinicCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.CLINIC_CREATE.isAllowed(context.getUser());
    }

}