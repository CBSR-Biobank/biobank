package edu.ualberta.med.biobank.common.action.container;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class ContainerMoveSpecimensAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    public final Integer fromContainerId;
    public final Integer toContainerId;

    public ContainerMoveSpecimensAction(Integer fromContainerId,
        Integer toContainerId) {
        this.fromContainerId = fromContainerId;
        this.toContainerId = toContainerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerUpdatePermission(fromContainerId)
            .isAllowed(context)
            && new ContainerUpdatePermission(toContainerId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        // move all specimens from fromContainerId to toContainerId
        Container fromContainer =
            context.load(Container.class, fromContainerId);
        Container toContainer = context.load(Container.class, toContainerId);
        Session session = context.getSession();

        for (SpecimenPosition pos : fromContainer.getSpecimenPositions()) {
            pos.setContainer(toContainer);
            session.saveOrUpdate(pos);
        }
        return new IdResult(toContainer.getId());
    }

}
