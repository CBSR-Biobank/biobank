package edu.ualberta.med.biobank.common.action.originInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

/**
 * Used to save a shipment from a clinic that does not have access to the Biobank software.
 * 
 * @author Aaron Young
 * 
 */
public class OriginInfoSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc INVALID_STUDY_ERRMSG =
        bundle.trnc(
            "error",
            "This centre is not allowed to collect specimens from study: {0}",
            "This centre is not allowed to collect specimens from studies: {0}");

    @SuppressWarnings("nls")
    public static final LString NULL_SPECIMEN_ID_ERRMSG =
        bundle.tr("Specimen id can not be null").format();

    private final OriginInfoSaveInfo originSaveInfo;

    private final ShipmentInfoSaveInfo shipmentSaveInfo;

    public OriginInfoSaveAction(
        OriginInfoSaveInfo originSaveInfo,
        ShipmentInfoSaveInfo shipmentSaveInfo) {
        this.originSaveInfo = originSaveInfo;
        this.shipmentSaveInfo = shipmentSaveInfo;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean allowed = new OriginInfoUpdatePermission(originSaveInfo.siteId).isAllowed(context);
        if (!allowed) return false;

        Clinic clinic = context.get(Clinic.class, originSaveInfo.centerId);
        if (clinic != null) {
            Set<Study> clinicStudies = new HashSet<Study>();

            for (Contact contact : clinic.getContacts()) {
                clinicStudies.addAll(contact.getStudies());
            }

            // get studies the added specimens come from
            Set<Study> specimenStudies = new HashSet<Study>();

            if (originSaveInfo.addedSpecIds != null) {
                for (Integer specimenId : originSaveInfo.addedSpecIds) {
                    if (specimenId != null) {
                        Specimen specimen = context.get(Specimen.class, specimenId);
                        if (specimen != null) {
                            specimenStudies.add(specimen.getCollectionEvent().getPatient().getStudy());
                        }
                    }
                }
            }

            if (!clinicStudies.containsAll(specimenStudies)) {
                Set<String> studyNames = new HashSet<String>();
                for (Study study : specimenStudies) {
                    if (!clinicStudies.contains(study)) {
                        studyNames.add(study.getNameShort());
                    }
                }

                LString msg;
                String nameList = StringUtils.join(studyNames, ", ");
                if (studyNames.size() == 1) {
                    msg = INVALID_STUDY_ERRMSG.singular(nameList);
                } else {
                    msg = INVALID_STUDY_ERRMSG.plural(nameList);
                }
                throw new LocalizedException(msg);
            }
        }

        return allowed;
    }

    @SuppressWarnings("nls")
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        OriginInfo oi = context.get(OriginInfo.class, originSaveInfo.oiId, new OriginInfo());

        oi.setReceiverCenter(context.get(Center.class, originSaveInfo.siteId));
        oi.setCenter(context.get(Center.class, originSaveInfo.centerId));

        ShipmentInfo si = context.get(ShipmentInfo.class, shipmentSaveInfo.siId, new ShipmentInfo());
        si.setBoxNumber(shipmentSaveInfo.boxNumber);
        si.setPackedAt(shipmentSaveInfo.packedAt);
        si.setReceivedAt(shipmentSaveInfo.receivedAt);
        si.setWaybill(shipmentSaveInfo.waybill);

        ShippingMethod sm = context.load(ShippingMethod.class, shipmentSaveInfo.shippingMethodId);

        si.setShippingMethod(sm);

        // This stuff could be extracted to a util method. need to think about
        // how
        if ((originSaveInfo.comment != null) && !originSaveInfo.comment.trim().equals("")) {
            Set<Comment> comments = oi.getComments();
            if (comments == null) {
                comments = new HashSet<Comment>();
            }
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(originSaveInfo.comment);
            newComment.setUser(context.getUser());
            context.getSession().saveOrUpdate(newComment);

            comments.add(newComment);
            oi.setComments(comments);
        }

        oi.setShipmentInfo(si);

        context.getSession().saveOrUpdate(oi);
        context.getSession().flush();

        if (originSaveInfo.removedSpecIds != null) {
            for (Integer specId : originSaveInfo.removedSpecIds) {
                if (specId == null) {
                    throw new LocalizedException(NULL_SPECIMEN_ID_ERRMSG);
                }
                Specimen spec = context.load(Specimen.class, specId);
                Center center = context.load(Center.class, originSaveInfo.siteId);
                OriginInfo newOriginInfo = new OriginInfo();
                newOriginInfo.setCenter(center);
                spec.setOriginInfo(newOriginInfo);
                spec.setCurrentCenter(center);
                context.getSession().saveOrUpdate(newOriginInfo);
                context.getSession().saveOrUpdate(spec);
            }
        }

        if (originSaveInfo.addedSpecIds != null) {
            for (Integer specId : originSaveInfo.addedSpecIds) {
                if (specId == null) {
                    throw new LocalizedException(NULL_SPECIMEN_ID_ERRMSG);
                }
                Specimen spec = context.load(Specimen.class, specId);
                spec.setOriginInfo(oi);
                spec.setCurrentCenter(oi.getReceiverCenter());
                context.getSession().saveOrUpdate(spec);
            }
        }

        return new IdResult(oi.getId());
    }
}
