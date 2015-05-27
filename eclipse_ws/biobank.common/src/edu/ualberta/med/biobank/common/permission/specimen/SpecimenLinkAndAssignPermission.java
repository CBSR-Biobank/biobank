package edu.ualberta.med.biobank.common.permission.specimen;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class SpecimenLinkAndAssignPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    public SpecimenLinkAndAssignPermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Center center = context.load(Center.class, centerId);
        // get is intended
        return PermissionEnum.SPECIMEN_LINK_AND_ASSIGN.isAllowed(context.getUser(), center, null);
    }
}
