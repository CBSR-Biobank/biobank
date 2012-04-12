package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;

public class ContainerActionHelper {

    public static final String PATH_DELIMITER = "/"; //$NON-NLS-1$

    public static void setPosition(ActionContext context,
        Container container, RowColPos rcp, Integer parentId) {
        ContainerPosition pos = container.getPosition();
        if ((pos == null) && (rcp != null)) {
            pos = new ContainerPosition();
            pos.setContainer(container);
            container.setPosition(pos);
        }

        Container parent = null;
        if ((parentId != null) && (rcp != null)) {
            pos.setRow(rcp.getRow());
            pos.setCol(rcp.getCol());

            parent = context.load(Container.class, parentId);
            pos.setParentContainer(parent);
        } else if ((parentId == null) && (rcp == null)) {
            if ((pos != null) && (pos.getId() != null)) {
                context.getSession().delete(pos);
            }
        } else {
            throw new ActionException(
                "Problem: parent and position should be both set or both null"); //$NON-NLS-1$
        }
        container.setTopContainer(parent == null ? container : parent
            .getTopContainer());
    }

    public static void updateContainerPathAndLabel(Container container,
        Container parentContainer) {
        StringBuilder path = new StringBuilder();
        String parentPath = parentContainer.getPath();
        if ((parentPath != null) && !parentPath.isEmpty()) {
            path.append(parentPath).append(PATH_DELIMITER);
        }
        path.append(parentContainer.getId());
        container.setPath(path.toString());
        container.setTopContainer(parentContainer.getTopContainer());
        container.setLabel(parentContainer.getLabel()
            + parentContainer.getContainerType().getPositionString(
                container.getPositionAsRowCol()));

    }
}
