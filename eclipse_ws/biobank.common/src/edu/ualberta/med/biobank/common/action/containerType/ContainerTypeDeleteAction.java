package edu.ualberta.med.biobank.common.action.containerType;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeDeletePermission;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer typeId;

    public ContainerTypeDeleteAction(ContainerType ctype) {
        if (ctype == null) {
            throw new IllegalArgumentException();
        }
        this.typeId = ctype.getId();
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

        // cascades delete all comments

        context.getSession().delete(containerType);
        return new EmptyResult();
    }

}
