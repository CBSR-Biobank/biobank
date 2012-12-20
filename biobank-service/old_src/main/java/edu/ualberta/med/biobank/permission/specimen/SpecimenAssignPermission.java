package edu.ualberta.med.biobank.permission.specimen;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenAssignPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    public SpecimenAssignPermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Center center = context.load(Center.class, centerId);
        // FIXME check also permission to create/update containers?
        return PermissionEnum.SPECIMEN_ASSIGN.isAllowed(context.getUser(),
            center);
    }
}
