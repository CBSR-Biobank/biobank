package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;

public class ContainerDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer containerId;

    public ContainerDeleteAction(Container container) {
        if (container == null) {
            throw new IllegalArgumentException();
        }
        this.containerId = container.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ContainerDeletePermission(containerId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Container container = context.load(Container.class, containerId);
        // cascades delete all comments

        delete(container, context);
        return new EmptyResult();
    }

    private void delete(Container container, ActionContext context)
        throws ActionException {
        for (ContainerPosition child : container.getChildPositions())
            delete(child.getContainer(), context);
        context.getSession().flush();
        context.getSession().delete(container);
    }

}
