package edu.ualberta.med.biobank.common.permission.containerType;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Site;

public class ContainerTypeReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    public ContainerTypeReadPermission(ContainerType type) {
        this.siteId = type.getSite().getId();
    }

    public ContainerTypeReadPermission(Site site) {
        this.siteId = site.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Site site = null;
        if (siteId != null) {
            site = context.load(Site.class, siteId);
        }
        return PermissionEnum.CONTAINER_TYPE_READ.isAllowed(context.getUser(),
            site);
    }
}
