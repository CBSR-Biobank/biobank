package edu.ualberta.med.biobank.common.action.dispatch;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.User;

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
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchChangeStatePermission(id).isAllowed(user,
            session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Dispatch disp =
            sessionUtil.load(Dispatch.class, id);

        disp.setState(newState.getId());

        if (shipInfo != null) {
            ShipmentInfo si =
                sessionUtil
                    .get(ShipmentInfo.class, shipInfo.siId, new ShipmentInfo());
            si.boxNumber = shipInfo.boxNumber;
            si.packedAt = shipInfo.packedAt;
            si.receivedAt = shipInfo.receivedAt;
            si.waybill = shipInfo.waybill;

            ShippingMethod sm =
                sessionUtil
                    .get(ShippingMethod.class, shipInfo.method.id,
                        new ShippingMethod());

            si.setShippingMethod(sm);
            disp.setShipmentInfo(si);
        }

        session.saveOrUpdate(disp);
        session.flush();

        return new IdResult(disp.getId());
    }

}
