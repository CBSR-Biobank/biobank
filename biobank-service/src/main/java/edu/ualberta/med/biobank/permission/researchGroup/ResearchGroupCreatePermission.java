package edu.ualberta.med.biobank.permission.researchGroup;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ResearchGroupCreatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.RESEARCH_GROUP_CREATE.isAllowed(
            context.getUser());
    }

}
