package edu.ualberta.med.biobank.common.permission.site;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Site;

public class SiteUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer siteId;

    public SiteUpdatePermission(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Site site = context.load(Site.class, siteId);
        return PermissionEnum.SITE_UPDATE.isAllowed(context.getUser(), site);
    }

}
