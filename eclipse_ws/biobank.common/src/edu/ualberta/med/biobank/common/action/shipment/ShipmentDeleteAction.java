package edu.ualberta.med.biobank.common.action.shipment;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.shipment.ShipmentDeletePermission;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

public class ShipmentDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer shipId = null;

    public ShipmentDeleteAction(Integer id) {
        this.shipId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new ShipmentDeletePermission(shipId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        OriginInfo ship =
            new SessionUtil(session).get(OriginInfo.class, shipId);

        // / ??? what checks???

        session.delete(ship);
        return new EmptyResult();
    }
}
