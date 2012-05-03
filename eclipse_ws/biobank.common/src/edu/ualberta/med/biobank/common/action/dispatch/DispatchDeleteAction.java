package edu.ualberta.med.biobank.common.action.dispatch;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString CREATION_ONLY_ERRMSG =
        bundle.tr("Only freshly created dispatches may be deleted.").format();

    protected final Integer shipId;

    public DispatchDeleteAction(Dispatch dispatch) {
        if (dispatch == null) {
            throw new IllegalArgumentException();
        }
        this.shipId = dispatch.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new DispatchDeletePermission(shipId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Dispatch ship = context.get(Dispatch.class, shipId);

        if (DispatchState.CREATION == ship.getState()) {
            context.getSession().delete(ship);
        } else {
            throw new ActionException(CREATION_ONLY_ERRMSG);
        }

        return new EmptyResult();
    }
}
