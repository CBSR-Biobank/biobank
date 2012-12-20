package edu.ualberta.med.biobank.action.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.center.Shipment;

public class SpecimenTransitSearchAction implements
    Action<ListResult<IBiobankModel>> {

    private static final long serialVersionUID = 1L;
    private String waybill;
    private Date received;
    private Date packed;
    private final Integer currentSite;

    public SpecimenTransitSearchAction(Integer currentSite) {
        this.currentSite = currentSite;
    }

    @SuppressWarnings("nls")
    private static final String SHIPMENT_HQL_STRING = "from "
        + OriginInfo.class.getName() + " o " +
        "inner join fetch o.shipmentInfo as s "
        + " inner join fetch o.center inner join fetch o.receiverSite";

    @SuppressWarnings("nls")
    private static final String DISPATCH_HQL_STRING = "from "
        + Shipment.class.getName() + " as o "
        + "inner join fetch o.shipmentInfo as s "
        + "inner join fetch o.senderCenter "
        + "inner join fetch o.receiverCenter";

    @SuppressWarnings("nls")
    private static final String SHIPMENTS_BY_WAYBILL_QRY = SHIPMENT_HQL_STRING
        + " where s.waybill=?";

    @SuppressWarnings("nls")
    private static final String DISPATCHES_BY_WAYBILL_QRY = DISPATCH_HQL_STRING
        + " where s." + ShipmentInfoPeer.WAYBILL.getName() + " = ?";

    @SuppressWarnings("nls")
    private static final String SHIPMENTS_BY_DATE_PACKED_QRY =
        SHIPMENT_HQL_STRING
            + " where s."
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " >= ? and s."
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " < ? and (o."
            + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
            + "= ? or o."
            + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
            + " = ?)";

    @SuppressWarnings("nls")
    private static final String DISPATCHES_BY_DATE_PACKED_QRY =
        DISPATCH_HQL_STRING
            + " where s."
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " >= ? and s."
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " < ? and (o."
            + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
            + "= ? or o."
            + Property.concatNames(DispatchPeer.RECEIVER_CENTER,
                CenterPeer.ID)
            + " = ?)";

    @SuppressWarnings("nls")
    private static final String SHIPMENTS_BY_DATE_RECEIVED_QRY =
        SHIPMENT_HQL_STRING
            + " where s."
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " >= ? and s."
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " < ? and (o."
            + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
            + "= ? or o."
            + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
            + " = ?)";

    @SuppressWarnings("nls")
    private static final String DISPATCHES_BY_DATE_RECEIVED_QRY =
        DISPATCH_HQL_STRING
            + " where s."
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " >= ? and s."
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " < ? and (o."
            + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
            + "= ? or o."
            + Property.concatNames(DispatchPeer.RECEIVER_CENTER,
                CenterPeer.ID)
            + " = ?)";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<IBiobankModel> run(ActionContext context)
        throws ActionException {
        List<IBiobankModel> results = new ArrayList<IBiobankModel>();
        Query q1, q2;
        if (waybill != null) {
            q1 = context.getSession().createQuery(SHIPMENTS_BY_WAYBILL_QRY);
            q2 = context.getSession().createQuery(DISPATCHES_BY_WAYBILL_QRY);
            q1.setParameter(0, waybill);
            q2.setParameter(0, waybill);
        }
        else if (received != null) {
            q1 =
                context.getSession().createQuery(
                    SHIPMENTS_BY_DATE_RECEIVED_QRY);
            q2 =
                context.getSession().createQuery(
                    DISPATCHES_BY_DATE_RECEIVED_QRY);
            q1.setParameter(0, DateUtil.startOfDay(received));
            q1.setParameter(1, DateUtil.endOfDay(received));
            q1.setParameter(2, currentSite);
            q1.setParameter(3, currentSite);
            q2.setParameter(0, DateUtil.startOfDay(received));
            q2.setParameter(1, DateUtil.endOfDay(received));
            q2.setParameter(2, currentSite);
            q2.setParameter(3, currentSite);
        }
        else {
            q1 = context.getSession().createQuery(SHIPMENTS_BY_DATE_PACKED_QRY);
            q2 =
                context.getSession().createQuery(DISPATCHES_BY_DATE_PACKED_QRY);
            q1.setParameter(0, DateUtil.startOfDay(packed));
            q1.setParameter(1, DateUtil.endOfDay(packed));
            q1.setParameter(2, currentSite);
            q1.setParameter(3, currentSite);
            q2.setParameter(0, DateUtil.startOfDay(packed));
            q2.setParameter(1, DateUtil.endOfDay(packed));
            q2.setParameter(2, currentSite);
            q2.setParameter(3, currentSite);

        }
        results.addAll(q1.list());
        results.addAll(q2.list());
        return new ListResult<IBiobankModel>(results);
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public void setDateReceived(Date received) {
        this.received = received;
    }

    public void setDatePacked(Date packed) {
        this.packed = packed;
    }
}
