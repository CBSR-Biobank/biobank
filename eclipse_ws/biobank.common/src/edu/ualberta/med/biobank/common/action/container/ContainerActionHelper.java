package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.SS;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ContainerActionHelper {

    @SuppressWarnings("nls")
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
                SS.tr("Parent container and position should either both be set or both be null"));
        }
        container.setTopContainer(parent == null ? container : parent
            .getTopContainer());
    }
}
