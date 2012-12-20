package edu.ualberta.med.biobank.permission.container;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ContainerReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    public ContainerReadPermission(Integer siteId) {
        this.siteId = siteId;
    }

    public ContainerReadPermission(Container container) {
        this.siteId = container.getSite().getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Site site = null;
        if (this.siteId != null) {
            site = context.load(Site.class, siteId);
        }

        return PermissionEnum.CONTAINER_READ.isAllowed(context.getUser(), site);
    }
}
