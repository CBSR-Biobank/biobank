package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ShipmentInfoBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class ShipmentInfoWrapper extends ShipmentInfoBaseWrapper {

    public ShipmentInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentInfoWrapper(WritableApplicationService appService,
        ShipmentInfo ship) {
        super(appService, ship);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ShipmentInfoPeer.PROP_NAMES;
    }

    public String getFormattedDateReceived() {
        // date received is not supposed to be null
        return DateFormatter.formatAsDateTime(getReceivedAt());
    }

    @Override
    public int compareTo(ModelWrapper<ShipmentInfo> wrapper) {
        if (wrapper instanceof ShipmentInfoWrapper) {
            Date v1Date = wrappedObject.getReceivedAt();
            Date v2Date = wrapper.wrappedObject.getReceivedAt();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        String s = getFormattedDateReceived();
        if (getWaybill() != null) {
            s += " (" + getWaybill() + ")";
        }
        return s;
    }

    public boolean isReceivedToday() {
        Calendar cal = Calendar.getInstance();
        // yesterday midnight
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        // today midnight
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        Date dateReveived = getReceivedAt();
        return dateReveived.compareTo(startDate) >= 0
            && dateReveived.compareTo(endDate) <= 0;
    }

    private static final String WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY = "from "
        + Clinic.class.getName() + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName() + " as oi join oi."
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as si where clinic."
        + ClinicPeer.ID.getName() + "=? and si."
        + ShipmentInfoPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForClinic(ClinicWrapper clinic)
        throws ApplicationException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        params.add(clinic.getId());
        params.add(getWaybill());

        StringBuilder qry = new StringBuilder(
            WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY);
        if (!isNew()) {
            qry.append(" and ce.").append(CollectionEventPeer.ID.getName())
                .append(" <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY
            + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public void checkAtLeastOneSpecimen() throws BiobankCheckException {
        List<SpecimenWrapper> spc = getOriginInfo()
            .getSpecimenCollection(false);
        if (spc == null || spc.isEmpty()) {
            throw new BiobankCheckException(
                "At least one specimen should be added to this Collection Event.");
        }
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        CenterWrapper<?> center = getOriginInfo().getCenter();
        if (center == null) {
            throw new BiobankCheckException("A Center should be set.");
        }
        checkAtLeastOneSpecimen();

        if (center instanceof ClinicWrapper) {
            ClinicWrapper clinic = (ClinicWrapper) center;

            if (Boolean.TRUE.equals(clinic.getSendsShipments())) {
                if (getWaybill() == null || getWaybill().isEmpty()) {
                    throw new BiobankCheckException(
                        "A waybill should be set on this shipment");
                }
                if (!checkWaybillUniqueForClinic(clinic)) {
                    throw new BiobankCheckException(
                        "A collection event with waybill " + getWaybill()
                            + " already exist in clinic "
                            + clinic.getNameShort());
                }
            } else {
                if (getWaybill() != null) {
                    throw new BiobankCheckException(
                        "This clinic doesn't send shipments: waybill should not be set");
                }
            }
        }
    }
}
