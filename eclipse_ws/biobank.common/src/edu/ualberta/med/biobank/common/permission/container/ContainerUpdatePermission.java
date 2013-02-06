package edu.ualberta.med.biobank.common.permission.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Site;

public class ContainerUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer containerId;

    private final Integer siteId;

    public ContainerUpdatePermission(Integer containerId) {
        this.containerId = containerId;
        this.siteId = null;
    }

    @SuppressWarnings("nls")
    public ContainerUpdatePermission(Site site) {
        this.containerId = null;

        if (site == null) {
            throw new NullPointerException("site is null");
        }
        this.siteId = site.getId();
    }



    @Override
    public boolean isAllowed(ActionContext context) {
        Site site = null;
        if (containerId != null) {
            Container container = context.load(Container.class, containerId);
            site = container.getSite();
        } else {
            site = context.load(Site.class, siteId);
        }
        return PermissionEnum.CONTAINER_UPDATE.isAllowed(context.getUser(), site);
    }

}
