package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ContainerDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer containerId = null;

    public ContainerDeletePermission() {
    }

    public ContainerDeletePermission(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (containerId != null) {
            Container container = context.load(Container.class, containerId);
            return PermissionEnum.CONTAINER_DELETE.isAllowed(context.getUser(),
                container.getSite());
        } else {
            return PermissionEnum.CONTAINER_DELETE.isAllowed(context.getUser());
        }
    }

}
