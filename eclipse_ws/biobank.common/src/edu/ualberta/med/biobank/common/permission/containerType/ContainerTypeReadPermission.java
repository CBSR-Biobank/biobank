package edu.ualberta.med.biobank.common.permission.containerType;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ContainerTypeReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer typeId;

    public ContainerTypeReadPermission() {
        this.typeId = null;
    }

    public ContainerTypeReadPermission(Integer typeId) {
        this.typeId = typeId;
    }

    public ContainerTypeReadPermission(ContainerType type) {
        this(type.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (typeId != null) {
            ContainerType cType = context.load(ContainerType.class, typeId);
            return PermissionEnum.CONTAINER_TYPE_READ.isAllowed(
                context.getUser(),
                cType.getSite());
        }
        return PermissionEnum.CONTAINER_TYPE_READ.isAllowed(context.getUser());
    }
}
