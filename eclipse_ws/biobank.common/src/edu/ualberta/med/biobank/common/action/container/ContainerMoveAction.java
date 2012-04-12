package edu.ualberta.med.biobank.common.action.container;

import java.util.ArrayList;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;

public class ContainerMoveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(ContainerMoveAction.class.getName());

    public final Integer containerToMoveId;
    public final Integer newParentContainerId;
    public final String newLabel;

    private ActionContext context;

    public ContainerMoveAction(Container containerToMove,
        Container newParentContainer, String newLabel) {
        if (containerToMove == null) {
            throw new IllegalArgumentException("Container to move is null");
        }
        if (newParentContainer == null) {
            throw new IllegalArgumentException(
                "New parent container to move to is null");
        }
        if (newLabel == null) {
            throw new IllegalArgumentException("Move to label is null");
        }

        if (log.isDebugEnabled()) {
            log.debug("containerToMoveId={} newParentId={} newLabel={}",
                new Object[] { containerToMove.getLabel(),
                    newParentContainer.getLabel(),
                    newLabel });
        }

        this.containerToMoveId = containerToMove.getId();
        this.newParentContainerId = newParentContainer.getId();
        this.newLabel = newLabel;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerUpdatePermission(containerToMoveId)
            .isAllowed(context)
            && new ContainerUpdatePermission(newParentContainerId)
                .isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        this.context = context;

        // validation tests will tell us if the new position is not empty
        Container containerToMove = context.load(Container.class,
            containerToMoveId);
        Container newParentContainer = context.load(Container.class,
            newParentContainerId);

        try {
            RowColPos position =
                newParentContainer.getPositionFromLabelingScheme(
                    newLabel.substring(newParentContainer.getLabel()
                        .length()));

            ContainerActionHelper.setPosition(context, containerToMove,
                position,
                newParentContainerId);

            context.getSession().saveOrUpdate(containerToMove);
            context.getSession().flush();

            log.debug("container " + containerToMoveId + " moved under parent "
                + newParentContainerId);
        } catch (ConstraintViolationException e) {
            ArrayList<String> msgs = new ArrayList<String>();
            for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
                msgs.add(cv.getMessage());
            }
            throw new ActionException(StringUtils.join(msgs.toArray(), "\n"));
        } catch (Exception e) {
            throw new ActionException(e);
        }

        // update the path information for this container and its children
        updateContainerAndChildren(containerToMove, newParentContainer);

        return new IdResult(containerToMoveId);

    }

    private void updateContainerAndChildren(Container container,
        Container parentContainer) {
        if (context == null) {
            throw new IllegalStateException("action context not set");
        }
        ContainerActionHelper.updateContainerPathAndLabel(container,
            parentContainer);
        context.getSession().saveOrUpdate(container);

        log.debug("updateContainerAndChildren: containerId={} label={}",
            container.getId(), container.getLabel());

        for (ContainerPosition childPosition : container.getChildPositions()) {
            updateContainerAndChildren(childPosition.getContainer(), container);
        }

    }
}
