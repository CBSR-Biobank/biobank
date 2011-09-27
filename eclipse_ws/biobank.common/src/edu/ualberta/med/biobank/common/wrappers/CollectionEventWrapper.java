package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.loggers.CollectionEventLogProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("unused")
public class CollectionEventWrapper extends CollectionEventBaseWrapper {
    private static final CollectionEventLogProvider LOG_PROVIDER = new CollectionEventLogProvider();
    private static final String HAS_SPECIMENS_MSG = Messages
        .getString("CollectionEventWrapper.has_specimen_delete_msg"); //$NON-NLS-1$
    private static final Collection<Property<?, ? super CollectionEvent>> UNIQUE_VISIT_NUMBER_PROPS;
    static {
        Collection<Property<?, ? super CollectionEvent>> tmp = new ArrayList<Property<?, ? super CollectionEvent>>();
        tmp.add(CollectionEventPeer.PATIENT.to(PatientPeer.ID));
        tmp.add(CollectionEventPeer.VISIT_NUMBER);

        UNIQUE_VISIT_NUMBER_PROPS = Collections.unmodifiableCollection(tmp);
    };

    private Map<String, StudyEventAttrWrapper> studyEventAttrMap;
    private Map<String, EventAttrWrapper> eventAttrMap;

    public CollectionEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    private void removeFromSpecimenCollections(
        List<? extends SpecimenBaseWrapper> specimenCollection) {
        super.removeFromAllSpecimenCollection(specimenCollection);
        super.removeFromOriginalSpecimenCollection(specimenCollection);
    }

    private void removeFromSpecimenCollectionsWithCheck(
        List<? extends SpecimenBaseWrapper> specimenCollection)
        throws BiobankCheckException {
        super.removeFromAllSpecimenCollectionWithCheck(specimenCollection);
        super.removeFromOriginalSpecimenCollectionWithCheck(specimenCollection);
    }

    @Override
    public void removeFromAllSpecimenCollection(
        List<? extends SpecimenBaseWrapper> specCollection) {
        removeFromSpecimenCollections(specCollection);
    }

    @Override
    public void removeFromOriginalSpecimenCollection(
        List<? extends SpecimenBaseWrapper> specCollection) {
        removeFromSpecimenCollections(specCollection);
    }

    @Override
    public void removeFromAllSpecimenCollectionWithCheck(
        List<? extends SpecimenBaseWrapper> specCollection)
        throws BiobankCheckException {
        removeFromSpecimenCollectionsWithCheck(specCollection);
    }

    @Override
    public void removeFromOriginalSpecimenCollectionWithCheck(
        List<? extends SpecimenBaseWrapper> specCollection)
        throws BiobankCheckException {
        removeFromSpecimenCollectionsWithCheck(specCollection);
    }

    @Override
    public void addToOriginalSpecimenCollection(
        List<? extends SpecimenBaseWrapper> specs) {
        super.addToOriginalSpecimenCollection(specs);
        super.addToAllSpecimenCollection(specs);
    }

    @Override
    public CollectionEventLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    private static final String COLLECTION_EVENTS_BY_WAYBILL_QRY = "from " //$NON-NLS-1$
        + CollectionEvent.class.getName()
        + " ce join ce." //$NON-NLS-1$
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION
        + " as spcs join spcs." + SpecimenPeer.ORIGIN_INFO.getName() //$NON-NLS-1$
        + " as oi join oi." + OriginInfoPeer.SHIPMENT_INFO.getName() //$NON-NLS-1$
        + " as shipinfo where shipinfo." + ShipmentInfoPeer.WAYBILL + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

    // TODO: make sure that these count methods are actually correct if the
    // memory contents have been altered...

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

    private static final String COLLECTION_EVENTS_BY_DATE_RECEIVED_QRY = "from " //$NON-NLS-1$
        + CollectionEvent.class.getName()
        + " ce join ce." //$NON-NLS-1$
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION
        + " as spcs join spcs." //$NON-NLS-1$
        + SpecimenPeer.ORIGIN_INFO.getName()
        + " as oi join oi." //$NON-NLS-1$
        + OriginInfoPeer.SHIPMENT_INFO.getName()
        + " as shipinfo where shipinfo." + ShipmentInfoPeer.RECEIVED_AT + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

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

    public long getSourceSpecimensCount(boolean fast) throws BiobankException,
        ApplicationException {
        return getPropertyCount(
            CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION, fast);
    }

    public long getAllSpecimensCount(boolean fast) throws BiobankException,
        ApplicationException {
        return getPropertyCount(CollectionEventPeer.ALL_SPECIMEN_COLLECTION,
            fast);
    }

    private static final String ALIQUOTED_SPECIMEN_COUNT_QRY = "select count(spc) from " //$NON-NLS-1$
        + Specimen.class.getName()
        + " as spc where spc." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.ID) + "=? and spc." //$NON-NLS-1$
        + SpecimenPeer.PARENT_SPECIMEN.getName() + " is not null"; //$NON-NLS-1$

    public long getAliquotedSpecimensCount(boolean fast)
        throws BiobankException, ApplicationException {
        long count = 0;

        if (fast
            && !isInitialized(CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION)
            && !isInitialized(CollectionEventPeer.ALL_SPECIMEN_COLLECTION)) {
            HQLCriteria criteria = new HQLCriteria(
                ALIQUOTED_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            count = getCountResult(appService, criteria);
        } else {
            count = getAllSpecimensCount(fast) - getSourceSpecimensCount(fast);
        }

        return count;
    }

    public List<SpecimenWrapper> getAliquotedSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> aliquotedSpecimens = new ArrayList<SpecimenWrapper>(
            getAllSpecimenCollection(true));
        aliquotedSpecimens.removeAll(getOriginalSpecimenCollection(false));
        return aliquotedSpecimens;
    }

    private static String SOURCE_SPEC_IN_PROCESS_NOT_FLAGGED_QRY = "select spec from " //$NON-NLS-1$
        + Specimen.class.getName()
        + " as spec where spec." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.ORIGINAL_COLLECTION_EVENT,
            CollectionEventPeer.ID)
        + " = ? and spec." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.PROCESSING_EVENT,
            ProcessingEventPeer.ID)
        + " = ? and spec." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.ACTIVITY_STATUS,
            ActivityStatusPeer.NAME) + " != 'Flagged'"; //$NON-NLS-1$

    /**
     * source specimen that are in a process event
     * 
     * @throws ApplicationException
     */
    public List<SpecimenWrapper> getSourceSpecimenCollectionInProcessNotFlagged(
        ProcessingEventWrapper pEvent, boolean sort)
        throws ApplicationException {
        List<Specimen> raw = appService.query(new HQLCriteria(
            SOURCE_SPEC_IN_PROCESS_NOT_FLAGGED_QRY, Arrays.asList(new Object[] {
                getId(), pEvent.getId() })));
        if (raw == null) {
            return new ArrayList<SpecimenWrapper>();
        }
        List<SpecimenWrapper> specs = wrapModelCollection(appService, raw,
            SpecimenWrapper.class);
        if (sort)
            Collections.sort(specs);
        return specs;
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
                throw new Exception("StudyEventAttr with label \"" + label //$NON-NLS-1$
                    + "\" is invalid"); //$NON-NLS-1$
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
                throw new Exception("StudyEventAttr withr label \"" + label //$NON-NLS-1$
                    + "\" does not exist"); //$NON-NLS-1$
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
                throw new Exception("EventAttr for label \"" + label //$NON-NLS-1$
                    + "\" does not exist"); //$NON-NLS-1$
            }
        }
        String permissible = studyEventAttr.getPermissible();
        if (permissible == null) {
            return null;
        }
        return permissible.split(";"); //$NON-NLS-1$
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
                throw new Exception("no StudyEventAttr found for label \"" //$NON-NLS-1$
                    + label + "\""); //$NON-NLS-1$
            }
        }

        if (!studyEventAttr.getActivityStatus().isActive()) {
            throw new Exception("attribute for label \"" + label //$NON-NLS-1$
                + "\" is locked, changes not premitted"); //$NON-NLS-1$
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
                            .asList(permissible.split(";")); //$NON-NLS-1$
                    }
                }

                if (EventAttrTypeEnum.SELECT_SINGLE.isSameType(type)) {
                    if (!permissibleSplit.contains(value)) {
                        throw new Exception("value " + value //$NON-NLS-1$
                            + "is invalid for label \"" + label + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else if (EventAttrTypeEnum.SELECT_MULTIPLE.isSameType(type)) {
                    for (String singleVal : value.split(";")) { //$NON-NLS-1$
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " (" //$NON-NLS-1$ //$NON-NLS-2$
                                + value + ") is invalid for label \"" + label //$NON-NLS-1$
                                + "\""); //$NON-NLS-1$
                        }
                    }
                } else if (EventAttrTypeEnum.NUMBER.isSameType(type)) {
                    Double.parseDouble(value);
                } else if (EventAttrTypeEnum.DATE_TIME.isSameType(type)) {
                    DateFormatter.dateFormatter.parse(value);
                } else if (EventAttrTypeEnum.TEXT.isSameType(type)) {
                    // do nothing
                } else {
                    throw new Exception("type \"" + type + "\" not tested"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        if (pvAttr == null) {
            pvAttr = new EventAttrWrapper(appService);
            pvAttr.setCollectionEvent(this);
            pvAttr.setStudyEventAttr(studyEventAttr);

            EventAttrWrapper oldValue = eventAttrMap.put(label, pvAttr);

            if (oldValue != null) {
                removeFromEventAttrCollection(Arrays.asList(oldValue));
            }

            addToEventAttrCollection(Arrays.asList(pvAttr));
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
        HQLCriteria c = new HQLCriteria("select max(ce.visitNumber) from " //$NON-NLS-1$
            + CollectionEvent.class.getName() + " ce where ce.patient.id=?", //$NON-NLS-1$
            Arrays.asList(cevent.getPatient().getId()));
        List<Object> result = appService.query(c);
        if (result == null || result.size() == 0 || result.get(0) == null)
            return 1;
        else
            return (Integer) result.get(0) + 1;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(CollectionEventPeer.VISIT_NUMBER));

        tasks.add(check().unique(UNIQUE_VISIT_NUMBER_PROPS));

        tasks.persistRemoved(this, CollectionEventPeer.ALL_SPECIMEN_COLLECTION);
        tasks.persistRemoved(this,
            CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION);

        super.addPersistTasks(tasks);
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        tasks.add(check().empty(CollectionEventPeer.ALL_SPECIMEN_COLLECTION,
            HAS_SPECIMENS_MSG));

        super.addDeleteTasks(tasks);
    }

    public void merge(CollectionEventWrapper p2event) throws Exception {
        List<SpecimenWrapper> ospecs = p2event
            .getOriginalSpecimenCollection(false);
        List<SpecimenWrapper> aspecs = p2event.getAllSpecimenCollection(false);
        for (SpecimenWrapper aspec : aspecs) {
            if (ospecs.contains(aspec))
                aspec.setOriginalCollectionEvent(this);
            aspec.setCollectionEvent(this);
            aspec.persist();
        }
        p2event.delete();
    }

    /**
     * return true if the user can delete this object
     */
    @Override
    public boolean canDelete(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canDelete(user, center, study)
            && (getPatient() == null || getPatient().getStudy() == null
                || user.getCurrentWorkingCenter() == null || user
                .getCurrentWorkingCenter().getStudyCollection()
                .contains(getPatient().getStudy()));
    }

    /**
     * return true if the user can edit this object
     */
    @Override
    public boolean canUpdate(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canUpdate(user, center, study)
            && (getPatient() == null || getPatient().getStudy() == null
                || user.getCurrentWorkingCenter() == null || user
                .getCurrentWorkingCenter().getStudyCollection()
                .contains(getPatient().getStudy()));
    }

    public Date getMinSourceSpecimenDate() {
        Date min = new Date();
        for (SpecimenWrapper spec : getOriginalSpecimenCollection(false))
            min = min.before(spec.getCreatedAt()) ? min : spec.getCreatedAt();
        return min;
    }
}
