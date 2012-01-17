package edu.ualberta.med.biobank.common.action.containerType;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeCreatePermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeUpdatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class ContainerTypeSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer containerTypeId;
    private String name;
    private String nameShort;
    private Integer siteId;
    private boolean topLevel;
    private Integer rowCapacity;
    private Integer colCapacity;
    private Double defaultTemperature;
    private Integer childLabelingSchemeId;
    private Integer activityStatusId;

    private String commentMessage;

    private Set<Integer> specimenTypeIds;
    private Set<Integer> containerTypeIds;

    public void setId(Integer containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setTopLevel(boolean topLevel) {
        this.topLevel = topLevel;
    }

    public void setRowCapacity(Integer rowCapacity) {
        this.rowCapacity = rowCapacity;
    }

    public void setColCapacity(Integer colCapacity) {
        this.colCapacity = colCapacity;
    }

    public void setDefaultTemperature(Double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }

    public void setChildLabelingSchemeId(Integer childLabelingSchemeId) {
        this.childLabelingSchemeId = childLabelingSchemeId;
    }

    public void setActivityStatusId(Integer activityStatusId) {
        this.activityStatusId = activityStatusId;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    public void setSpecimenTypeIds(Set<Integer> specimenTypeIds) {
        this.specimenTypeIds = specimenTypeIds;
    }

    public void setContainerTypeIds(Set<Integer> containerTypeIds) {
        this.containerTypeIds = containerTypeIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Permission permission;
        if (containerTypeId == null)
            permission = new ContainerTypeCreatePermission();
        else
            permission = new ContainerTypeUpdatePermission(containerTypeId);
        return permission.isAllowed(null);
    }

    @Override
    public IdResult run(ActionContext context)
        throws ActionException {
        ContainerType containerType = getContainerType(context);

        containerType.setName(name);
        containerType.setNameShort(nameShort);
        containerType.setSite(context.load(Site.class, siteId));
        containerType.setTopLevel(topLevel);
        containerType.getCapacity().setRowCapacity(rowCapacity);
        containerType.getCapacity().setColCapacity(colCapacity);
        containerType.setDefaultTemperature(defaultTemperature);

        addComment(context, containerType);
        setChildLabelingScheme(context, containerType);
        setActivityStatus(context, containerType);
        setContents(context, containerType);

        context.getSession().save(containerType);

        return new IdResult(containerType.getId());
    }

    private ContainerType getContainerType(ActionContext context) {
        ContainerType containerType =
            context.load(ContainerType.class, containerTypeId);

        if (containerType == null) {
            containerType = new ContainerType();
            containerType.setCapacity(new Capacity());
            containerType.setCommentCollection(new HashSet<Comment>());
        }

        return containerType;
    }

    private void addComment(ActionContext context, ContainerType containerType) {
        Comment comment = CommentUtil.create(context.getUser(), commentMessage);
        if (comment != null) {
            context.getSession().save(comment);
            containerType.getCommentCollection().add(comment);
        }
    }

    private void setChildLabelingScheme(ActionContext context,
        ContainerType containerType) {
        ContainerLabelingScheme childLabelingScheme =
            context.load(ContainerLabelingScheme.class, childLabelingSchemeId);
        containerType.setChildLabelingScheme(childLabelingScheme);
    }

    private void setActivityStatus(ActionContext context,
        ContainerType containerType) {
        ActivityStatus activityStatus =
            context.load(ActivityStatus.class, activityStatusId);
        containerType.setActivityStatus(activityStatus);
    }

    private void setContents(ActionContext context, ContainerType containerType) {
        // TODO: this
    }
}
