package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.base.StudyBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends StudyBaseWrapper {

    private Map<String, StudyEventAttrWrapper> studyEventAttrMap;

    private Set<AliquotedSpecimenWrapper> deletedAliquotedSpecimens = new HashSet<AliquotedSpecimenWrapper>();

    private Set<SourceSpecimenWrapper> deletedSourceSpecimens = new HashSet<SourceSpecimenWrapper>();

    private Set<StudyEventAttrWrapper> deletedStudyEventAttr = new HashSet<StudyEventAttrWrapper>();

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudyWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (hasPatients()) {
            throw new BiobankDeleteException("Unable to delete study "
                + getName() + ". All defined patients must be removed first.");
        }
    }

    @Override
    public void addToAliquotedSpecimenCollection(
        List<AliquotedSpecimenWrapper> AliquotedSpecimenCollection) {
        super.addToAliquotedSpecimenCollection(AliquotedSpecimenCollection);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedAliquotedSpecimens.removeAll(AliquotedSpecimenCollection);
    }

    @Override
    public void removeFromAliquotedSpecimenCollection(
        List<AliquotedSpecimenWrapper> AliquotedSpecimensToRemove) {
        deletedAliquotedSpecimens.addAll(AliquotedSpecimensToRemove);
        super.removeFromAliquotedSpecimenCollection(AliquotedSpecimensToRemove);
    }

    /*
     * Removes the StudyEventAttr objects that are not contained in the
     * collection.
     */
    private void deleteStudyEventAttrs() throws Exception {
        for (StudyEventAttrWrapper st : deletedStudyEventAttr) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     */
    private void deleteAliquotedSpecimens() throws Exception {
        for (AliquotedSpecimenWrapper st : deletedAliquotedSpecimens) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    /**
     * Removes the study source vessel objects that are not contained in the
     * collection.
     */
    private void deleteSourceSpecimens() throws Exception {
        for (SourceSpecimenWrapper st : deletedSourceSpecimens) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    @Override
    public void addToSourceSpecimenCollection(
        List<SourceSpecimenWrapper> newSourceSpecimens) {
        super.addToSourceSpecimenCollection(newSourceSpecimens);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedSourceSpecimens.removeAll(newSourceSpecimens);
    }

    @Override
    public void removeFromSourceSpecimenCollection(
        List<SourceSpecimenWrapper> SourceSpecimensToDelete) {
        deletedSourceSpecimens.addAll(SourceSpecimensToDelete);
        super.removeFromSourceSpecimenCollection(SourceSpecimensToDelete);
    }

    protected Collection<StudyEventAttrWrapper> getStudyEventAttrCollection() {
        Map<String, StudyEventAttrWrapper> map = getStudyEventAttrMap();
        if (map == null) {
            return null;
        }
        return map.values();
    }

    private Map<String, StudyEventAttrWrapper> getStudyEventAttrMap() {
        if (studyEventAttrMap != null)
            return studyEventAttrMap;

        studyEventAttrMap = new HashMap<String, StudyEventAttrWrapper>();

        List<StudyEventAttrWrapper> EventAttrList = StudyEventAttrWrapper
            .getStudyEventAttrCollection(this);

        for (StudyEventAttrWrapper studyEventAttr : EventAttrList) {
            studyEventAttrMap.put(studyEventAttr.getLabel(), studyEventAttr);
        }
        return studyEventAttrMap;
    }

    public String[] getStudyEventAttrLabels() {
        getStudyEventAttrMap();
        return studyEventAttrMap.keySet().toArray(new String[] {});
    }

    protected StudyEventAttrWrapper getStudyEventAttr(String label)
        throws Exception {
        getStudyEventAttrMap();
        StudyEventAttrWrapper studyEventAttr = studyEventAttrMap.get(label);
        if (studyEventAttr == null) {
            throw new Exception("StudyEventAttr with label \"" + label
                + "\" is invalid");
        }
        return studyEventAttr;
    }

    public EventAttrTypeEnum getStudyEventAttrType(String label)
        throws Exception {
        return EventAttrTypeEnum.getEventAttrType(getStudyEventAttr(label)
            .getEventAttrType().getName());
    }

    /**
     * Retrieves the permissible values for a patient visit attribute.
     * 
     * @param label The label to be used by the attribute.
     * @return Semicolon separated list of allowed values.
     * @throws Exception hrown if there is no patient visit information item
     *             with the label specified.
     */
    public String[] getStudyEventAttrPermissible(String label) throws Exception {
        String joinedPossibleValues = getStudyEventAttr(label).getPermissible();
        if (joinedPossibleValues == null)
            return null;
        return joinedPossibleValues.split(";");
    }

    /**
     * Retrieves the activity status for a patient visit attribute. If locked,
     * patient visits will not allow information to be saved for this attribute.
     * 
     * @param label
     * @return True if the attribute is locked. False otherwise.
     * @throws Exception
     */
    public ActivityStatusWrapper getStudyEventAttrActivityStatus(String label)
        throws Exception {
        return getStudyEventAttr(label).getActivityStatus();
    }

    /**
     * Assigns patient visit attributes to be used for this study.
     * 
     * @param label The label used for the attribute.
     * @param type The string corresponding to the type of the attribute.
     * @param permissibleValues If the attribute is of type "select_single" or
     *            "select_multiple" this array contains the possible values as a
     *            String array. Otherwise, this parameter should be set to null.
     * 
     * @throws Exception Thrown if the attribute type does not exist.
     */
    public void setStudyEventAttr(String label, EventAttrTypeEnum type,
        String[] permissibleValues) throws Exception {
        Map<String, EventAttrTypeWrapper> EventAttrTypeMap = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService);
        EventAttrTypeWrapper EventAttrType = EventAttrTypeMap.get(type
            .getName());
        if (EventAttrType == null) {
            throw new Exception("the pv attribute type \"" + type
                + "\" does not exist");
        }

        getStudyEventAttrMap();
        StudyEventAttrWrapper studyEventAttr = studyEventAttrMap.get(label);

        if (type.isSelectType()) {
            // type has permissible values
            if ((studyEventAttr == null) && (permissibleValues == null)) {
                // nothing to do
                return;
            }

            if ((studyEventAttr != null) && (permissibleValues == null)) {
                deleteStudyEventAttr(label);
                return;
            }
        }

        if (studyEventAttr == null) {
            // does not yet exist
            studyEventAttr = new StudyEventAttrWrapper(appService);
            studyEventAttr.setLabel(label);
            studyEventAttr.setEventAttrType(EventAttrType);
            studyEventAttr.setStudy(this);
        }

        deletedStudyEventAttr.remove(studyEventAttr);
        studyEventAttr.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        studyEventAttr.setPermissible(StringUtils.join(permissibleValues, ';'));
        studyEventAttrMap.put(label, studyEventAttr);
    }

    /**
     * Assigns patient visit attributes to be used for this study.
     * 
     * @param label The label to be used for the attribute.
     * @param type The string corresponding to the type of the attribute.
     * 
     * @throws Exception Thrown if there is no possible patient visit with the
     *             label specified.
     */
    public void setStudyEventAttr(String label, EventAttrTypeEnum type)
        throws Exception {
        setStudyEventAttr(label, type, null);
    }

    /**
     * Used to enable or disable the locked status of a patient visit attribute.
     * If an attribute is locked the patient visits will not allow information
     * to be saved for this attribute.
     * 
     * @param label The label used for the attribute. Note: the label must
     *            already exist.
     * @param enable True to enable the lock, false otherwise.
     * 
     * @throws Exception if attribute with label does not exist.
     */
    public void setStudyEventAttrActivityStatus(String label,
        ActivityStatusWrapper activityStatus) throws Exception {
        getStudyEventAttrMap();
        StudyEventAttrWrapper studyEventAttr = getStudyEventAttr(label);
        studyEventAttr.setActivityStatus(activityStatus);
    }

    /**
     * Used to delete a patient visit attribute.
     * 
     * @param label The label used for the attribute.
     * @throws Exception if attribute with label does not exist.
     */
    public void deleteStudyEventAttr(String label) throws Exception {
        getStudyEventAttrMap();
        StudyEventAttrWrapper studyEventAttr = getStudyEventAttr(label);
        if (studyEventAttr.isUsedByCollectionEvents()) {
            throw new BiobankCheckException("StudyEventAttr with label \""
                + label + "\" is in use by patient visits");
        }
        studyEventAttrMap.remove(label);
        deletedStudyEventAttr.add(studyEventAttr);
    }

    public List<ClinicWrapper> getClinicCollection() {
        // FIXME: is it faster to do an HQL query here?
        // FIXME answer: might be faster but need to check when this is needed
        // (what if
        // want to show the current modification in the studyEntryform, before
        // it is saved in the DB)
        List<ContactWrapper> contacts = getContactCollection(false);
        List<ClinicWrapper> clinicWrappers = new ArrayList<ClinicWrapper>();
        if (contacts != null)
            for (ContactWrapper contact : contacts) {
                clinicWrappers.add(contact.getClinic());
            }
        return clinicWrappers;
    }

    private static final String PATIENT_QRY = "select patients from "
        + Study.class.getName() + " as study inner join study."
        + StudyPeer.PATIENT_COLLECTION.getName()
        + " as patients where patients." + PatientPeer.PNUMBER.getName()
        + " = ? and study." + StudyPeer.ID.getName() + " = ?";

    public PatientWrapper getPatient(String patientNumber) throws Exception {
        HQLCriteria criteria = new HQLCriteria(PATIENT_QRY,
            Arrays.asList(new Object[] { patientNumber, getId() }));
        List<Patient> result = appService.query(criteria);
        if (result.size() > 1) {
            throw new BiobankQueryResultSizeException();
        } else if (result.size() == 1) {
            return new PatientWrapper(appService, result.get(0));
        }
        return null;
    }

    private static final String HAS_PATIENTS_QRY = "select count(patient) from "
        + Study.class.getName()
        + " as study inner join study."
        + StudyPeer.PATIENT_COLLECTION.getName()
        + " as patient where study."
        + StudyPeer.ID.getName() + " = ?";

    public boolean hasPatients() throws ApplicationException,
        BiobankQueryResultSizeException {
        HQLCriteria criteria = new HQLCriteria(HAS_PATIENTS_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria) > 0;
    }

    public static final String PATIENT_COUNT_QRY = "select count(patients) from "
        + Study.class.getName()
        + " as study inner join study."
        + StudyPeer.PATIENT_COLLECTION.getName()
        + " as patients where study."
        + StudyPeer.ID.getName() + "= ?";

    /**
     * fast = true will execute a hql query. fast = false will call the
     * getpatientCollection method
     */
    public long getPatientCount(boolean fast) throws ApplicationException,
        BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<PatientWrapper> list = getPatientCollection(false);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int compareTo(ModelWrapper<Study> wrapper) {
        if (wrapper instanceof StudyWrapper) {
            String nameShort1 = getNameShort();
            String nameShort2 = wrapper.wrappedObject.getNameShort();

            int compare = 0;
            if ((nameShort1 != null) && (nameShort2 != null)) {
                compare = nameShort1.compareTo(nameShort2);
            }
            if (compare == 0) {
                String name1 = getName();
                String name2 = wrapper.wrappedObject.getName();

                return name1.compareTo(name2);
            }
            return compare;
        }
        return 0;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Study.class, StudyPeer.NAME.getName(), getName(),
            "A study with name");
        checkNoDuplicates(Study.class, StudyPeer.NAME_SHORT.getName(),
            getNameShort(), "A study with short name");
    }

    @Override
    protected void persistDependencies(Study origObject) throws Exception {
        if (studyEventAttrMap != null) {
            List<StudyEventAttrWrapper> allStudyEventAttrWrappers = new ArrayList<StudyEventAttrWrapper>();
            for (StudyEventAttrWrapper ss : studyEventAttrMap.values()) {
                allStudyEventAttrWrappers.add(ss);
            }
            setWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION,
                allStudyEventAttrWrappers);
        }
        deleteAliquotedSpecimens();
        deleteSourceSpecimens();
        deleteStudyEventAttrs();
    }

    public static final String IS_LINKED_TO_CLINIC_QRY = "select count(clinics) from "
        + Contact.class.getName()
        + " as contacts join contacts."
        + ContactPeer.CLINIC.getName()
        + " as clinics where contacts."
        + Property.concatNames(ContactPeer.STUDY_COLLECTION, StudyPeer.ID)
        + " = ? and clinics." + ClinicPeer.ID.getName() + " = ?";

    /**
     * return true if this study is linked to the given clinic (through
     * contacts)
     */
    public boolean isLinkedToClinic(ClinicWrapper clinic)
        throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(IS_LINKED_TO_CLINIC_QRY,
            Arrays.asList(new Object[] { getId(), clinic.getId() }));
        return getCountResult(appService, c) != 0;
    }

    @Override
    public void resetInternalFields() {
        studyEventAttrMap = null;
        deletedAliquotedSpecimens.clear();
        deletedSourceSpecimens.clear();
        deletedStudyEventAttr.clear();
    }

    public static final String ALL_STUDIES_QRY = "from "
        + Study.class.getName();

    public static List<StudyWrapper> getAllStudies(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria c = new HQLCriteria(ALL_STUDIES_QRY);
        return ModelWrapper.wrapModelCollection(appService,
            appService.query(c), StudyWrapper.class);
    }

    public static final String COUNT_QRY = "select count (*) from "
        + Study.class.getName();

    public static long getCount(WritableApplicationService appService)
        throws BiobankException, ApplicationException {
        return getCountResult(appService, new HQLCriteria(COUNT_QRY));
    }

    @Override
    public String toString() {
        return getName();
    }

    private static final String COLLECTION_EVENT_COUNT_QRY = "select count(distinct ce) from "
        + CollectionEvent.class.getName()
        + " as ce where ce."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.STUDY,
            StudyPeer.ID) + "=?";

    public long getCollectionEventCount(boolean fast)
        throws ApplicationException, BiobankException {
        if (fast) {
            HQLCriteria c = new HQLCriteria(COLLECTION_EVENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, c);
        }
        return getCollectionEventWrapper().size();
    }

    public List<CollectionEventWrapper> getCollectionEventWrapper() {
        List<CollectionEventWrapper> cEvents = new ArrayList<CollectionEventWrapper>();
        for (PatientWrapper p : getPatientCollection(false)) {
            cEvents.addAll(p.getCollectionEventCollection(false));
        }
        return cEvents;
    }

}
