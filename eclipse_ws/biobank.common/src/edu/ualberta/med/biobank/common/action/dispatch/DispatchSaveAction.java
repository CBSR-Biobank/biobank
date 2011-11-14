package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchSavePermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class DispatchSaveAction implements Action<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DispatchSaveInfo dInfo;
    private ShipmentInfoSaveInfo siInfo;

    public DispatchSaveAction(DispatchSaveInfo dInfo, ShipmentInfoSaveInfo siInfo) {
        this.dInfo = dInfo;
        this.siInfo = siInfo;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchSavePermission(dInfo.id).isAllowed(user,
            session);
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Dispatch oi =
            sessionUtil.get(Dispatch.class, dInfo.id, new Dispatch());

        oi.setReceiverCenter(sessionUtil.get(Site.class, dInfo.receiverId));
        oi.setSenderCenter(sessionUtil.get(Center.class, dInfo.senderId));

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
        Collection<Comment> comments = si.getCommentCollection();
        if (comments == null) comments = new HashSet<Comment>();
        Comment newComment = new Comment();
        newComment.setCreatedAt(new Date());
        newComment.setMessage(siInfo.commentInfo.message);
        newComment.setUser(user);
        session.saveOrUpdate(newComment);
        
        comments.add(newComment);
        si.setCommentCollection(comments);

        oi.setShipmentInfo(si);

        session.saveOrUpdate(oi);
        session.flush();

        return oi.getId();
    }
}
