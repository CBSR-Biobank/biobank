package edu.ualberta.med.biobank.common.permission.researchGroup;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class ResearchGroupUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer id;

    public ResearchGroupUpdatePermission(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ResearchGroup rg =
            context.get(ResearchGroup.class, id, new ResearchGroup());
        return PermissionEnum.RESEARCH_GROUP_UPDATE.isAllowed(
            context.getUser(), rg);
    }

}
