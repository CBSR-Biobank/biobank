package edu.ualberta.med.biobank.common.action.containerType;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.User;

public class ContainerTypeSaveAction implements Action<ContainerTypeIdResult> {
    private static final long serialVersionUID = 1L;

    private Integer containerTypeId;
    private String name;
    private String nameShort;
    private boolean topLevel;
    private Integer rowCapacity;
    private Integer colCapacity;
    private Double defaultTemperature;
    private Integer childLabelingSchemeId;
    private Integer activityStatusId;

    private String commentMessage;

    private Set<Integer> specimenTypeIds;
    private Set<Integer> containerTypeIds;

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true; // TODO: real permissions.
    }

    @Override
    public ContainerTypeIdResult run(User user, Session session)
        throws ActionException {
        ActionContext context = new ActionContext(user, session);

        ContainerType containerType = getContainerType(context);

        containerType.setName(name);
        containerType.setNameShort(nameShort);
        containerType.setTopLevel(topLevel);
        containerType.getCapacity().setRowCapacity(rowCapacity);
        containerType.getCapacity().setColCapacity(colCapacity);
        containerType.setDefaultTemperature(defaultTemperature);

        addComment(context, containerType);
        setChildLabelingScheme(context, containerType);
        setActivityStatus(context, containerType);
        setContents(context, containerType);

        context.getSession().save(containerType);

        return new ContainerTypeIdResult(containerType.getId());
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
