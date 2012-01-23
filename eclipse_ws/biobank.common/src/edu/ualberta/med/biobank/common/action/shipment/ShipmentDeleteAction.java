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

    private Integer workingCenter;

    public ShipmentDeleteAction(Integer id, Integer workingCenter) {
        this.shipId = id;
        this.workingCenter = workingCenter;
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
        oi.setCenter(context.load(Center.class, workingCenter));
        context.getSession().saveOrUpdate(oi);
        for (Specimen spc : ship.getSpecimenCollection()) {
            context.getSession().saveOrUpdate(spc);
        }

        context.getSession().delete(ship);
        return new EmptyResult();
    }
}
