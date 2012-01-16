package edu.ualberta.med.biobank.common.permission.request;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.User;

public class RequestDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer rId;

    public RequestDeletePermission(Integer rId) {
        this.rId = rId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Request request =
            new ActionContext(user, session).load(Request.class, rId);
        return PermissionEnum.REQUEST_DELETE.isAllowed(user,
            request.getResearchGroup());
    }
}
