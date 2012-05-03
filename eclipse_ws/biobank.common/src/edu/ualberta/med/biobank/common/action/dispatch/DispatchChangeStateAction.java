package edu.ualberta.med.biobank.common.action.dispatch;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchChangeStateAction implements Action<IdResult> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    private DispatchState newState;
    private ShipmentInfoSaveInfo shipInfo;

    public DispatchChangeStateAction(Integer id, DispatchState state,
        ShipmentInfoSaveInfo shipInfo) {
        this.id = id;
        this.newState = state;
        this.shipInfo = shipInfo;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchChangeStatePermission(id).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Dispatch disp =
            context.load(Dispatch.class, id);

        disp.setState(newState);

        if (shipInfo == null)
            disp.setShipmentInfo(null);
        else {
            ShipmentInfo si =
                context
                    .get(ShipmentInfo.class, shipInfo.siId, new ShipmentInfo());
            si.setBoxNumber(shipInfo.boxNumber);
            si.setPackedAt(shipInfo.packedAt);
            si.setReceivedAt(shipInfo.receivedAt);
            si.setWaybill(shipInfo.waybill);

            ShippingMethod sm = context.load(ShippingMethod.class,
                shipInfo.shippingMethodId);

            si.setShippingMethod(sm);
            disp.setShipmentInfo(si);
        }

        context.getSession().saveOrUpdate(disp);
        context.getSession().flush();

        return new IdResult(disp.getId());
    }

}
