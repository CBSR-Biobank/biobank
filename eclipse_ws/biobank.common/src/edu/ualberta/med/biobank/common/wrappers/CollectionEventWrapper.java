package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("unused")
public class CollectionEventWrapper extends CollectionEventBaseWrapper {

    private Map<String, StudyEventAttrWrapper> studyEventAttrMap;

    private Map<String, EventAttrWrapper> eventAttrMap;

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

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        // FIXME: how do we know what clinic this CE is for?
        // checkPatientsStudy(clinic);
    }

    @Override
    protected void persistDependencies(CollectionEvent origObject)
        throws Exception {
        deleteSourceVessels();
        if (eventAttrMap != null) {
            setWrapperCollection(CollectionEventPeer.EVENT_ATTR_COLLECTION,
                eventAttrMap.values());
        }
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
        Log log = new Log();
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
        return log;
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

    private Map<String, StudyEventAttrWrapper> getStudyEventAttrMap() {
        if (studyEventAttrMap != null)
            return studyEventAttrMap;

        PatientWrapper patient = getPatient();

        studyEventAttrMap = new HashMap<String, StudyEventAttrWrapper>();
        if (patient != null && patient.getStudy() != null) {
            Collection<StudyEventAttrWrapper> studyEventAttrCollection = patient
                .getStudy().getStudyEventAttrCollection();
            if (studyEventAttrCollection != null) {
                for (StudyEventAttrWrapper studyEventAttr : studyEventAttrCollection) {
                    studyEventAttrMap.put(studyEventAttr.getLabel(),
                        studyEventAttr);
                }
            }
        }
        return studyEventAttrMap;
    }

    private Map<String, EventAttrWrapper> getEventAttrMap() {
        getStudyEventAttrMap();
        if (eventAttrMap != null)
            return eventAttrMap;

        eventAttrMap = new HashMap<String, EventAttrWrapper>();
        List<EventAttrWrapper> pvAttrCollection = getEventAttrCollection(false);
        if (pvAttrCollection != null) {
            for (EventAttrWrapper pvAttr : pvAttrCollection) {
                eventAttrMap.put(pvAttr.getStudyEventAttr().getLabel(), pvAttr);
            }
        }
        return eventAttrMap;
    }

    public String[] getEventAttrLabels() {
        getEventAttrMap();
        return eventAttrMap.keySet().toArray(new String[] {});
    }

    public String getEventAttrValue(String label) throws Exception {
        getEventAttrMap();
        EventAttrWrapper pvAttr = eventAttrMap.get(label);
        if (pvAttr == null) {
            StudyEventAttrWrapper studyEventAttr = studyEventAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyEventAttr == null) {
                throw new Exception("StudyEventAttr with label \"" + label
                    + "\" is invalid");
            }
            // not assigned yet so return null
            return null;
        }
        return pvAttr.getValue();
    }

    public String getEventAttrTypeName(String label) throws Exception {
        getEventAttrMap();
        EventAttrWrapper pvAttr = eventAttrMap.get(label);
        StudyEventAttrWrapper studyEventAttr = null;
        if (pvAttr != null) {
            studyEventAttr = pvAttr.getStudyEventAttr();
        } else {
            studyEventAttr = studyEventAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyEventAttr == null) {
                throw new Exception("StudyEventAttr withr label \"" + label
                    + "\" does not exist");
            }
        }
        return studyEventAttr.getEventAttrType().getName();
    }

    public String[] getEventAttrPermissible(String label) throws Exception {
        getEventAttrMap();
        EventAttrWrapper pvAttr = eventAttrMap.get(label);
        StudyEventAttrWrapper studyEventAttr = null;
        if (pvAttr != null) {
            studyEventAttr = pvAttr.getStudyEventAttr();
        } else {
            studyEventAttr = studyEventAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyEventAttr == null) {
                throw new Exception("EventAttr for label \"" + label
                    + "\" does not exist");
            }
        }
        String permissible = studyEventAttr.getPermissible();
        if (permissible == null) {
            return null;
        }
        return permissible.split(";");
    }

    /**
     * Assigns a value to a patient visit attribute. The value is parsed for
     * correctness.
     * 
     * @param label The attribute's label.
     * @param value The value to assign.
     * @throws Exception when assigning a label of type "select_single" or
     *             "select_multiple" and the value is not one of the permissible
     *             ones.
     * @throws NumberFormatException when assigning a label of type "number" and
     *             the value is not a valid double number.
     * @throws ParseException when assigning a label of type "date_time" and the
     *             value is not a valid date and time.
     * @see edu.ualberta.med.biobank
     *      .common.formatters.DateFormatter.DATE_TIME_FORMAT
     */
    public void setEventAttrValue(String label, String value) throws Exception {
        getEventAttrMap();
        EventAttrWrapper pvAttr = eventAttrMap.get(label);
        StudyEventAttrWrapper studyEventAttr = null;

        if (pvAttr != null) {
            studyEventAttr = pvAttr.getStudyEventAttr();
        } else {
            studyEventAttr = studyEventAttrMap.get(label);
            if (studyEventAttr == null) {
                throw new Exception("no StudyEventAttr found for label \""
                    + label + "\"");
            }
        }

        if (!studyEventAttr.getActivityStatus().isActive()) {
            throw new Exception("attribute for label \"" + label
                + "\" is locked, changes not premitted");
        }

        if (value != null) {
            // validate the value
            value = value.trim();
            if (value.length() > 0) {
                String type = studyEventAttr.getEventAttrType().getName();
                List<String> permissibleSplit = null;

                if (type.equals("select_single")
                    || type.equals("select_multiple")) {
                    String permissible = studyEventAttr.getPermissible();
                    if (permissible != null) {
                        permissibleSplit = Arrays
                            .asList(permissible.split(";"));
                    }
                }

                if (type.equals("select_single")) {
                    if (!permissibleSplit.contains(value)) {
                        throw new Exception("value " + value
                            + "is invalid for label \"" + label + "\"");
                    }
                } else if (type.equals("select_multiple")) {
                    for (String singleVal : value.split(";")) {
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " ("
                                + value + ") is invalid for label \"" + label
                                + "\"");
                        }
                    }
                } else if (type.equals("number")) {
                    Double.parseDouble(value);
                } else if (type.equals("date_time")) {
                    DateFormatter.dateFormatter.parse(value);
                } else if (type.equals("text")) {
                    // do nothing
                } else {
                    throw new Exception("type \"" + type + "\" not tested");
                }
            }
        }

        if (pvAttr == null) {
            pvAttr = new EventAttrWrapper(appService);
            pvAttr.setCollectionEvent(this);
            pvAttr.setStudyEventAttr(studyEventAttr);
            eventAttrMap.put(label, pvAttr);
        }
        pvAttr.setValue(value);
    }

    @Override
    public void resetInternalFields() {
        eventAttrMap = null;
        studyEventAttrMap = null;
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
    public ClinicWrapper getClinic() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void setClinic(ClinicWrapper selectedObject) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void checkPatientsStudy() {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public String getFormattedDateReceived() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public CenterWrapper<?> getSite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void setSite(SiteWrapper selectedSite) {
        // TODO Auto-generated method stub

    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

}
