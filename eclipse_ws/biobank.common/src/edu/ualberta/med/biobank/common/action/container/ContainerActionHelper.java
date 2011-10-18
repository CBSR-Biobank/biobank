package edu.ualberta.med.biobank.common.action.container;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerActionHelper {

    public static void setPosition(Session session, Container container,
        RowColPos rcp, Integer parentId) {
        ContainerPosition pos = container.getPosition();
        if (pos == null) {
            pos = new ContainerPosition();
            pos.setContainer(container);
            container.setPosition(pos);
        }

        Container parent = null;
        if (parentId != null && rcp != null) {
            pos.setRow(rcp.getRow());
            pos.setCol(rcp.getCol());

            parent = ActionUtil.sessionGet(session, Container.class, parentId);
            pos.setParentContainer(parent);
            ContainerType parentType = parent.getContainerType();
            String positionString = ContainerLabelingSchemeWrapper
                .getPositionString(rcp, parentType.getChildLabelingScheme()
                    .getId(), parentType.getCapacity().getRowCapacity(),
                    parentType.getCapacity().getColCapacity());
            pos.setPositionString(positionString);
        } else if (parentId == null && rcp == null) {
            if (pos.getId() != null)
                session.delete(pos);
        } else {
            throw new ActionException(
                "Problem: parent and position should be both set or both null"); //$NON-NLS-1$
        }
        container.setTopContainer(parent == null ? container : parent
            .getTopContainer());
    }
}
