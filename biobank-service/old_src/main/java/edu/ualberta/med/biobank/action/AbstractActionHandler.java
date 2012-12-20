package edu.ualberta.med.biobank.action;

import edu.ualberta.med.biobank.util.GenericUtil;

public abstract class AbstractActionHandler<A extends Action2p0<R>, R extends ActionResult>
    implements ActionHandler<A, R> {
    private final Class<A> actionType;

    @SuppressWarnings("unchecked")
    public AbstractActionHandler() {
        actionType = (Class<A>) GenericUtil
            .getFirstTypeParameterDeclaredOnSuperclass(getClass());
    }

    @Override
    public Class<A> getActionType() {
        return actionType;
    }
}
