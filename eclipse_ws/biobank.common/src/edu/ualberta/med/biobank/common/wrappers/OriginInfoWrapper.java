package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.OriginInfoFromClinicCheck;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {

    private static final String SHIPMENT_HQL_STRING = "from "
        + OriginInfo.class.getName() + " as o inner join fetch o."
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as s ";

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
        StringBuilder qry = new StringBuilder(SHIPMENT_HQL_STRING + " where s."
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
            + " where DATE(s." + ShipmentInfoPeer.RECEIVED_AT.getName()
            + ") = DATE(?)");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { dateReceived }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    public static List<OriginInfoWrapper> getShipmentsByDateSent(
        WritableApplicationService appService, Date dateSent)
        throws ApplicationException {

        StringBuilder qry = new StringBuilder(SHIPMENT_HQL_STRING
            + " where DATE(s." + ShipmentInfoPeer.PACKED_AT.getName()
            + ") = DATE(?)");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { dateSent }));

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
        ShipmentInfoWrapper shipInfo = getShipmentInfo();
        if (shipInfo == null) {
            // nothing to log since origin info does not yet point to any
            // shipping information
            return null;
        }

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

        detailsList.add(new StringBuilder("waybill:").append(
            shipInfo.getWaybill()).toString());
        detailsList.add(new StringBuilder("specimens:").append(
            getSpecimenCollection(false).size()).toString());
        log.setDetails(StringUtils.join(detailsList, ", "));
        log.setType("Shipment");
        return log;
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().notNull(OriginInfoPeer.CENTER));

        tasks.add(super.getPersistTasks());

        tasks.add(new OriginInfoFromClinicCheck(this));

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        tasks.add(super.getDeleteTasks());

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}
