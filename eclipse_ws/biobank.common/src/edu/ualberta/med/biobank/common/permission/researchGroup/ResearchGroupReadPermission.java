package edu.ualberta.med.biobank.common.permission.researchGroup;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.User;

public class ResearchGroupReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer rgId;

    public ResearchGroupReadPermission(Integer rgId) {
        this.rgId = rgId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        ResearchGroup rg =
            new ActionContext(user, session).load(ResearchGroup.class, rgId);
        return PermissionEnum.RESEARCH_GROUP_READ.isAllowed(user,
            rg);
    }

}
