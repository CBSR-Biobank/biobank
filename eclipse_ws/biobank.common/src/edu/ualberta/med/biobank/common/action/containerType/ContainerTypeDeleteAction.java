package edu.ualberta.med.biobank.common.action.containerType;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeDeletePermission;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.User;

public class ContainerTypeDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer typeId = null;

    public ContainerTypeDeleteAction(Integer id) {
        this.typeId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new ContainerTypeDeletePermission(typeId).isAllowed(user,
            session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        ContainerType containerType =
            new ActionContext(user, session).load(ContainerType.class, typeId);

        new CollectionIsEmptyCheck<ContainerType>(ContainerType.class,
            containerType, ContainerTypePeer.SPECIMEN_TYPE_COLLECTION,
            containerType.getName(), null).run(user, session);

        // cascades delete all comments

        session.delete(containerType);
        return new EmptyResult();
    }

}
