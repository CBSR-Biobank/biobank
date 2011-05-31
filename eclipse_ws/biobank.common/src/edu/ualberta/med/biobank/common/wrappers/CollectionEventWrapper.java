package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
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

    private Set<SpecimenWrapper> deletedSourceSpecimens = new HashSet<SpecimenWrapper>();

    public CollectionEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    private void removeFromSpecimenCollections(
        List<SpecimenWrapper> specimenCollection) {
        deletedSourceSpecimens.addAll(specimenCollection);
        super.removeFromAllSpecimenCollection(specimenCollection);
        super.removeFromOriginalSpecimenCollection(specimenCollection);
    }

    private void removeFromSpecimenCollectionsWithCheck(
        List<SpecimenWrapper> specimenCollection) throws BiobankCheckException {
        deletedSourceSpecimens.addAll(specimenCollection);
        super.removeFromAllSpecimenCollectionWithCheck(specimenCollection);
        super.removeFromOriginalSpecimenCollectionWithCheck(specimenCollection);
    }

    @Override
    public void removeFromAllSpecimenCollection(
        List<SpecimenWrapper> specCollection) {
        removeFromSpecimenCollections(specCollection);
    }

    @Override
    public void removeFromOriginalSpecimenCollection(
        List<SpecimenWrapper> specCollection) {
        removeFromSpecimenCollections(specCollection);
    }

    @Override
    public void removeFromAllSpecimenCollectionWithCheck(
        List<SpecimenWrapper> specCollection) throws BiobankCheckException {
        removeFromSpecimenCollectionsWithCheck(specCollection);
    }

    @Override
    public void removeFromOriginalSpecimenCollectionWithCheck(
        List<SpecimenWrapper> specCollection) throws BiobankCheckException {
        removeFromSpecimenCollectionsWithCheck(specCollection);
    }

    @Override
    public void addToOriginalSpecimenCollection(List<SpecimenWrapper> specs) {
        super.addToOriginalSpecimenCollection(specs);
        super.addToAllSpecimenCollection(specs);
        deletedSourceSpecimens.removeAll(specs);
    }

    private void deleteSpecimens() throws Exception {
        // FIXME delete only if no children ??
        for (SpecimenWrapper sv : deletedSourceSpecimens) {
            if (!sv.isNew()) {
                sv.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankDeleteException,
        ApplicationException {
        List<SpecimenWrapper> specimens = getAllSpecimenCollection(false);
        if (specimens != null && !specimens.isEmpty()) {
            throw new BiobankDeleteException(
                "Specimens are still linked to this Collection Event. "
                    + "Delete them before attempting to remove this Collection Event");
        }
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkVisitNumberUnused();
    }

    private static final String CHECK_VISIT_NUMBER_UNUSED = "select ce from "
        + CollectionEvent.class.getName() + " ce where ce."
        + CollectionEventPeer.VISIT_NUMBER.getName() + "=? and ce."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=? {0}";

    private void checkVisitNumberUnused() throws BiobankCheckException,
        ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(getVisitNumber());
        params.add(getPatient().getId());
        String equalsTest = "";
        if (!isNew()) {
            equalsTest = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(MessageFormat.format(
            CHECK_VISIT_NUMBER_UNUSED, equalsTest), params);
        List<Object> result = appService.query(c);
        if (result.size() != 0)
            throw new BiobankCheckException("Visit #" + getVisitNumber()
                + " has already been added for patient "
                + getPatient().getPnumber() + ".");
    }

    @Override
    protected void persistDependencies(CollectionEvent origObject)
        throws Exception {
        deleteSpecimens();
        if (eventAttrMap != null) {
            setWrapperCollection(CollectionEventPeer.EVENT_ATTR_COLLECTION,
                eventAttrMap.values());
        }
    }

    public void checkAtLeastOneSourceSpecimen() throws BiobankCheckException {
        List<SpecimenWrapper> spc = getOriginalSpecimenCollection(false);
        if (spc == null || spc.isEmpty()) {
            throw new BiobankCheckException(
                "At least one specimen should be added to this Collection Event.");
        }
    }

    @Override
    protected Log getLogMessage(String action, String site, String details)
        throws Exception {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setCenter(null);
        } else {
            log.setCenter(site);
        }
        log.setPatientNumber(getPatient().getPnumber());
        List<String> detailsList = new ArrayList<String>();
        if (details.length() > 0) {
            detailsList.add(details);
        }

        detailsList.add(new StringBuilder("visit:").append(getVisitNumber())
            .toString());

        try {
            detailsList.add(new StringBuilder("specimens:").append(
                getSourceSpecimensCount(false)).toString());
        } catch (BiobankException e) {
            e.printStackTrace();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        log.setDetails(StringUtils.join(detailsList, ", "));
        log.setType("CollectionEvent");
        return log;
    }

    private static final String COLLECTION_EVENTS_BY_WAYBILL_QRY = "from "
        + CollectionEvent.class.getName() + " ce join ce."
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION
        + " as spcs join spcs." + SpecimenPeer.ORIGIN_INFO.getName()
        + " as oi join oi." + OriginInfoPeer.SHIPMENT_INFO.getName()
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
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION
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

    private static final String SOURCE_SPECIMEN_COUNT_QRY = "select count(specimens) from "
        + CollectionEvent.class.getName()
        + " as cEvent join cEvent."
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName()
        + " as specimens where cEvent."
        + CollectionEventPeer.ID.getName()
        + "=?";

    public long getSourceSpecimensCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(SOURCE_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<SpecimenWrapper> list = getOriginalSpecimenCollection(false);
        if (list == null)
            return 0;
        return list.size();
    }

    private static final String ALL_SPECIMEN_COUNT_QRY = "select count(spc) from "
        + Specimen.class.getName()
        + " as spc where spc."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.ID) + "=?";

    public long getAllSpecimensCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(ALL_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<SpecimenWrapper> list = getOriginalSpecimenCollection(false);
        if (list == null)
            return 0;
        return list.size();
    }

    private static final String ALIQUOTED_SPECIMEN_COUNT_QRY = "select count(spc) from "
        + Specimen.class.getName()
        + " as spc where spc."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.ID)
        + "=? and spc."
        + SpecimenPeer.PARENT_SPECIMEN.getName() + " is not null";

    public long getAliquotedSpecimensCount(boolean fast)
        throws BiobankException, ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(
                ALIQUOTED_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<SpecimenWrapper> aliquotedSpecimens = getAliquotedSpecimenCollection(false);
        if (aliquotedSpecimens == null)
            return 0;
        return aliquotedSpecimens.size();
    }

    public List<SpecimenWrapper> getAliquotedSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> aliquotedSpecimens = new ArrayList<SpecimenWrapper>(
            getAllSpecimenCollection(true));
        aliquotedSpecimens.removeAll(getOriginalSpecimenCollection(false));
        return aliquotedSpecimens;
    }

    /**
     * source specimen that are in a process event
     */
    public List<SpecimenWrapper> getSourceSpecimenCollectionInProcess(
        ProcessingEventWrapper pEvent, boolean sort) {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (SpecimenWrapper specimen : getOriginalSpecimenCollection(sort)) {
            if (specimen.getProcessingEvent() != null
                && specimen.getProcessingEvent().equals(pEvent))
                specimens.add(specimen);
        }
        return specimens;
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

                if (EventAttrTypeEnum.SELECT_SINGLE.isSameType(type)
                    || EventAttrTypeEnum.SELECT_MULTIPLE.isSameType(type)) {
                    String permissible = studyEventAttr.getPermissible();
                    if (permissible != null) {
                        permissibleSplit = Arrays
                            .asList(permissible.split(";"));
                    }
                }

                if (EventAttrTypeEnum.SELECT_SINGLE.isSameType(type)) {
                    if (!permissibleSplit.contains(value)) {
                        throw new Exception("value " + value
                            + "is invalid for label \"" + label + "\"");
                    }
                } else if (EventAttrTypeEnum.SELECT_MULTIPLE.isSameType(type)) {
                    for (String singleVal : value.split(";")) {
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " ("
                                + value + ") is invalid for label \"" + label
                                + "\"");
                        }
                    }
                } else if (EventAttrTypeEnum.NUMBER.isSameType(type)) {
                    Double.parseDouble(value);
                } else if (EventAttrTypeEnum.DATE_TIME.isSameType(type)) {
                    DateFormatter.dateFormatter.parse(value);
                } else if (EventAttrTypeEnum.TEXT.isSameType(type)) {
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

    @Override
    public int compareTo(ModelWrapper<CollectionEvent> wrapper) {
        if (wrapper instanceof CollectionEventWrapper) {
            Integer nber1 = wrappedObject.getVisitNumber();
            Integer nber2 = wrapper.wrappedObject.getVisitNumber();
            if (nber1 != null && nber2 != null) {
                return nber1.compareTo(nber2);
            }
        }
        return 0;
    }

    public static Integer getNextVisitNumber(
        WritableApplicationService appService, CollectionEventWrapper cevent)
        throws Exception {
        HQLCriteria c = new HQLCriteria("select max(ce.visitNumber) from "
            + CollectionEvent.class.getName() + " ce where ce.patient.id=?",
            Arrays.asList(cevent.getPatient().getId()));
        List<Object> result = appService.query(c);
        if (result == null || result.size() == 0 || result.get(0) == null)
            return 1;
        else
            return (Integer) result.get(0) + 1;
    }

}
