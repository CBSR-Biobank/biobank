package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final OriginInfoLogProvider LOG_PROVIDER =
        new OriginInfoLogProvider();
    private static final String SHIPMENT_HQL_STRING = "from " //$NON-NLS-1$
        + OriginInfo.class.getName() + " as o inner join fetch o." //$NON-NLS-1$
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as s "; //$NON-NLS-1$

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

    @Deprecated
    public void checkAtLeastOneSpecimen() {
    }

    public static List<OriginInfoWrapper> getTodayShipments(
        BiobankApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        return getShipmentsByDateReceived(appService, new Date(), center);
    }

    private static final String SHIPMENTS_BY_WAYBILL_QRY = SHIPMENT_HQL_STRING
        + " where s." + ShipmentInfoPeer.WAYBILL.getName() + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$

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
            Arrays.asList(new Object[] { startOfDay(dateReceived),
                endOfDay(dateReceived), centerId, centerId }));

        List<OriginInfo> origins = appService.query(criteria);
        List<OriginInfoWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, OriginInfoWrapper.class);

        return shipments;
    }

    // Don't run this on the server unless you take into account timezones in
    // your inputs
    private static final String SHIPMENTS_BY_DATE_SENT_QRY =
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

    public static List<OriginInfoWrapper> getShipmentsByDateSent(
        WritableApplicationService appService, Date dateSent,
        CenterWrapper<?> center) throws ApplicationException {
        Integer centerId = center.getId();
        HQLCriteria criteria = new HQLCriteria(SHIPMENTS_BY_DATE_SENT_QRY,
            Arrays.asList(new Object[] { startOfDay(dateSent),
                endOfDay(dateSent), centerId, centerId }));
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

    @Override
    public OriginInfoLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(OriginInfoPeer.CENTER));

        super.addPersistTasks(tasks);

        tasks.add(new OriginInfoFromClinicCheck(this));
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // TODO: this SHOULD NOT be done in the wrappers. It should be done in
        // some other method because this is a highly customized action and is
        // not always correct. For example, if a shipment is deleted after a
        // dispatch, then it will appear that the specimen came from the place
        // it was dispatched to.

        // all specimen should be linked to another origin info. This origin
        // info center will be the specimen current center.
        for (SpecimenWrapper spc : getSpecimenCollection()) {
            OriginInfoWrapper oi = new OriginInfoWrapper(appService);
            oi.setCenter(spc.getCurrentCenter());
            spc.setOriginInfo(oi);

            oi.addPersistAndLogTasks(tasks);
            spc.addPersistAndLogTasks(tasks);
        }

        super.addDeleteTasks(tasks);
    }

    /**
     * Should be addToSpecimenCollection most of the time. But can use this
     * method from tome to tome to reset the collection (used in saving
     * originInfo when want to try to re-add the specimens)
     */
    public void setSpecimenWrapperCollection(List<SpecimenWrapper> specs) {
        setWrapperCollection(OriginInfoPeer.SPECIMEN_COLLECTION, specs);
    }

}
