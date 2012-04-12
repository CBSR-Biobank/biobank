package edu.ualberta.med.biobank.common.action.originInfo;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.shipment.ShipmentDeletePermission;
import edu.ualberta.med.biobank.i18n.LocalizedString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;

public class OriginInfoDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer originInfoId;

    private final Integer centerId;

    public OriginInfoDeleteAction(OriginInfo originInfo, Center center) {
        if (originInfo == null) {
            throw new IllegalArgumentException();
        }
        if (center == null) {
            throw new IllegalArgumentException();
        }
        this.originInfoId = originInfo.getId();
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ShipmentDeletePermission(originInfoId, centerId)
            .isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        OriginInfo originInfo = context.load(OriginInfo.class, originInfoId);

        // any specimens get assigned this new origin info
        OriginInfo newOriginInfo = new OriginInfo();

        Center currentCenter = null;
        Center wCenter = context.load(Center.class, centerId);
        for (Specimen spc : originInfo.getSpecimens()) {
            if (currentCenter == null)
                currentCenter = spc.getCurrentCenter();
            else if (currentCenter != spc.getCurrentCenter())
                throw new ActionException(
                    LocalizedString.tr("Specimens do not come from the same place."));
            spc.setOriginInfo(newOriginInfo);
            spc.setCurrentCenter(wCenter);
        }
        newOriginInfo.setCenter(wCenter);
        context.getSession().saveOrUpdate(newOriginInfo);
        for (Specimen spc : originInfo.getSpecimens()) {
            context.getSession().saveOrUpdate(spc);
        }

        context.getSession().delete(originInfo);
        return new EmptyResult();
    }
}
