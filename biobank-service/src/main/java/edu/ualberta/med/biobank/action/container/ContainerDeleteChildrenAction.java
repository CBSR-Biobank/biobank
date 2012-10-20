package edu.ualberta.med.biobank.action.container;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerPosition;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * Delete the children at the specified positions in the parent container and
 * with the given type (or all if positions list is null).
 * 
 * If type is null then all child containers are deleted.
 * 
 * @return true if at least one children has been deleted
 */
public class ContainerDeleteChildrenAction extends ContainerChildAction {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerDeletePermission().isAllowed(context);
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        Container parentContainer =
            context.load(Container.class, parentContainerId);

        boolean result = false;

        if (positions == null) {
            for (ContainerPosition pos : parentContainer
                .getChildPositions()) {
                result |= deleteChild(context, pos.getContainer());
            }
        } else {
            Map<RowColPos, Container> children =
                new HashMap<RowColPos, Container>();
            for (ContainerPosition pos : parentContainer
                .getChildPositions()) {
                children.put(new RowColPos(pos.getRow(), pos.getCol()),
                    pos.getContainer());
            }

            for (RowColPos rcp : positions) {
                Container childContainer = children.get(rcp);
                if (childContainer != null) {
                    result |= deleteChild(context, childContainer);
                }
            }
        }

        return new BooleanResult(result);
    }

    protected boolean deleteChild(ActionContext context,
        Container childContainer) {
        // if container type is null then all child containers should be deleted
        if ((childContainerTypeId != null)
            && !childContainer.getContainerType().getId()
                .equals(childContainerTypeId)) {
            return false;
        }

        ContainerDeleteAction containerDeleteAction =
            new ContainerDeleteAction(childContainer);
        containerDeleteAction.run(context);

        return true;

    }
}
