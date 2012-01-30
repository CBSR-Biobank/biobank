package edu.ualberta.med.biobank.common.action.containerType;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeDeletePermission;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer typeId = null;

    public ContainerTypeDeleteAction(Integer id) {
        this.typeId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ContainerTypeDeletePermission(typeId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        // TODO: remove this container type from all parent container types that
        // have it as a child.

        ContainerType containerType = context.load(ContainerType.class, typeId);

        new CollectionIsEmptyCheck<ContainerType>(ContainerType.class,
            containerType, ContainerTypePeer.SPECIMEN_TYPE_COLLECTION,
            containerType.getName(), null).run(context);

        // cascades delete all comments

        context.getSession().delete(containerType);
        return new EmptyResult();
    }

}
