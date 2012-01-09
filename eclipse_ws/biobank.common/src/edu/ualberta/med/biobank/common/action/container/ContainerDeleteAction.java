package edu.ualberta.med.biobank.common.action.container;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.permission.site.SiteDeletePermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.User;

public class ContainerDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer containerId = null;

    public ContainerDeleteAction(Integer id) {
        this.containerId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SiteDeletePermission(containerId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Container container =
            new ActionContext(user, session).load(Container.class, containerId);

        new CollectionIsEmptyCheck<Container>(Container.class, container,
            ContainerPeer.CHILD_POSITION_COLLECTION,
            container.getLabel(), null).run(user, session);

        new CollectionIsEmptyCheck<Container>(Container.class, container,
            ContainerPeer.SPECIMEN_POSITION_COLLECTION,
            container.getLabel(), null).run(user, session);

        // cascades delete all comments

        session.delete(container);
        return new EmptyResult();
    }

}
