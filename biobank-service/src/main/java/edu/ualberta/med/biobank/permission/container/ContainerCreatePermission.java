package edu.ualberta.med.biobank.permission.container;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ContainerCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer centerId;

    public ContainerCreatePermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.CONTAINER_CREATE.isAllowed(context.getUser(),
            context.load(Center.class, centerId));
    }
}
