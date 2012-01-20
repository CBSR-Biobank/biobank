package edu.ualberta.med.biobank.common.action.shipment;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.shipment.ShipmentDeletePermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;

public class ShipmentDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer shipId = null;

    public ShipmentDeleteAction(Integer id) {
        this.shipId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ShipmentDeletePermission(shipId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        OriginInfo ship = context.get(OriginInfo.class, shipId);

        OriginInfo oi = new OriginInfo();

        Center currentCenter = null;
        for (Specimen spc : ship.getSpecimenCollection()) {
            if (currentCenter == null)
                currentCenter = spc.getCurrentCenter();
            else if (currentCenter != spc.getCurrentCenter())
                throw new ActionException(
                    "Specimens do not come from the same place.");
            spc.setOriginInfo(oi);
        }
        oi.setCenter(currentCenter);
        context.getSession().saveOrUpdate(oi);
        context.getSession().delete(ship);
        return new EmptyResult();
    }
}
