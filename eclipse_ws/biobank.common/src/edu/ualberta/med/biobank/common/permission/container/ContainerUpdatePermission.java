package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Container;

public class ContainerUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer containerId;

    public ContainerUpdatePermission(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Container container = context.load(Container.class, containerId);
        return PermissionEnum.CONTAINER_UPDATE.isAllowed(context.getUser(),
            container.getSite());
    }

}
