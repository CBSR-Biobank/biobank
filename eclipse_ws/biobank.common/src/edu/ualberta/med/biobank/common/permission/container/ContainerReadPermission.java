package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ContainerReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer containerId;

    public ContainerReadPermission(Integer containerId) {
        this.containerId = containerId;
    }

    public ContainerReadPermission(Container container) {
        this(container.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Container container = context.load(Container.class, containerId);
        return PermissionEnum.CONTAINER_READ.isAllowed(context.getUser(),
            container.getSite());
    }
}
