package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.User;
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

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudyWrapper(WritableApplicationService appService) {
        super(appService);
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

        List<StudyEventAttrWrapper> eventAttrList = getStudyEventAttrCollection(false);
        // StudyEventAttrWrapper.getStudyEventAttrCollection(this);

        for (StudyEventAttrWrapper studyEventAttr : eventAttrList) {
            studyEventAttrMap.put(studyEventAttr.getLabel(), studyEventAttr);
        }
        return studyEventAttrMap;
    }

    private void updateStudyEventAttrCollection() {
        if (studyEventAttrMap != null) {
            List<StudyEventAttrWrapper> allStudyEventAttrWrappers = new ArrayList<StudyEventAttrWrapper>();
            for (StudyEventAttrWrapper ss : studyEventAttrMap.values()) {
                allStudyEventAttrWrappers.add(ss);
            }
            setWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION,
                allStudyEventAttrWrappers);
        }
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

        StudyEventAttrWrapper studyEventAttr = getStudyEventAttrMap()
            .get(label);

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

        studyEventAttr.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        studyEventAttr.setPermissible(StringUtils.join(permissibleValues, ';'));
        studyEventAttrMap.put(label, studyEventAttr);

        updateStudyEventAttrCollection();
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
        updateStudyEventAttrCollection();
    }

    public List<ClinicWrapper> getClinicCollection() {
        // unique clinics
        List<ContactWrapper> contacts = getContactCollection(false);
        HashSet<ClinicWrapper> clinicWrappers = new HashSet<ClinicWrapper>();
        if (contacts != null)
            for (ContactWrapper contact : contacts) {
                clinicWrappers.add(contact.getClinic());
            }
        return Arrays.asList(clinicWrappers.toArray(new ClinicWrapper[] {}));
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

    public boolean hasPatients() throws ApplicationException, BiobankException {
        return getPatientCount(true) > 0;
    }

    public static final String PATIENT_COUNT_HQL = "select count(patients) from "
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
            HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_HQL,
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

    // WARNING: this runs very slow and generates a lot of network traffic
    public List<CollectionEventWrapper> getCollectionEventWrapper() {
        List<CollectionEventWrapper> cEvents = new ArrayList<CollectionEventWrapper>();
        for (PatientWrapper p : getPatientCollection(false)) {
            cEvents.addAll(p.getCollectionEventCollection(false));
        }
        return cEvents;
    }

    @Override
    public boolean canUpdate(User user) {
        return user.isInSuperAdminMode()
            && user.hasPrivilegeOnObject(Privilege.UPDATE, getWrappedClass(),
                getSecuritySpecificCenters());
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().uniqueAndNotNull(StudyPeer.NAME));
        tasks.add(check().uniqueAndNotNull(StudyPeer.NAME_SHORT));

        tasks.add(cascade()
            .deleteRemoved(StudyPeer.STUDY_EVENT_ATTR_COLLECTION));
        tasks
            .add(cascade().deleteRemoved(StudyPeer.SOURCE_SPECIMEN_COLLECTION));
        tasks.add(cascade().deleteRemoved(
            StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION));

        tasks.add(super.getPersistTasks());

        tasks
            .add(cascade().persistAdded(StudyPeer.STUDY_EVENT_ATTR_COLLECTION));
        tasks.add(cascade().persistAdded(StudyPeer.SOURCE_SPECIMEN_COLLECTION));
        tasks.add(cascade().persistAdded(
            StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION));

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().empty(StudyPeer.PATIENT_COLLECTION));

        tasks.add(cascade().delete(StudyPeer.STUDY_EVENT_ATTR_COLLECTION));
        tasks.add(cascade().delete(StudyPeer.SOURCE_SPECIMEN_COLLECTION));
        tasks.add(cascade().delete(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION));

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

    // public List<PatientWrapper> getPatientCollection(boolean sort) {
    // return HQLAccessor.getCachedCollection(this, PatientPeer.STUDY,
    // Patient.class, PatientWrapper.class, sort);
    // }
}
