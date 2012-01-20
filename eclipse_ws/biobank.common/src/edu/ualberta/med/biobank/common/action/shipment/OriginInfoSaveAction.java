package edu.ualberta.med.biobank.common.action.shipment;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoSavePermission;
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
        return new OriginInfoSavePermission(oiInfo.oiId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        OriginInfo oi =
            context.get(OriginInfo.class, oiInfo.oiId, new OriginInfo());

        oi.setReceiverSite(context.get(Site.class, oiInfo.siteId));
        oi.setCenter(context.get(Center.class, oiInfo.centerId));

        Collection<Specimen> oiSpecimens = oi.getSpecimenCollection();
        if (oiSpecimens == null) oiSpecimens = new HashSet<Specimen>();

        if (oiInfo.removedSpecIds != null)
            for (Integer specId : oiInfo.removedSpecIds) {
                Specimen spec = context.load(Specimen.class, specId);
                oiSpecimens.remove(spec);
            }
        if (oiInfo.addedSpecIds != null)
            for (Integer specId : oiInfo.addedSpecIds) {
                Specimen spec = context.load(Specimen.class, specId);
                oiSpecimens.add(spec);
            }

        oi.setSpecimenCollection(oiSpecimens);

        ShipmentInfo si =
            context
                .get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
        si.boxNumber = siInfo.boxNumber;
        si.packedAt = siInfo.packedAt;
        si.receivedAt = siInfo.receivedAt;
        si.waybill = siInfo.waybill;

        ShippingMethod sm = context.load(ShippingMethod.class,
            siInfo.shippingMethodId);

        si.setShippingMethod(sm);

        // This stuff could be extracted to a util method. need to think about
        // how
        if ((oiInfo.comment != null) && !oiInfo.comment.trim().equals("")) {
            Collection<Comment> comments = oi.getCommentCollection();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(oiInfo.comment);
            newComment.setUser(context.getUser());
            context.getSession().saveOrUpdate(newComment);

            comments.add(newComment);
            oi.setCommentCollection(comments);
        }

        oi.setShipmentInfo(si);

        context.getSession().saveOrUpdate(oi);
        context.getSession().flush();

        return new IdResult(oi.getId());
    }
}
