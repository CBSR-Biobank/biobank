package edu.ualberta.med.biobank.common.action.shipment;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OiInfo;
import edu.ualberta.med.biobank.common.action.info.SiInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoSavePermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class OriginInfoSaveAction implements Action<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private OiInfo oiInfo;
    private SiInfo siInfo;

    public OriginInfoSaveAction(OiInfo oiInfo, SiInfo siInfo) {
        this.oiInfo = oiInfo;
        this.siInfo = siInfo;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoSavePermission(oiInfo.oiId).isAllowed(user,
            session);
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        OriginInfo oi =
            sessionUtil.get(OriginInfo.class, oiInfo.oiId, new OriginInfo());

        oi.setReceiverSite(sessionUtil.get(Site.class, oiInfo.siteId));
        oi.setCenter(sessionUtil.get(Center.class, oiInfo.centerId));

        ShipmentInfo si =
            sessionUtil
                .get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
        si.boxNumber = siInfo.boxNumber;
        si.packedAt = siInfo.packedAt;
        si.receivedAt = siInfo.receivedAt;
        si.waybill = siInfo.waybill;

        // This stuff could be extracted to a util method. need to think about
        // how
        Collection<Comment> comments = si.getCommentCollection();
        if (comments == null) comments = new ArrayList<Comment>();
        Comment newComment = new Comment();
        newComment.setCreatedAt(siInfo.commentInfo.createdAt);
        newComment.setMessage(siInfo.commentInfo.message);
        newComment.setUser(user);
        comments.add(newComment);
        si.setCommentCollection(comments);

        oi.setShipmentInfo(si);

        session.saveOrUpdate(oi);
        session.flush();

        return oi.getId();
    }
}
