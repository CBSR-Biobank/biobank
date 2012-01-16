package edu.ualberta.med.biobank.common.permission.containerType;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class ContainerTypeCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public ContainerTypeCreatePermission() {
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.CONTAINER_TYPE_CREATE.isAllowed(user);
    }
}
