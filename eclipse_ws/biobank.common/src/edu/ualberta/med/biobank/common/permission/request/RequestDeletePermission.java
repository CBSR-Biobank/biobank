package edu.ualberta.med.biobank.common.permission.request;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Request;

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
