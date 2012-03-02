package edu.ualberta.med.biobank.common.permission.researchGroup;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ResearchGroupCreatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.RESEARCH_GROUP_CREATE.isAllowed(
            context.getUser());
    }

}
