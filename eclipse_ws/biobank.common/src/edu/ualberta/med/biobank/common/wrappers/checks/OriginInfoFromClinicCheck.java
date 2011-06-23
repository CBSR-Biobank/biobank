package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadAction;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class OriginInfoFromClinicCheck extends LoadAction<OriginInfo> {
    private static final long serialVersionUID = 1L;
    private static final String WAYBILL_REQUIRED_MSG = "A waybill should be set on this shipment.";
    private static final String WAYBILL_NOT_ALLOWED_MSG = "This clinic does not send shipments: waybill should not be set.";
    private static final String WAYBILL_USED_MSG = "A shipment with waybill {0} already exist in clinic {1}.";
    private static final Collection<Property<?, ? super OriginInfo>> UNIQUE_WAYBILL = new ArrayList<Property<?, ? super OriginInfo>>();

    static {
        UNIQUE_WAYBILL.add(OriginInfoPeer.SHIPMENT_INFO
            .to(ShipmentInfoPeer.WAYBILL));
        UNIQUE_WAYBILL.add(OriginInfoPeer.CENTER);
    }

    private final BiobankSessionAction waybillCheck;

    public OriginInfoFromClinicCheck(OriginInfoWrapper wrapper) {
        super(wrapper);
        this.waybillCheck = new UniqueOnSavedCheck<OriginInfo>(wrapper,
            UNIQUE_WAYBILL);
    }

    @Override
    public void onLoad(Session session, OriginInfo originInfo)
        throws BiobankSessionException {
        Center center = originInfo.getCenter();
        ShipmentInfo shipmentInfo = originInfo.getShipmentInfo();

        if (center instanceof Clinic && shipmentInfo != null) {
            checkShipmentInfo(session, (Clinic) center, shipmentInfo);
        }
    }

    private void checkShipmentInfo(Session session, Clinic clinic,
        ShipmentInfo shipmentInfo) throws BiobankSessionException {
        String waybill = shipmentInfo.getWaybill();
        if (clinic.getSendsShipments()) {
            checkWaybillRequired(waybill);
            checkWaybillNotUsed(session, waybill, clinic);
        } else {
            checkWaybillNotAllowed(waybill);
        }
    }

    private void checkWaybillRequired(String waybill)
        throws BiobankSessionException {
        if (waybill == null || waybill.isEmpty()) {
            throw new BiobankSessionException(WAYBILL_REQUIRED_MSG);
        }
    }

    private void checkWaybillNotUsed(Session session, String waybill,
        Clinic clinic) throws BiobankSessionException {
        String clinicName = clinic.getNameShort();
        String msg = MessageFormat
            .format(WAYBILL_USED_MSG, waybill, clinicName);

        try {
            waybillCheck.doAction(session);
        } catch (Exception e) {
            throw new BiobankSessionException(msg, e);
        }
    }

    private void checkWaybillNotAllowed(String waybill)
        throws BiobankSessionException {
        if (waybill != null) {
            throw new BiobankSessionException(WAYBILL_NOT_ALLOWED_MSG);
        }
    }
}