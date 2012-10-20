package edu.ualberta.med.biobank.action.dispatch;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.center.Shipment;
import edu.ualberta.med.biobank.model.center.ShipmentData;
import edu.ualberta.med.biobank.model.center.ShipmentSpecimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.model.type.ShipmentState;

public class DispatchChangeStateAction implements Action<IdResult> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    private ShipmentState newState;
    private ShipmentInfoSaveInfo shipInfo;

    public DispatchChangeStateAction(Integer id, ShipmentState state,
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
        Shipment disp =
            context.load(Shipment.class, id);

        disp.setState(newState);

        if (newState.equals(ShipmentState.LOST))
            for (ShipmentSpecimen ds : disp.getDispatchSpecimens())
                ds.getSpecimen().setActivityStatus(ActivityStatus.FLAGGED);

        if (shipInfo == null)
            disp.setShipmentInfo(null);
        else {
            ShipmentData si =
                context
                    .get(ShipmentData.class, shipInfo.siId, new ShipmentData());
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
