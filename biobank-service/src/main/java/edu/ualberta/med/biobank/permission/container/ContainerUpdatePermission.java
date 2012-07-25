package edu.ualberta.med.biobank.permission.container;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PermissionEnum;

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
