package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchChangeStateAction implements Action<IdResult> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Integer id;
    private final DispatchState newState;
    private final ShipmentInfoSaveInfo shipInfo;

    public DispatchChangeStateAction(Integer id, DispatchState state, ShipmentInfoSaveInfo shipInfo) {
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
        Dispatch dispatch = context.load(Dispatch.class, id);

        dispatch.setState(newState);

        if (newState.equals(DispatchState.LOST)) {
            for (DispatchSpecimen ds : dispatch.getDispatchSpecimens()) {
                ds.getSpecimen().setActivityStatus(ActivityStatus.FLAGGED);
            }
        } else if (newState.equals(DispatchState.IN_TRANSIT)) {
            // update the current center on the specimens
            Center receiverCenter = dispatch.getReceiverCenter();

            Criteria c = context.getSession().createCriteria(DispatchSpecimen.class)
                .add(Restrictions.eq("dispatch", dispatch))
                .setFetchMode("specimen", FetchMode.JOIN);

            @SuppressWarnings("unchecked")
            List<DispatchSpecimen> list = c.list();
            for (DispatchSpecimen dispatchSpecimen : list) {
                Specimen specimen = dispatchSpecimen.getSpecimen();
                specimen.setCurrentCenter(receiverCenter);
                context.getSession().saveOrUpdate(specimen);
            }
        }

        if (shipInfo == null) {
            dispatch.setShipmentInfo(null);
        } else {
            ShipmentInfo si = context.get(ShipmentInfo.class, shipInfo.siId, new ShipmentInfo());
            si.setBoxNumber(shipInfo.boxNumber);
            si.setPackedAt(shipInfo.packedAt);
            si.setReceivedAt(shipInfo.receivedAt);
            si.setWaybill(shipInfo.waybill);

            ShippingMethod sm = context.load(ShippingMethod.class,
                shipInfo.shippingMethodId);

            si.setShippingMethod(sm);
            dispatch.setShipmentInfo(si);
        }

        context.getSession().saveOrUpdate(dispatch);
        context.getSession().flush();

        return new IdResult(dispatch.getId());
    }
}
