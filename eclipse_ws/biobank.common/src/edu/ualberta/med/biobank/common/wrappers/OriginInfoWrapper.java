package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.CheckUnique;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
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

    private static class OriginInfoFromClinicPostCheck extends
        BiobankWrapperAction<OriginInfo> {
        private static final long serialVersionUID = 1L;
        private static final Collection<Property<?, ? super OriginInfo>> UNIQUE_WAYBILL_PER_CENTER_PROPERTIES = new ArrayList<Property<?, ? super OriginInfo>>();

        static {
            UNIQUE_WAYBILL_PER_CENTER_PROPERTIES
                .add(OriginInfoPeer.SHIPMENT_INFO.to(ShipmentInfoPeer.WAYBILL));
            UNIQUE_WAYBILL_PER_CENTER_PROPERTIES.add(OriginInfoPeer.CENTER);
        }

        private final BiobankSessionAction checkUniqueWaybillPerCenter;

        protected OriginInfoFromClinicPostCheck(OriginInfoWrapper wrapper) {
            super(wrapper);

            this.checkUniqueWaybillPerCenter = new CheckUnique<OriginInfo>(
                wrapper, UNIQUE_WAYBILL_PER_CENTER_PROPERTIES);
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            // COOL-BEANS?
            // Query query = session.createQuery("");
            // query.setCacheable(false);
            // query.setCacheMode(CacheMode.IGNORE);

            // TODO: if this works, then extend BiobankWrapperAction with
            // BiobankWrapperCheck and then override a doCheck(Session session)
            // method so that checks will never touch the cache! :-) Also,
            // perhaps auto-supply or load a new re-attached version of the
            // object for a post-check?
            CacheMode oldCacheMode = session.getCacheMode();

            try {
                session.setCacheMode(CacheMode.IGNORE);
                doChecks(session);
            } finally {
                session.setCacheMode(oldCacheMode);
            }

            return null;
        }

        private void doChecks(Session session) throws BiobankSessionException {
            Object obj = session.load(getModelClass(), getModelId());
            OriginInfo originInfo = (OriginInfo) obj;
            Center center = originInfo.getCenter();

            if (!(center instanceof Clinic)) {
                return;
            }
            Clinic clinic = (Clinic) center;

            ShipmentInfo shipmentInfo = originInfo.getShipmentInfo();
            if (shipmentInfo == null) {
                return;
            }

            String waybill = shipmentInfo.getWaybill();

            if (Boolean.TRUE.equals(clinic.getSendsShipments())) {
                if (waybill == null || waybill.isEmpty()) {
                    throw new BiobankSessionException(
                        "A waybill should be set on this shipment");
                }

                checkUniqueWaybillPerCenter.doAction(session);
                // TODO: replace above with appropriate exception String (as
                // found below)
                // if (!checkWaybillUniqueForClinic(clinic)) {
                // throw new BiobankCheckException("A shipment with waybill "
                // + waybill + " already exist in clinic "
                // + clinic.getNameShort());
                // }
            } else {
                if (waybill != null) {
                    throw new BiobankSessionException(
                        "This clinic does not send shipments: waybill should not be set.");
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

        tasks.add(new OriginInfoFromClinicPostCheck(this));

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
