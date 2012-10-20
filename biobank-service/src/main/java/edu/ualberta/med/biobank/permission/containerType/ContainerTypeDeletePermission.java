package edu.ualberta.med.biobank.permission.containerType;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ContainerTypeDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer typeId;

    public ContainerTypeDeletePermission(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ContainerType cType = context.load(ContainerType.class, typeId);
        return PermissionEnum.CONTAINER_TYPE_DELETE.isAllowed(
            context.getUser(),
            cType.getSite());
    }

}
