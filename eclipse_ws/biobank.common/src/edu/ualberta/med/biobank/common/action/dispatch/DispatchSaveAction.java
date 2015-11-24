package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenMicroplateConsistentAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenMicroplateConsistentAction.SpecimenMicroplateInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private DispatchSaveInfo dInfo;

    private final Set<DispatchSpecimenInfo> dsInfos;

    private final ShipmentInfoSaveInfo siInfo;

    public DispatchSaveAction(
        DispatchSaveInfo dInfo,
        Set<DispatchSpecimenInfo> dsInfos,
        ShipmentInfoSaveInfo siInfo) {
        this.dInfo = dInfo;
        this.dsInfos = dsInfos;
        this.siInfo = siInfo;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchUpdatePermission(dInfo.dispatchId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        List<SpecimenMicroplateInfo> specimenMicroplateInfos = new ArrayList<SpecimenMicroplateInfo>();
        for (DispatchSpecimenInfo dsi : dsInfos) {
            SpecimenMicroplateInfo smi = new SpecimenMicroplateInfo();
            smi.inventoryId = context.load(Specimen.class, dsi.specimenId).getInventoryId();
            smi.containerId = null;
            smi.position = null;
            specimenMicroplateInfos.add(smi);
        }
        new SpecimenMicroplateConsistentAction(
            dInfo.receiverId, false, specimenMicroplateInfos).run(context);

        Dispatch disp = context.get(Dispatch.class, dInfo.dispatchId, new Dispatch());

        disp.setReceiverCenter(context.get(Center.class, dInfo.receiverId));
        disp.setSenderCenter(context.get(Center.class, dInfo.senderId));

        if (dInfo.state == null) {
            dInfo = new DispatchSaveInfo(dInfo, DispatchState.CREATION);
        }

        disp.setState(dInfo.state);

        disp.getDispatchSpecimens().clear();
        disp.getDispatchSpecimens().addAll(reassemble(context, disp, dsInfos));

        if (siInfo != null) {
            ShipmentInfo si = context.get(ShipmentInfo.class, siInfo.siId, new ShipmentInfo());
            si.setBoxNumber(siInfo.boxNumber);
            si.setPackedAt(siInfo.packedAt);
            si.setReceivedAt(siInfo.receivedAt);
            si.setWaybill(siInfo.waybill);

            ShippingMethod sm = context.load(ShippingMethod.class, siInfo.shippingMethodId);

            si.setShippingMethod(sm);
            disp.setShipmentInfo(si);
        }

        // This stuff could be extracted to a util method. need to think about how
        if ((dInfo.comment != null) && !dInfo.comment.trim().isEmpty()) {
            Set<Comment> comments = disp.getComments();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(dInfo.comment);
            newComment.setUser(context.getUser());
            context.getSession().saveOrUpdate(newComment);

            comments.add(newComment);
            disp.setComments(comments);
        }

        context.getSession().saveOrUpdate(disp);
        context.getSession().flush();

        return new IdResult(disp.getId());
    }

    @SuppressWarnings("nls")
    private Set<DispatchSpecimen> reassemble(ActionContext context, Dispatch dispatch,
        Set<DispatchSpecimenInfo> dsInfos) {
        Set<DispatchSpecimen> dispSpecimens = new HashSet<DispatchSpecimen>();
        for (DispatchSpecimenInfo dsInfo : dsInfos) {
            DispatchSpecimen dspec = context.get(DispatchSpecimen.class, dsInfo.dispatchSpecimenId,
                new DispatchSpecimen());
            Specimen spec = context.load(Specimen.class, dsInfo.specimenId);
            if (spec == null) {
                throw new IllegalStateException(
                    "specimen for dispatch does not exist: " + dsInfo.specimenId);
            }
            if ((dInfo.state == DispatchState.RECEIVED)
                || (dInfo.state == DispatchState.CLOSED)
                || (dInfo.state == DispatchState.LOST)) {
                spec.setCurrentCenter(context.get(Center.class, dInfo.receiverId));
                context.getSession().saveOrUpdate(spec);
            }
            dspec.setSpecimen(spec);
            dspec.setState(dsInfo.state);
            dspec.setDispatch(dispatch);
            dispSpecimens.add(dspec);
        }
        return dispSpecimens;

    }

    public static ShipmentInfoSaveInfo prepareShipInfo(
        ShipmentInfoWrapper shipmentInfo) {
        if (shipmentInfo == null) return null;
        ShipmentInfoSaveInfo si = new ShipmentInfoSaveInfo(
            shipmentInfo.getId(), shipmentInfo.getBoxNumber(), shipmentInfo.getPackedAt(),
            shipmentInfo.getReceivedAt(), shipmentInfo.getWaybill(),
            shipmentInfo.getShippingMethod().getId());
        return si;
    }
}
