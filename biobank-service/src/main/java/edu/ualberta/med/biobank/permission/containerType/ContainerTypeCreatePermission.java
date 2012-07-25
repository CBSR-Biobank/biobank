package edu.ualberta.med.biobank.permission.containerType;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ContainerTypeCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer centerId;

    public ContainerTypeCreatePermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.CONTAINER_TYPE_CREATE
            .isAllowed(context.getUser(), context.load(Center.class, centerId));
    }
}
