package edu.ualberta.med.biobank.common.permission.containerType;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;

public class ContainerTypeCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public ContainerTypeCreatePermission() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.CONTAINER_TYPE_CREATE
            .isAllowed(context.getUser());
    }
}
