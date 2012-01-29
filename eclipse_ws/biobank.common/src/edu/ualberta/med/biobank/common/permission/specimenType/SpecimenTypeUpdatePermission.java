package edu.ualberta.med.biobank.common.permission.specimenType;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class SpecimenTypeUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenTypeUpdatePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.SPECIMEN_TYPE_UPDATE.isAllowed(context.getUser());
    }
}
