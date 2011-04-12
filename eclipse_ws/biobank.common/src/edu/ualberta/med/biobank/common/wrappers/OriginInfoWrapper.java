package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {

    private static final String SHIPMENT_HQL_STRING = "from "
        + OriginInfo.class.getName() + " as o inner join fetch o."
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as s where s is not null";

    public OriginInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OriginInfoWrapper(WritableApplicationService appService,
        OriginInfo originInfo) {
        super(appService, originInfo);
    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

    public List<PatientWrapper> getPatientCollection() {
        List<SpecimenWrapper> specimens = getSpecimenCollection();
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();

        for (SpecimenWrapper specimen : specimens) {
            PatientWrapper patient = specimen.getCollectionEvent().getPatient();

            if (!patients.contains(patient)) {
                patients.add(patient);
            }
        }

        return patients;
    }

    public void checkAtLeastOneSpecimen() {
        // FIXME don't want that when create from collection event
        // List<SpecimenWrapper> spc = getSpecimenCollection(false);
        // if (spc == null || spc.isEmpty()) {
        // throw new BiobankCheckException(
        // "At least one specimen should be added to this Collection Event.");
        // }
    }

    private static final String WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY = "from "
        + Clinic.class.getName() + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName() + " as oi join oi."
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as si where clinic."
        + ClinicPeer.ID.getName() + "=? and si."
        + ShipmentInfoPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForClinic(ClinicWrapper clinic)
        throws ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(clinic.getId());
        params.add(getShipmentInfo().getWaybill());

        StringBuilder qry = new StringBuilder(
            WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY);
        if (!isNew()) {
            qry.append(" and oi.").append(OriginInfoPeer.ID.getName())
                .append(" <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(qry.toString(), params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        CenterWrapper<?> center = getCenter();
        if (center == null) {
            throw new BiobankCheckException("A Center should be set.");
        }
        checkAtLeastOneSpecimen();

        if (center instanceof ClinicWrapper && getShipmentInfo() != null) {
            ClinicWrapper clinic = (ClinicWrapper) center;
            String waybill = getShipmentInfo().getWaybill();

            if (Boolean.TRUE.equals(clinic.getSendsShipments())) {
                if (waybill == null || waybill.isEmpty()) {
                    throw new BiobankCheckException(
                        "A waybill should be set on this shipment");
                }
                if (!checkWaybillUniqueForClinic(clinic)) {
                    throw new BiobankCheckException(
                        "A collection event with waybill " + waybill
                            + " already exist in clinic "
                            + clinic.getNameShort());
                }
            } else {
                if (waybill != null) {
                    throw new BiobankCheckException(
                        "This clinic doesn't send shipments: waybill should not be set");
                }
            }
        }

    }

    public static List<OriginInfoWrapper> getTodayShipments(
        BiobankApplicationService appService) throws ApplicationException {
        return getShipmentsByDateReceived(appService, new Date());
    }

    /**
     * Search for shipments in the site with the given waybill
     */
    public static List<OriginInfoWrapper> getShipmentsByWaybill(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {
        StringBuilder qry = new StringBuilder(SHIPMENT_HQL_STRING + " and s."
            + ShipmentInfoPeer.WAYBILL.getName() + " = ?");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { waybill }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute.
     */
    public static List<OriginInfoWrapper> getShipmentsByDateReceived(
        WritableApplicationService appService, Date dateReceived)
        throws ApplicationException {

        StringBuilder qry = new StringBuilder(SHIPMENT_HQL_STRING
            + " and DATE(s." + ShipmentInfoPeer.RECEIVED_AT.getName()
            + ") = DATE(?)");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { dateReceived }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    // @SuppressWarnings("unchecked")
    // @Override
    // jmf: Cannot just return the origin center becaus then the receivers
    // cannot edit the shipment
    // public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
    // if (getCenter() != null)
    // return Arrays.asList(getCenter());
    // return super.getSecuritySpecificCenters();
    // }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setCenter(getCenter().getNameShort());
        } else {
            log.setCenter(site);
        }

        List<String> detailsList = new ArrayList<String>();
        if (details.length() > 0) {
            detailsList.add(details);
        }

        ShipmentInfoWrapper shipInfo = getShipmentInfo();
        if (shipInfo != null) {
            detailsList.add(new StringBuilder("waybill:").append(
                shipInfo.getWaybill()).toString());
        }
        detailsList.add(new StringBuilder("specimens:").append(
            getSpecimenCollection(false).size()).toString());
        log.setDetails(StringUtils.join(detailsList, ", "));
        log.setType("Shipment");
        return log;
    }

    public String getWaybill() {
        ShipmentInfoWrapper shipInfo = getShipmentInfo();
        return shipInfo != null ? shipInfo.getWaybill() : null;
    }

    public void setWaybill(String waybill) {
        getShipmentInfo().setWaybill(waybill);
    }
}
