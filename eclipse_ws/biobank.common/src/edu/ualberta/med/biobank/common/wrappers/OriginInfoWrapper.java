package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.OriginInfoFromClinicCheck;
import edu.ualberta.med.biobank.common.wrappers.loggers.OriginInfoLogProvider;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {
    private static final OriginInfoLogProvider LOG_PROVIDER = new OriginInfoLogProvider();
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

    @Override
    protected void deleteDependencies() throws Exception {
        // all specimen should be linked to another origin info. This origin
        // info center will be the specimen current center.
        for (SpecimenWrapper spc : getSpecimenCollection()) {
            OriginInfoWrapper oi = new OriginInfoWrapper(appService);
            oi.setCenter(spc.getCurrentCenter());
            oi.persist();
            spc.setOriginInfo(oi);
            spc.persist();
        }
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
        BiobankApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        return getShipmentsByDateReceived(appService, new Date(), center);
    }

    private static final String SHIPMENTS_BY_WAYBILL_QRY = SHIPMENT_HQL_STRING
        + " where s." + ShipmentInfoPeer.WAYBILL.getName() + " = ?";

    /**
     * Search for shipments in the site with the given waybill
     */
    public static List<OriginInfoWrapper> getShipmentsByWaybill(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {

        HQLCriteria criteria = new HQLCriteria(SHIPMENTS_BY_WAYBILL_QRY,
            Arrays.asList(new Object[] { waybill }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    // Don't run this on the server unless you take into account timezones in
    // your inputs
    private static final String SHIPMENTS_BY_DATE_RECEIVED_QRY = SHIPMENT_HQL_STRING
        + " where s."
        + ShipmentInfoPeer.RECEIVED_AT.getName()
        + " >= ? and s."
        + ShipmentInfoPeer.RECEIVED_AT.getName()
        + " <= ? and (o."
        + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
        + "= ? or o."
        + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
        + " = ?)";

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute. Will check the given center is either the sender or the
     * receiver.
     */
    public static List<OriginInfoWrapper> getShipmentsByDateReceived(
        WritableApplicationService appService, Date dateReceived,
        CenterWrapper<?> center) throws ApplicationException {

        Integer centerId = center.getId();
        HQLCriteria criteria = new HQLCriteria(SHIPMENTS_BY_DATE_RECEIVED_QRY,
            Arrays.asList(new Object[] { dateReceived, endOfDay(dateReceived),
                centerId, centerId }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    // Don't run this on the server unless you take into account timezones in
    // your inputs
    private static final String SHIPMENTS_BY_DATE_SENT_QRY = SHIPMENT_HQL_STRING
        + " where s."
        + ShipmentInfoPeer.PACKED_AT.getName()
        + " >= ? and s."
        + ShipmentInfoPeer.PACKED_AT.getName()
        + " <= ? and (o."
        + Property.concatNames(OriginInfoPeer.CENTER, CenterPeer.ID)
        + "= ? or o."
        + Property.concatNames(OriginInfoPeer.RECEIVER_SITE, CenterPeer.ID)
        + " = ?)";

    public static List<OriginInfoWrapper> getShipmentsByDateSent(
        WritableApplicationService appService, Date dateSent,
        CenterWrapper<?> center) throws ApplicationException {
        Integer centerId = center.getId();
        HQLCriteria criteria = new HQLCriteria(SHIPMENTS_BY_DATE_SENT_QRY,
            Arrays.asList(new Object[] { dateSent, endOfDay(dateSent),
                centerId, centerId }));
        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    /**
     * security specific to the 2 centers involved in the shipment
     */
    @Override
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        if (getCenter() != null)
            centers.add(getCenter());
        if (getReceiverSite() != null)
            centers.add(getReceiverSite());
        return centers;
    }

    // Date should input with no hour/minute/seconds
    public static Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    @Override
    public OriginInfoLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(OriginInfoPeer.CENTER));

        super.addPersistTasks(tasks);

        tasks.add(new OriginInfoFromClinicCheck(this));
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        super.addDeleteTasks(tasks);
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
