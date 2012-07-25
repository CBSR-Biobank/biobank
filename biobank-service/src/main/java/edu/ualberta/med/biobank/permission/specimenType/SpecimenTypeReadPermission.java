package edu.ualberta.med.biobank.permission.specimenType;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenTypeReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.SPECIMEN_TYPE_READ.isAllowed(context.getUser());
    }
}
