package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Container;

public class ContainerDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer containerId;

    public ContainerDeletePermission(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Container container = context.load(Container.class, containerId);
        return PermissionEnum.CONTAINER_DELETE.isAllowed(context.getUser(),
            container.getSite());
    }

}