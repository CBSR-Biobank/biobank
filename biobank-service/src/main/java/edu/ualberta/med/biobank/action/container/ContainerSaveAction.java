package edu.ualberta.med.biobank.action.container;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.comment.CommentUtil;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.permission.container.ContainerUpdatePermission;

public class ContainerSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString CANNOT_SET_LABEL_ERRMSG =
        bundle.tr("Cannot set label on child containers.").format();

    @SuppressWarnings("nls")
    public static final String PATH_DELIMITER = "/";

    public Integer containerId;
    public ActivityStatus activityStatus;
    public String barcode;
    public String label;
    public Integer siteId;
    public Integer typeId;
    public RowColPos position;
    public String path;
    public Integer parentId;
    private String commentText;

    public void setId(Integer containerId) {
        this.containerId = containerId;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
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

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (containerId == null)
            permission = new ContainerCreatePermission(siteId);
        else
            permission = new ContainerUpdatePermission(containerId);
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Container container;
        if (containerId != null) {
            container = context.load(Container.class, containerId);
        } else {
            container = new Container();
        }
        container.setActivityStatus(activityStatus);
        container.setSite(context.load(Site.class, siteId));
        container.setProductBarcode(barcode);
        container.setContainerType(context.load(ContainerType.class,
            typeId));

        if (parentId != null) {
            if (label != null) {
                throw new LocalizedException(CANNOT_SET_LABEL_ERRMSG);
            }
            ContainerActionHelper.setPosition(context, container, position,
                parentId);
            Container parent = context.load(Container.class, parentId);
            ContainerActionHelper.updateContainerPathAndLabel(container,
                parent);
        } else {
            container.setLabel(label);
            container.setTopContainer(container);
        }

        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            container.getComments().add(comment);
        }

        context.getSession().saveOrUpdate(container);

        return new IdResult(container.getId());
    }
}
