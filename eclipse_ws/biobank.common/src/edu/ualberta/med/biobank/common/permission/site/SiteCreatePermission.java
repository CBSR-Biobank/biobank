package edu.ualberta.med.biobank.common.permission.site;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;

public class SiteCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public SiteCreatePermission() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.SITE_CREATE.isAllowed(context.getUser());
    }
}
