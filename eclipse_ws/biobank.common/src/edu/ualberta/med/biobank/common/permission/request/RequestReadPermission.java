package edu.ualberta.med.biobank.common.permission.request;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class RequestReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.REQUEST_PROCESS.isAllowed(context.getUser());
    }
}
