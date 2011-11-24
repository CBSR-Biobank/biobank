package edu.ualberta.med.biobank.common.action.shipment;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoSavePermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

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
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoSavePermission(oiInfo.oiId).isAllowed(user,
            session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        OriginInfo oi =
            sessionUtil.get(OriginInfo.class, oiInfo.oiId, new OriginInfo());

        oi.setReceiverSite(sessionUtil.get(Site.class, oiInfo.siteId));
        oi.setCenter(sessionUtil.get(Center.class, oiInfo.centerId));
        
        Collection<Specimen> oiSpecimens = oi.getSpecimenCollection();
        if(oiSpecimens == null) oiSpecimens = new HashSet<Specimen>();
        
        for (Integer specId : oiInfo.removedSpecIds) {
            Specimen spec =
                sessionUtil.load(Specimen.class, specId);
            oiSpecimens.remove(spec);
        }
        
        for (Integer specId : oiInfo.addedSpecIds) {
            Specimen spec =
                sessionUtil.load(Specimen.class, specId);
            oiSpecimens.add(spec);
        }
        
        oi.setSpecimenCollection(oiSpecimens);
        
        ShipmentInfo si =
            sessionUtil
                .get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
        si.boxNumber = siInfo.boxNumber;
        si.packedAt = siInfo.packedAt;
        si.receivedAt = siInfo.receivedAt;
        si.waybill = siInfo.waybill;

        ShippingMethod sm = sessionUtil
            .get(ShippingMethod.class, siInfo.method.id, new ShippingMethod());

        si.setShippingMethod(sm);

        // This stuff could be extracted to a util method. need to think about
        // how
        if (!oiInfo.comment.trim().equals("")) {
            Collection<Comment> comments = oi.getCommentCollection();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(oiInfo.comment);
            newComment.setUser(user);
            session.saveOrUpdate(newComment);
            
            comments.add(newComment);
            oi.setCommentCollection(comments);
        }

        oi.setShipmentInfo(si);

        session.saveOrUpdate(oi);
        session.flush();

        return new IdResult(oi.getId());
    }
}
