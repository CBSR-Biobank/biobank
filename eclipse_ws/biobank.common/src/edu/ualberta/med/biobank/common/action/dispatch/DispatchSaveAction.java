package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchSavePermission;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class DispatchSaveAction implements Action<IdResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DispatchSaveInfo dInfo;
    private Set<DispatchSpecimenInfo> dsInfos;
    private ShipmentInfoSaveInfo siInfo;

    public DispatchSaveAction(DispatchSaveInfo dInfo,
        Set<DispatchSpecimenInfo> dsInfos, ShipmentInfoSaveInfo siInfo) {
        this.dInfo = dInfo;
        this.dsInfos = dsInfos;
        this.siInfo = siInfo;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchSavePermission(dInfo.id).isAllowed(user,
            session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Dispatch disp =
            sessionUtil.get(Dispatch.class, dInfo.id, new Dispatch());

        disp.setReceiverCenter(sessionUtil.get(Center.class, dInfo.receiverId));
        disp.setSenderCenter(sessionUtil.get(Center.class, dInfo.senderId));

        if (dInfo.state == null)
            dInfo.state = DispatchState.CREATION.getId();

        disp.setState(dInfo.state);

        disp.setDispatchSpecimenCollection(reassemble(sessionUtil, disp,
            dsInfos));

        if (siInfo != null) {
            ShipmentInfo si =
                sessionUtil
                    .get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
            si.boxNumber = siInfo.boxNumber;
            si.packedAt = siInfo.packedAt;
            si.receivedAt = siInfo.receivedAt;
            si.waybill = siInfo.waybill;

            ShippingMethod sm =
                sessionUtil
                    .get(ShippingMethod.class, siInfo.method.id,
                        new ShippingMethod());

            si.setShippingMethod(sm);
            disp.setShipmentInfo(si);
        }

        // This stuff could be extracted to a util method. need to think about
        // how
        if (!dInfo.comment.trim().equals("")) {
            Collection<Comment> comments = disp.getCommentCollection();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(dInfo.comment);
            newComment.setUser(user);
            session.saveOrUpdate(newComment);

            comments.add(newComment);
            disp.setCommentCollection(comments);
        }

        session.saveOrUpdate(disp);
        session.flush();

        return new IdResult(disp.getId());
    }

    private Collection<DispatchSpecimen> reassemble(SessionUtil sessionUtil,
        Dispatch dispatch,
        Set<DispatchSpecimenInfo> dsInfos) {
        Collection<DispatchSpecimen> dispSpecimens =
            new HashSet<DispatchSpecimen>();
        for (DispatchSpecimenInfo dsInfo : dsInfos) {
            DispatchSpecimen dspec =
                sessionUtil.get(DispatchSpecimen.class, dsInfo.id,
                    new DispatchSpecimen());
            dspec.setSpecimen(sessionUtil.load(Specimen.class,
                dsInfo.specimenId));
            dspec.setState(dsInfo.state);
            dspec.setDispatch(dispatch);
            dispSpecimens.add(dspec);
        }
        return dispSpecimens;

    }
}
