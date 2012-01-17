package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.model.Container;

public class ContainerDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer containerId = null;

    public ContainerDeleteAction(Integer id) {
        this.containerId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ContainerDeletePermission(containerId).isAllowed(null);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Container container = context.load(Container.class, containerId);

        new CollectionIsEmptyCheck<Container>(Container.class, container,
            ContainerPeer.CHILD_POSITION_COLLECTION,
            container.getLabel(), null).run(context);

        new CollectionIsEmptyCheck<Container>(Container.class, container,
            ContainerPeer.SPECIMEN_POSITION_COLLECTION,
            container.getLabel(), null).run(context);

        // cascades delete all comments

        context.getSession().delete(container);
        return new EmptyResult();
    }

}
