package edu.ualberta.med.biobank.common.permission.container;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class ContainerCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public ContainerCreatePermission() {
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.CONTAINER_CREATE.isAllowed(user);
    }
}
