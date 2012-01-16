package edu.ualberta.med.biobank.common.permission.researchGroup;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.User;

public class SubmitRequestPermission implements Permission {

    private Integer rgId;

    private static final long serialVersionUID = -4824265254683646872L;

    public SubmitRequestPermission(Integer rgId) {
        this.rgId = rgId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        ResearchGroup rg =
            new SessionUtil(session).get(ResearchGroup.class, rgId,
                new ResearchGroup());
        return PermissionEnum.REQUEST_CREATE.isAllowed(user, rg);
    }
}
