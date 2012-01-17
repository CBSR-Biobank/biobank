package edu.ualberta.med.biobank.common.permission.researchGroup;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class ResearchGroupSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer id;

    public ResearchGroupSavePermission(Integer id) {
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
