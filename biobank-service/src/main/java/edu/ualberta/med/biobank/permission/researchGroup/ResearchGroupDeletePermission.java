package edu.ualberta.med.biobank.permission.researchGroup;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class ResearchGroupDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer rgId;

    public ResearchGroupDeletePermission(Integer rgId) {
        this.rgId = rgId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ResearchGroup rg = context.load(ResearchGroup.class, rgId);
        return PermissionEnum.RESEARCH_GROUP_DELETE.isAllowed(
            context.getUser(), rg);
    }
}
