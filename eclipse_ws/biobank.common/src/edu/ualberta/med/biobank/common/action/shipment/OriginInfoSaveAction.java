package edu.ualberta.med.biobank.common.action.shipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.CommentInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
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

    public class OiInfo {
        private Integer oiId;
        private Integer siteId;
        private Integer centerId;
        private Integer siId;
        private List<Integer> specIds;
    }

    public class SiInfo {

        private String boxNumber;
        private Date packedAt;
        private Date receivedAt;
        private String waybill;
        private CommentInfo commentInfo;

    }

    public OriginInfoSaveAction(Integer oiId, Integer siteId, Integer centerId,
        Integer siId, String boxNumber, Date packedAt, Date receivedAt,
        String waybill, String comment, List<Integer> specIds) {
        this.oiId = oiId;
        this.siteId = siteId;
        this.centerId = centerId;
        this.siId = siId;
        this.boxNumber = boxNumber;
        this.packedAt = packedAt;
        this.receivedAt = receivedAt;
        this.waybill = waybill;
        this.comment = comment;
        this.specIds = specIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoSavePermission(oiId).isAllowed(user, session);
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        OriginInfo oi =
            sessionUtil.get(OriginInfo.class, oiId, new OriginInfo());

        oi.setReceiverSite(sessionUtil.get(Site.class, siteId));
        oi.setCenter(sessionUtil.get(Center.class, centerId));

        ShipmentInfo si =
            sessionUtil.get(ShipmentInfo.class, siId, new ShipmentInfo());
        si.boxNumber = boxNumber;
        si.packedAt = packedAt;
        si.receivedAt = receivedAt;
        si.waybill = waybill;

        Collection<Comment> comments = si.getCommentCollection();
        if (comments == null) comments = new ArrayList<Comment>();
        comments.add(comment);
        si.setCommentCollection(comments);

        oi.setShipmentInfo(si);

        session.saveOrUpdate(oi);
        session.flush();

        return oi.getId();
    }
}
