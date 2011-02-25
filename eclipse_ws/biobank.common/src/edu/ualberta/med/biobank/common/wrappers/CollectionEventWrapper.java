package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class CollectionEventWrapper extends CollectionEventBaseWrapper {

    private Set<SourceVesselWrapper> deletedSourceVessels = new HashSet<SourceVesselWrapper>();

    public CollectionEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public void addToSourceVesselCollection(
        List<SourceVesselWrapper> sourceVesselCollection) {
        super.addToSourceVesselCollection(sourceVesselCollection);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedSourceVessels.removeAll(sourceVesselCollection);
    }

    @Override
    public void removeFromSourceVesselCollection(
        List<SourceVesselWrapper> sourceVesselCollection) {
        deletedSourceVessels.addAll(sourceVesselCollection);
        super.removeFromSourceVesselCollection(sourceVesselCollection);
    }

    @Override
    public void removeFromSourceVesselCollectionWithCheck(
        List<SourceVesselWrapper> sourceVesselCollection)
        throws BiobankCheckException {
        deletedSourceVessels.addAll(sourceVesselCollection);
        super.removeFromSourceVesselCollectionWithCheck(sourceVesselCollection);
    }

    private void deleteSourceVessels() throws Exception {
        for (SourceVesselWrapper sv : deletedSourceVessels) {
            if (!sv.isNew()) {
                sv.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkNoMoreSourceVessels();
    }

    private void checkNoMoreSourceVessels() throws BiobankCheckException {
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        if (sourceVessels != null && sourceVessels.size() > 0) {
            throw new BiobankCheckException(
                "Source Vessels are still linked to this Collection Event. Delete them before attempting to remove this Collection Event");
        }
    }

    private static final String WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY = "from "
        + Clinic.class.getName() + " as clinic join clinic."
        + ClinicPeer.COLLECTION_EVENT_COLLECTION.getName()
        + " as ce where clinic." + ClinicPeer.ID.getName() + "=? and ce."
        + CollectionEventPeer.WAYBILL.getName() + "=?";

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

    public void checkPatientsStudy(ClinicWrapper clinic)
        throws BiobankException, ApplicationException {
        List<String> patientsInError = new ArrayList<String>();
        for (SourceVesselWrapper sv : getSourceVesselCollection(false)) {
            PatientWrapper patient = sv.getPatient();
            if (!patient.canBeAddedToCollectionEvent(this)) {
                patientsInError.add(patient.getPnumber());
            }
        }
        if (!patientsInError.isEmpty()) {
            throw new BiobankCheckException("Patient(s) "
                + StringUtils.join(patientsInError, ", ")
                + " are not part of a study that has contact with clinic "
                + clinic.getName());
        }
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        CenterWrapper<?> center = getSourceCenter();
        if (center == null) {
            throw new BiobankCheckException("A Center should be set.");
        }
        checkAtLeastOneSouceVessel();

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
            checkPatientsStudy(clinic);
        }
    }

    @Override
    protected void persistDependencies(CollectionEvent origObject)
        throws Exception {
        deleteSourceVessels();
    }

    public void checkAtLeastOneSouceVessel() throws BiobankCheckException {
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        if (sourceVessels == null || sourceVessels.isEmpty()) {
            throw new BiobankCheckException(
                "At least one Source Vessel should be added to this Collection Event.");
        }
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setSite(getSourceCenter().getNameShort());
        } else {
            log.setSite(site);
        }
        details += "Received:" + getFormattedDateReceived();
        String waybill = getWaybill();
        if (waybill != null) {
            details += " - Waybill:" + waybill;
        }
        log.setDetails(details);
        log.setType("Shipment");
        return log;
    }

    public Boolean needDeparted() {
        ShippingMethodWrapper shippingMethod = getShippingMethod();
        return shippingMethod == null || shippingMethod.needDate();
    }

    public List<PatientWrapper> getPatientCollection() {
        Collection<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        for (SourceVesselWrapper sourceVessel : sourceVessels) {
            PatientWrapper patient = sourceVessel.getPatient();
            patients.add(patient);
        }
        return patients;
    }

    private static final String COLLECTION_EVENTS_BY_WAYBILL_QRY = "from "
        + CollectionEvent.class.getName() + " ce where ce."
        + CollectionEventPeer.WAYBILL.getName() + "=?";

    public static List<CollectionEventWrapper> getCollectionEvents(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(COLLECTION_EVENTS_BY_WAYBILL_QRY,
            Arrays.asList(new Object[] { waybill }));
        List<CollectionEvent> raw = appService.query(c);
        if (raw == null) {
            return new ArrayList<CollectionEventWrapper>();
        }
        return wrapModelCollection(appService, raw,
            CollectionEventWrapper.class);
    }

    private static final String COLLECTION_EVENTS_BY_DATE_RECEIVED_QRY = "from "
        + CollectionEvent.class.getName()
        + " ce where ce."
        + CollectionEventPeer.DATE_RECEIVED.getName() + "=?";

    public static List<CollectionEventWrapper> getCollectionEvents(
        WritableApplicationService appService, Date dateReceived)
        throws ApplicationException {
        List<CollectionEvent> raw = appService.query(new HQLCriteria(
            COLLECTION_EVENTS_BY_DATE_RECEIVED_QRY, Arrays
                .asList(new Object[] { dateReceived })));
        if (raw == null) {
            return new ArrayList<CollectionEventWrapper>();
        }
        return wrapModelCollection(appService, raw,
            CollectionEventWrapper.class);
    }

    public boolean hasPatient(String pnum) {
        List<SourceVesselWrapper> svs = getSourceVesselCollection(false);
        for (SourceVesselWrapper sv : svs)
            if (sv.getPatient().getPnumber().equals(pnum))
                return true;
        return false;
    }

    private static final String TODAYS_COLLECTION_EVENTS = "from "
        + CollectionEvent.class.getName() + " ce where ce."
        + CollectionEventPeer.DATE_RECEIVED.getName() + "=?";

    public static List<CollectionEventWrapper> getTodayCollectionEvents(
        WritableApplicationService appService) throws ApplicationException {
        List<CollectionEvent> raw = appService.query(new HQLCriteria(
            TODAYS_COLLECTION_EVENTS, Arrays
                .asList(new Object[] { new Date() })));
        if (raw == null) {
            return new ArrayList<CollectionEventWrapper>();
        }
        return wrapModelCollection(appService, raw,
            CollectionEventWrapper.class);
    }

    @Deprecated
    public static List<? extends ModelWrapper<?>> getTodayShipments(
        BiobankApplicationService appService) {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public ClinicWrapper getClinic() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public static List<CollectionEventWrapper> getShipmentsInSites(
        BiobankApplicationService appService, String trim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public static List<? extends ModelWrapper<?>> getShipmentsInSites(
        BiobankApplicationService appService, Date date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public SiteWrapper getSite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void addPatients(List<PatientWrapper> asList) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void checkCanRemovePatient(PatientWrapper patient) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void removePatients(List<PatientWrapper> asList) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void checkPatientsStudy() {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void setClinic(ClinicWrapper selectedObject) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void setSite(SiteWrapper selectedSite) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public Collection<? extends ModelWrapper<?>> getProcessingEventCollection() {
        // TODO new but not yet in model
        return null;
    }

    @Deprecated
    public PatientWrapper getPatient() {
        // TODO new but not yet in model
        return null;
    }

    @Deprecated
    public String getFormattedDateProcessed() {
        // TODO new but not yet in model
        return null;
    }

    @Deprecated
    public String getFormattedDateDrawn() {
        // TODO new but not yet in model
        return null;
    }

    public List<SourceVesselWrapper> getSourceVesselCollection() {
        return getSourceVesselCollection(false);
    }

    @Deprecated
    public String getPvAttrValue(String label) {
        return label;
        // will be added in new model
    }

    @Deprecated
    public Integer getVisitNumber() {
        // TODO new but not yet in model
        return null;
    }

    @Deprecated
    public void setPatient(PatientWrapper wrapper) {
        // will be added in new model

    }

    @Deprecated
    public Date getDateDrawn() {
        return null;
        // will be added in new model
    }

    public Integer getOriginalSpecimensCount() {
        // TODO new but not yet in model
        return null;
    }

    @Deprecated
    public void setPvAttrValue(String label, String value) {
        // will be added in new model

    }

    @Deprecated
    public List<AliquotWrapper> getSpecimenCollection() {
        return null;
        // will be added in new model
    }

    public Integer getAliquotedSpecimensCount() {
        // TODO new but not yet in model
        return null;
    }
}
