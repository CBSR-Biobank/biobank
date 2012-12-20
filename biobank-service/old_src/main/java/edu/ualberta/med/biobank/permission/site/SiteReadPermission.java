package edu.ualberta.med.biobank.permission.site;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SiteReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    public SiteReadPermission(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteReadPermission(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Site site = context.load(Site.class, siteId);
        return PermissionEnum.SITE_READ.isAllowed(context.getUser(), site);
    }
}
