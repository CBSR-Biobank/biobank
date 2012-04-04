package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ContainerSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    public static final String PATH_DELIMITER = "/"; //$NON-NLS-1$

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

        StringBuilder path = new StringBuilder();

        if (parentId != null) {
            if (label != null) {
                throw new ActionCheckException(
                    "cannot set label on child containers");
            }
            Container parent = context.load(Container.class, parentId);
            String parentPath = parent.getPath();
            if ((parentPath != null) && !parentPath.isEmpty()) {
                path.append(parentPath).append(PATH_DELIMITER);
            }
            path.append(parentId);
            container.setPath(path.toString());

            container.setTopContainer(parent.getTopContainer());
            container.setLabel(parent.getLabel()
                + parent.getContainerType().getPositionString(
                    position));
        } else {
            container.setLabel(label);
        }

        ContainerActionHelper.setPosition(context, container, position,
            parentId);

        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            container.getComments().add(comment);
        }

        context.getSession().saveOrUpdate(container);

        return new IdResult(container.getId());
    }
}
