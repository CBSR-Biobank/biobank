package edu.ualberta.med.biobank.action.container;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * Initialise children at given positions with the given type. If the positions
 * list is null, initialise all the children. <strong>If a position is already
 * filled then it is skipped and no changes are made to it</strong>.
 * 
 * @return true if at least one children has been initialised
 */
public class ContainerCreateChildrenAction extends ContainerChildAction {
    private static final long serialVersionUID = 1L;

    protected Map<RowColPos, Container> children;
    protected Integer siteId;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerCreatePermission(siteId).isAllowed(context);
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        Container parentContainer =
            context.load(Container.class, parentContainerId);
        siteId = parentContainer.getSite().getId();

        boolean result = false;

        children = new HashMap<RowColPos, Container>();
        for (ContainerPosition pos : parentContainer
            .getChildPositions()) {
            children.put(new RowColPos(pos.getRow(), pos.getCol()),
                pos.getContainer());
        }

        if (positions == null) {
            Capacity capacity =
                parentContainer.getContainerType().getCapacity();
            for (int i = 0, n = capacity.getRowCapacity().intValue(); i < n; i++) {
                for (int j = 0, m = capacity.getColCapacity().intValue(); j < m; j++) {
                    result |= addChildContainer(context, new RowColPos(i, j));
                }
            }
        } else {
            for (RowColPos rcp : positions) {
                result |= addChildContainer(context, rcp);
            }
        }

        return new BooleanResult(result);
    }

    protected boolean addChildContainer(ActionContext context, RowColPos rcp) {
        Container childContainer = children.get(rcp);

        // only initialize if empty
        if (childContainer != null) return false;

        ContainerSaveAction containerSaveAction = new ContainerSaveAction();
        containerSaveAction.setTypeId(childContainerTypeId);
        containerSaveAction.setSiteId(siteId);
        containerSaveAction.setParentId(parentContainerId);
        containerSaveAction.setPosition(rcp);
        containerSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
        containerSaveAction.run(context);

        return true;
    }
}
