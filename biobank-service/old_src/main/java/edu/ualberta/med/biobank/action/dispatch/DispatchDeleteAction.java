package edu.ualberta.med.biobank.action.dispatch;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.center.Shipment;
import edu.ualberta.med.biobank.model.type.ShipmentState;

public class DispatchDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString CREATION_ONLY_ERRMSG =
        bundle.tr("Only freshly created dispatches may be deleted.").format();

    protected final Integer shipId;

    public DispatchDeleteAction(Shipment dispatch) {
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
        Shipment ship = context.get(Shipment.class, shipId);

        if (ShipmentState.PACKED == ship.getState()) {
            context.getSession().delete(ship);
        } else {
            throw new LocalizedException(CREATION_ONLY_ERRMSG);
        }

        return new EmptyResult();
    }
}
