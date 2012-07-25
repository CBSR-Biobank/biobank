package edu.ualberta.med.biobank.permission.site;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class SiteCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public SiteCreatePermission() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.SITE_CREATE.isAllowed(context.getUser());
    }
}
