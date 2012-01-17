package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;

public class ContainerCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public ContainerCreatePermission() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.CONTAINER_CREATE.isAllowed(context.getUser());
    }
}
