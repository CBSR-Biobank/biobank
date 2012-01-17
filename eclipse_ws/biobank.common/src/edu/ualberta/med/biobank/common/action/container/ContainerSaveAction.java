package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class ContainerSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    public Integer containerId;
    public Integer statusId;
    public String barcode;
    public String label;
    public Integer siteId;
    public Integer typeId;
    public RowColPos position;
    public Integer parentId;

    public void setId(Integer containerId) {
        this.containerId = containerId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public void setPosition(RowColPos position) {
        this.position = position;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // FIXME add specific permission for this?
        Permission permission;
        if (containerId == null)
            permission = new ContainerCreatePermission();
        else
            permission = new ContainerUpdatePermission(containerId);
        return permission.isAllowed(null);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        // FIXME logging
        // FIXME checks

        Container container;
        if (containerId != null) {
            container = context.load(Container.class, containerId);
        } else {
            container = new Container();
        }
        container.setActivityStatus(context.load(ActivityStatus.class,
            statusId));
        container.setSite(context.load(Site.class, siteId));
        container.setProductBarcode(barcode);
        container.setContainerType(context.load(ContainerType.class,
            typeId));
        container.setLabel(label);
        ContainerActionHelper.setPosition(context, container, position,
            parentId);

        context.getSession().saveOrUpdate(container);

        return new IdResult(container.getId());
    }

}
