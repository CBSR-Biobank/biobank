package edu.ualberta.med.biobank.common.permission.container;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.User;

public class ContainerDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer containerId;

    public ContainerDeletePermission(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Container container =
            new ActionContext(user, session).load(Container.class, containerId);
        return PermissionEnum.CONTAINER_DELETE.isAllowed(user,
            container.getSite());
    }

}
