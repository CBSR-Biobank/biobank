package edu.ualberta.med.biobank.permission.request;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class RequestDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer rId;

    public RequestDeletePermission(Integer rId) {
        this.rId = rId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Request request = context.load(Request.class, rId);
        return PermissionEnum.REQUEST_DELETE.isAllowed(context.getUser(),
            request.getResearchGroup());
    }
}
