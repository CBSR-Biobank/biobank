package edu.ualberta.med.biobank.common.action.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.util.DateUtil;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.OriginInfo;

public class SpecimenTransitSearchAction implements
    Action<ListResult<IBiobankModel>> {

    private static final long serialVersionUID = 1L;
    private String waybill;
    private Date received;
    private Date packed;
    private Integer currentSite;

    public SpecimenTransitSearchAction(Integer currentSite) {
        this.currentSite = currentSite;
    }

    private static final String SHIPMENT_HQL_STRING = "from " //$NON-NLS-1$
        + OriginInfo.class.getName() + " as o inner join fetch o." //$NON-NLS-1$
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as s " //$NON-NLS-1$
        + " inner join fetch o.center inner join fetch o.receiverSite"; //$NON-NLS-1$

    private static final String DISPATCH_HQL_STRING = "from " //$NON-NLS-1$
        + Dispatch.class.getName() + " as o inner join fetch o." //$NON-NLS-1$
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as s "
        + " inner join fetch o.senderCenter inner join fetch o.receiverCenter"; //$NON-NLS-1$

    private static final String SHIPMENTS_BY_WAYBILL_QRY = SHIPMENT_HQL_STRING
        + " where s." + ShipmentInfoPeer.WAYBILL.getName() + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String DISPATCHES_BY_WAYBILL_QRY = DISPATCH_HQL_STRING
        + " where s." + ShipmentInfoPeer.WAYBILL.getName() + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String SHIPMENTS_BY_DATE_PACKED_QRY =
        SHIPMENT_HQL_STRING
            + " where s." //$NON-NLS-1$
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " >= ? and s." //$NON-NLS-1$
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " < ? and (o." //$NON-NLS-1$
            + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
            + "= ? or o." //$NON-NLS-1$
            + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
            + " = ?)"; //$NON-NLS-1$

    private static final String DISPATCHES_BY_DATE_PACKED_QRY =
        DISPATCH_HQL_STRING
            + " where s." //$NON-NLS-1$
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " >= ? and s." //$NON-NLS-1$
            + ShipmentInfoPeer.PACKED_AT.getName()
            + " < ? and (o." //$NON-NLS-1$
            + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
            + "= ? or o." //$NON-NLS-1$
            + Property.concatNames(DispatchPeer.RECEIVER_CENTER,
                CenterPeer.ID)
            + " = ?)"; //$NON-NLS-1$

    private static final String SHIPMENTS_BY_DATE_RECEIVED_QRY =
        SHIPMENT_HQL_STRING
            + " where s." //$NON-NLS-1$
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " >= ? and s." //$NON-NLS-1$
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " < ? and (o." //$NON-NLS-1$
            + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
            + "= ? or o." //$NON-NLS-1$
            + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
            + " = ?)"; //$NON-NLS-1$

    private static final String DISPATCHES_BY_DATE_RECEIVED_QRY =
        DISPATCH_HQL_STRING
            + " where s." //$NON-NLS-1$
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " >= ? and s." //$NON-NLS-1$
            + ShipmentInfoPeer.RECEIVED_AT.getName()
            + " < ? and (o." //$NON-NLS-1$
            + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
            + "= ? or o." //$NON-NLS-1$
            + Property.concatNames(DispatchPeer.RECEIVER_CENTER,
                CenterPeer.ID)
            + " = ?)"; //$NON-NLS-1$

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

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
