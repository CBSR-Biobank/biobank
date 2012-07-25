package edu.ualberta.med.biobank.permission.researchGroup;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class SubmitRequestPermission implements Permission {

    private Integer rgId;

    private static final long serialVersionUID = -4824265254683646872L;

    public SubmitRequestPermission(Integer rgId) {
        this.rgId = rgId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ResearchGroup rg =
            context.get(ResearchGroup.class, rgId, new ResearchGroup());
        return PermissionEnum.REQUEST_CREATE.isAllowed(context.getUser(), rg);
    }
}
