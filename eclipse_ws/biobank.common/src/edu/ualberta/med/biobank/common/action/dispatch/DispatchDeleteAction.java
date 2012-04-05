package edu.ualberta.med.biobank.common.action.dispatch;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.i18n.Msg;
import edu.ualberta.med.biobank.model.Dispatch;

public class DispatchDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

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

    @SuppressWarnings("nls")
    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Dispatch ship = context.get(Dispatch.class, shipId);

        if (ship.getState().equals(DispatchState.CREATION.getId())) {
            context.getSession().delete(ship);
        } else {
            throw new ActionException(
                Msg.tr("Only freshly created dispatches may be deleted."));
        }

        return new EmptyResult();
    }
}
