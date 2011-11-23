package edu.ualberta.med.biobank.common.permission.researchGroup;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.User;

public class ResearchGroupSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer id;

    public ResearchGroupSavePermission(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        ResearchGroup rg= new SessionUtil(session).get(ResearchGroup.class, id, new ResearchGroup());
        return PermissionEnum.RESEARCH_GROUP_UPDATE.isAllowed(user,
            rg);
    }

}
