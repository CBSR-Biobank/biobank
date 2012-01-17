package edu.ualberta.med.biobank.common.permission.containerType;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ContainerType;

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
