package edu.ualberta.med.biobank.common.action.containerType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeCreatePermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SpecimenType;

public class ContainerTypeSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer containerTypeId;
    private String name;
    private String nameShort;
    private Integer siteId;
    private Boolean topLevel;
    private Integer rowCapacity;
    private Integer colCapacity;
    private Double defaultTemperature;
    private Integer childLabelingSchemeId;
    private Integer activityStatusId;

    private String commentMessage;

    private Set<Integer> specimenTypeIds = new HashSet<Integer>();
    private Set<Integer> childContainerTypeIds = new HashSet<Integer>();

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

    public void setTopLevel(Boolean topLevel) {
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

    public void setChildContainerTypeIds(Set<Integer> childContainerTypeIds) {
        this.childContainerTypeIds = childContainerTypeIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Permission permission;
        if (containerTypeId == null)
            permission = new ContainerTypeCreatePermission();
        else
            permission = new ContainerTypeUpdatePermission(containerTypeId);
        return permission.isAllowed(context);
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
        if ((specimenTypeIds.size() > 0) &&
            (childContainerTypeIds.size() > 0)) {
            throw new ActionCheckException(
                "container type cannot have both specimen types and child container types");
        }
        setSpecimenTypes(context, containerType);
        setChildContainerTypes(context, containerType);
    }

    private void setSpecimenTypes(ActionContext context,
        ContainerType containerType) {
        Map<Integer, SpecimenType> specimenTypes =
            context.load(SpecimenType.class, specimenTypeIds);
        SetDifference<SpecimenType> specimenTypeDiff =
            new SetDifference<SpecimenType>(
                containerType.getSpecimenTypeCollection(),
                specimenTypes.values());

        containerType.setSpecimenTypeCollection(new HashSet<SpecimenType>(
            specimenTypes.values()));

        // remove this container type from specimen types in removed list
        for (SpecimenType specimenType : specimenTypeDiff.getRemoveSet()) {
            Collection<ContainerType> containerTypes =
                specimenType.getContainerTypeCollection();
            if (containerTypes.remove(containerTypes)) {
                specimenType.setContainerTypeCollection(containerTypes);
            } else {
                throw new ActionException(
                    "container type not found in removed specimen type's collection");
            }
        }
    }

    private void setChildContainerTypes(ActionContext context,
        ContainerType containerType) {
        Map<Integer, ContainerType> childContainerTypes =
            context.load(ContainerType.class, childContainerTypeIds);
        SetDifference<ContainerType> childContainerTypeDiff =
            new SetDifference<ContainerType>(
                containerType.getChildContainerTypeCollection(),
                childContainerTypes.values());

        containerType.setChildContainerTypeCollection(
            new HashSet<ContainerType>(childContainerTypes.values()));

        // remove this parent container type from children container types in
        // removed list
        for (ContainerType childContainerType : childContainerTypeDiff
            .getRemoveSet()) {
            Collection<ContainerType> parentContainerTypes =
                childContainerType.getParentContainerTypeCollection();
            if (parentContainerTypes.remove(containerType)) {
                childContainerType
                    .setChildContainerTypeCollection(parentContainerTypes);
            } else {
                throw new ActionException(
                    "parent container type not found in removed child container type's collection");
            }
        }
    }
}
