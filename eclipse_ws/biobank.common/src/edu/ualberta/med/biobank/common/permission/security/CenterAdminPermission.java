package edu.ualberta.med.biobank.common.permission.security;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class CenterAdminPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = 2457374540746789832L;
    private Integer workingCenter;

    public CenterAdminPermission(Integer workingCenter) {
        this.workingCenter = workingCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.ADMINISTRATION.isAllowed(context.getUser(),
            context.load(Center.class, workingCenter));
    }
}
