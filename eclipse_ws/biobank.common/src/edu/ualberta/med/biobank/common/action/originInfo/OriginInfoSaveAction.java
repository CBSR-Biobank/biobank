package edu.ualberta.med.biobank.common.action.originInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.i18n.LocalizedString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;

public class OriginInfoSaveAction implements Action<IdResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private OriginInfoSaveInfo oiInfo;
    private ShipmentInfoSaveInfo siInfo;

    public OriginInfoSaveAction(OriginInfoSaveInfo oiInfo,
        ShipmentInfoSaveInfo siInfo) {
        this.oiInfo = oiInfo;
        this.siInfo = siInfo;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new OriginInfoUpdatePermission(oiInfo.siteId)
            .isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        OriginInfo oi =
            context.get(OriginInfo.class, oiInfo.oiId, new OriginInfo());

        oi.setReceiverSite(context.get(Site.class, oiInfo.siteId));
        oi.setCenter(context.get(Center.class, oiInfo.centerId));

        ShipmentInfo si =
            context
                .get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
        si.setBoxNumber(siInfo.boxNumber);
        si.setPackedAt(siInfo.packedAt);
        si.setReceivedAt(siInfo.receivedAt);
        si.setWaybill(siInfo.waybill);

        ShippingMethod sm = context.load(ShippingMethod.class,
            siInfo.shippingMethodId);

        si.setShippingMethod(sm);

        // This stuff could be extracted to a util method. need to think about
        // how
        if ((oiInfo.comment != null) && !oiInfo.comment.trim().equals("")) {
            Set<Comment> comments = oi.getComments();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(oiInfo.comment);
            newComment.setUser(context.getUser());
            context.getSession().saveOrUpdate(newComment);

            comments.add(newComment);
            oi.setComments(comments);
        }

        oi.setShipmentInfo(si);

        context.getSession().saveOrUpdate(oi);
        context.getSession().flush();

        if (oiInfo.removedSpecIds != null)
            for (Integer specId : oiInfo.removedSpecIds) {
                if (specId == null)
                    throw new ActionException(
                        LocalizedString.tr("Specimen id can not be null"));
                Specimen spec =
                    context.load(Specimen.class, specId);
                Center center = context.load(Center.class, oiInfo.siteId);
                OriginInfo newOriginInfo = new OriginInfo();
                newOriginInfo.setCenter(center);
                spec.setOriginInfo(newOriginInfo);
                spec.setCurrentCenter(center);
                context.getSession().saveOrUpdate(newOriginInfo);
                context.getSession().saveOrUpdate(spec);
            }
        if (oiInfo.addedSpecIds != null)
            for (Integer specId : oiInfo.addedSpecIds) {
                if (specId == null)
                    throw new ActionException(
                        LocalizedString.tr("Specimen id can not be null"));
                Specimen spec =
                    context.load(Specimen.class, specId);
                spec.setOriginInfo(oi);
                spec.setCurrentCenter(oi.getReceiverSite());
                context.getSession().saveOrUpdate(spec);
            }

        return new IdResult(oi.getId());
    }
}
