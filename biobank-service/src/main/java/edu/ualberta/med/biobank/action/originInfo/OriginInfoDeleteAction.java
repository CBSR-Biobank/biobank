package edu.ualberta.med.biobank.action.originInfo;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.permission.shipment.ShipmentDeletePermission;

public class OriginInfoDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString SPECIMEN_ORIGIN_ERRMSG =
        bundle.tr("Specimens do not come from the same place.").format();

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
                throw new LocalizedException(SPECIMEN_ORIGIN_ERRMSG);
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
