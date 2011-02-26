package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("unused")
public class CollectionEventWrapper extends CollectionEventBaseWrapper {

    private Set<SpecimenWrapper> deletedSpecimens = new HashSet<SpecimenWrapper>();

    public CollectionEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    @Deprecated
    public void addToSourceVesselCollection(
        List<SourceVesselWrapper> sourceVesselCollection) {
    }

    @Deprecated
    public void removeFromSourceVesselCollection(
        List<SpecimenWrapper> sourceVesselCollection) {
    }

    @Deprecated
    public void removeFromSourceVesselCollectionWithCheck(
        List<SourceVesselWrapper> sourceVesselCollection)
        throws BiobankCheckException {
    }

    @Override
    public void addToSpecimenCollection(List<SpecimenWrapper> specimenCollection) {
        super.addToSpecimenCollection(specimenCollection);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedSpecimens.removeAll(specimenCollection);
    }

    @Override
    public void removeFromSpecimenCollection(
        List<SpecimenWrapper> specimenCollection) {
        deletedSpecimens.addAll(specimenCollection);
        super.removeFromSpecimenCollection(specimenCollection);
    }

    @Override
    public void removeFromSpecimenCollectionWithCheck(
        List<SpecimenWrapper> specimenCollection) throws BiobankCheckException {
        deletedSpecimens.addAll(specimenCollection);
        super.removeFromSpecimenCollectionWithCheck(specimenCollection);
    }

    private void deleteSourceVessels() throws Exception {
        for (SpecimenWrapper sv : deletedSpecimens) {
            if (!sv.isNew()) {
                sv.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkNoMoreSpecimens();
    }

    private void checkNoMoreSpecimens() throws BiobankCheckException {
        List<SpecimenWrapper> sourceVessels = getSpecimenCollection(false);
        if (sourceVessels != null && !sourceVessels.isEmpty()) {
            throw new BiobankCheckException(
                "Source Vessels are still linked to this Collection Event. Delete them before attempting to remove this Collection Event");
        }
    }

    public void checkPatientsStudy(ClinicWrapper clinic)
        throws BiobankException, ApplicationException {
        List<String> patientsInError = new ArrayList<String>();
        PatientWrapper patient = getPatient();
        if (!patient.canBeAddedToCollectionEvent(this)) {
            throw new BiobankCheckException("Patient(s) "
                + StringUtils.join(patientsInError, ", ")
                + " are not part of a study that has contact with clinic "
                + clinic.getName());
        }
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        // FIX: how do we know what clinic this CE is for?
        // checkPatientsStudy(clinic);
    }

    @Override
    protected void persistDependencies(CollectionEvent origObject)
        throws Exception {
        deleteSourceVessels();
    }

    public void checkAtLeastOneSpecimen() throws BiobankCheckException {
        List<SpecimenWrapper> spc = getSpecimenCollection(false);
        if (spc == null || spc.isEmpty()) {
            throw new BiobankCheckException(
                "At least one specimen should be added to this Collection Event.");
        }
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        // FIXME: what should be logged here
        //
        // Log log = new Log();
        // log.setAction(action);
        // if (site == null) {
        // log.setSite(getSourceCenter().getNameShort());
        // } else {
        // log.setSite(site);
        // }
        // details += "Received:" + getFormattedDateReceived();
        // String waybill = getWaybill();
        // if (waybill != null) {
        // details += " - Waybill:" + waybill;
        // }
        // log.setDetails(details);
        // log.setType("Shipment");
        // return log;
        return null;
    }

    @Deprecated
    public Boolean needDeparted() {
        // ShippingMethodWrapper shippingMethod = getShippingMethod();
        // return shippingMethod == null || shippingMethod.needDate();
        return false;
    }

    @Deprecated
    public List<PatientWrapper> getPatientCollection() {
        return null;
    }

    private static final String COLLECTION_EVENTS_BY_WAYBILL_QRY = "from "
        + CollectionEvent.class.getName() + " ce join ce."
        + CollectionEventPeer.SPECIMEN_COLLECTION + " as spcs join spcs."
        + SpecimenPeer.ORIGIN_INFO.getName() + " as oi join oi."
        + OriginInfoPeer.SHIPMENT_INFO.getName()
        + " as shipinfo where shipinfo." + ShipmentInfoPeer.WAYBILL + "=?";

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
        + " ce join ce."
        + CollectionEventPeer.SPECIMEN_COLLECTION
        + " as spcs join spcs."
        + SpecimenPeer.ORIGIN_INFO.getName()
        + " as oi join oi."
        + OriginInfoPeer.SHIPMENT_INFO.getName()
        + " as shipinfo where shipinfo." + ShipmentInfoPeer.RECEIVED_AT + "=?";

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

    private static final String SPECIMEN_COUNT_QRY = "select count(spc) from "
        + Specimen.class.getName()
        + " as spc where spc."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.ID) + "=?";

    public long getSpecimensCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<SpecimenWrapper> list = getSpecimenCollection(false);
        if (list == null)
            return 0;
        return getSpecimenCollection(false).size();
    }

    @Deprecated
    public boolean hasPatient(String pnum) {
        return false;
    }

    @Deprecated
    public static List<CollectionEventWrapper> getTodayCollectionEvents(
        WritableApplicationService appService) {
        return null;
    }
}
