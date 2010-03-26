package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends ModelWrapper<Study> {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

    private Set<SampleStorageWrapper> deletedSampleStorages = new HashSet<SampleStorageWrapper>();

    private Set<SourceVesselWrapper> deletedSourceVessels = new HashSet<SourceVesselWrapper>();

    private Set<StudyPvAttrWrapper> deletedStudyPvAttr = new HashSet<StudyPvAttrWrapper>();

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
        studyPvAttrMap = null;
    }

    public StudyWrapper(WritableApplicationService appService) {
        super(appService);
        studyPvAttrMap = null;
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("nameShort", oldNameShort,
            nameShort);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatus activityStatus = wrappedObject.getActivityStatus();
        if (activityStatus == null)
            return null;
        return new ActivityStatusWrapper(appService, activityStatus);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public void setSite(SiteWrapper site) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = site.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (hasPatients()) {
            throw new BiobankCheckException("Unable to delete study "
                + getName() + ". All defined patients must be removed first.");
        }
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "nameShort", "activityStatus", "comment",
            "site", "contactCollection", "sampleStorageCollection",
            "sourceVesselCollection", "studyPvAttrCollection",
            "patientCollection" };
    }

    @Override
    public Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkNotEmpty(getName(), "Name");
        checkNoDuplicatesInSite(Study.class, "name", getName(), getSite()
            .getId(), "A study with name \"" + getName() + "\" already exists.");
        checkNotEmpty(getNameShort(), "Short Name");
        checkNoDuplicatesInSite(Study.class, "nameShort", getNameShort(),
            getSite().getId(), "A study with short name \"" + getNameShort()
                + "\" already exists.");
        checkValidActivityStatus();
        checkContactsFromSameSite();
    }

    private void checkValidActivityStatus() throws BiobankCheckException {
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the clinic does not have an activity status");
        }
    }

    private void checkContactsFromSameSite() throws BiobankCheckException {
        if (getContactCollection() != null) {
            for (ContactWrapper contact : getContactCollection()) {
                if (!contact.getClinic().getSite().equals(getSite())) {
                    throw new BiobankCheckException(
                        "Contact associated with this study should be from the same site.");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<ContactWrapper> getContactCollection(boolean sort) {
        List<ContactWrapper> contactCollection = (List<ContactWrapper>) propertiesMap
            .get("contactCollection");
        if (contactCollection == null) {
            Collection<Contact> children = wrappedObject.getContactCollection();
            if (children != null) {
                contactCollection = new ArrayList<ContactWrapper>();
                for (Contact type : children) {
                    contactCollection.add(new ContactWrapper(appService, type));
                }
                propertiesMap.put("contactCollection", contactCollection);
            }
        }
        if ((contactCollection != null) && sort)
            Collections.sort(contactCollection);
        return contactCollection;
    }

    public List<ContactWrapper> getContactCollection() {
        return getContactCollection(false);
    }

    /**
     * Returns a list of contacts that have not yet been associated with this
     * study.
     */
    public List<ContactWrapper> getContactsNotAssoc() throws Exception {
        HQLCriteria criteria = new HQLCriteria("select distinct contacts from "
            + Contact.class.getName()
            + " as contacts left join contacts.studyCollection as studies "
            + "where (studies.id <> ?  or studies is null)"
            + "and contacts.clinic.site.id = ? "
            + "order by contacts.clinic.name", Arrays.asList(new Object[] {
            getId(), getSite().getId() }));
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        List<Contact> rawContacts = appService.query(criteria);
        for (Contact rawContact : rawContacts) {
            contacts.add(new ContactWrapper(appService, rawContact));
        }
        return contacts;
    }

    private void setContactCollection(Collection<Contact> allContactObjects,
        List<ContactWrapper> allContactWrappers) {
        Collection<Contact> oldContacts = wrappedObject.getContactCollection();
        wrappedObject.setContactCollection(allContactObjects);
        propertyChangeSupport.firePropertyChange("contactCollection",
            oldContacts, allContactObjects);
        propertiesMap.put("contactCollection", allContactWrappers);
    }

    public void addContacts(List<ContactWrapper> newContacts) {
        if (newContacts != null && newContacts.size() > 0) {
            Collection<Contact> allContactObjects = new HashSet<Contact>();
            List<ContactWrapper> allContactWrappers = new ArrayList<ContactWrapper>();
            // already added contacts
            List<ContactWrapper> currentList = getContactCollection();
            if (currentList != null) {
                for (ContactWrapper contact : currentList) {
                    allContactObjects.add(contact.getWrappedObject());
                    allContactWrappers.add(contact);
                }
            }
            // new contacts added
            for (ContactWrapper contact : newContacts) {
                allContactObjects.add(contact.getWrappedObject());
                allContactWrappers.add(contact);
            }
            setContactCollection(allContactObjects, allContactWrappers);
        }
    }

    public void removeContacts(List<ContactWrapper> contactsToRemove) {
        if (contactsToRemove != null && contactsToRemove.size() > 0) {
            Collection<Contact> allContactObjects = new HashSet<Contact>();
            List<ContactWrapper> allContactWrappers = new ArrayList<ContactWrapper>();
            // already added contacts
            List<ContactWrapper> currentList = getContactCollection();
            if (currentList != null) {
                for (ContactWrapper contact : currentList) {
                    if (!contactsToRemove.contains(contact)) {
                        allContactObjects.add(contact.getWrappedObject());
                        allContactWrappers.add(contact);
                    }
                }
            }
            setContactCollection(allContactObjects, allContactWrappers);
        }
    }

    @SuppressWarnings("unchecked")
    public List<SampleStorageWrapper> getSampleStorageCollection(boolean sort) {
        List<SampleStorageWrapper> ssCollection = (List<SampleStorageWrapper>) propertiesMap
            .get("sampleStorageCollection");
        if (ssCollection == null) {
            Collection<SampleStorage> children = wrappedObject
                .getSampleStorageCollection();
            if (children != null) {
                ssCollection = new ArrayList<SampleStorageWrapper>();
                for (SampleStorage study : children) {
                    ssCollection
                        .add(new SampleStorageWrapper(appService, study));
                }
                propertiesMap.put("sampleStorageCollection", ssCollection);
            }
        }
        if ((ssCollection != null) && sort)
            Collections.sort(ssCollection);
        return ssCollection;
    }

    public List<SampleStorageWrapper> getSampleStorageCollection() {
        return getSampleStorageCollection(false);
    }

    public void addSampleStorage(List<SampleStorageWrapper> newSampleStorages) {
        if (newSampleStorages != null && newSampleStorages.size() > 0) {
            Collection<SampleStorage> allSsObjects = new HashSet<SampleStorage>();
            List<SampleStorageWrapper> allSsWrappers = new ArrayList<SampleStorageWrapper>();
            // already in list
            List<SampleStorageWrapper> currentList = getSampleStorageCollection();
            if (currentList != null) {
                for (SampleStorageWrapper ss : currentList) {
                    allSsObjects.add(ss.getWrappedObject());
                    allSsWrappers.add(ss);
                }
            }
            // new
            for (SampleStorageWrapper ss : newSampleStorages) {
                ss.setStudy(this);
                allSsObjects.add(ss.getWrappedObject());
                allSsWrappers.add(ss);
                deletedSampleStorages.remove(ss);
            }
            setSampleStorages(allSsObjects, allSsWrappers);
        }
    }

    public void removeSampleStorages(
        List<SampleStorageWrapper> sampleStoragesToRemove) {
        if (sampleStoragesToRemove != null && sampleStoragesToRemove.size() > 0) {
            deletedSampleStorages.addAll(sampleStoragesToRemove);
            Collection<SampleStorage> allSsObjects = new HashSet<SampleStorage>();
            List<SampleStorageWrapper> allSsWrappers = new ArrayList<SampleStorageWrapper>();
            // already in list
            List<SampleStorageWrapper> currentList = getSampleStorageCollection();
            if (currentList != null) {
                for (SampleStorageWrapper ss : currentList) {
                    if (!sampleStoragesToRemove.contains(ss)) {
                        allSsObjects.add(ss.getWrappedObject());
                        allSsWrappers.add(ss);
                    }
                }
            }
            setSampleStorages(allSsObjects, allSsWrappers);
        }
    }

    private void setSampleStorages(Collection<SampleStorage> allSsObjects,
        List<SampleStorageWrapper> allSsWrappers) {
        Collection<SampleStorage> oldSampleStorage = wrappedObject
            .getSampleStorageCollection();
        wrappedObject.setSampleStorageCollection(allSsObjects);
        propertyChangeSupport.firePropertyChange("sampleStorageCollection",
            oldSampleStorage, allSsObjects);
        propertiesMap.put("sampleStorageCollection", allSsWrappers);
    }

    /*
     * Removes the StudyPvAttr objects that are not contained in the collection.
     */
    private void deleteStudyPvAttrs() throws Exception {
        for (StudyPvAttrWrapper st : deletedStudyPvAttr) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     */
    private void deleteSampleStorages() throws Exception {
        for (SampleStorageWrapper st : deletedSampleStorages) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<SourceVesselWrapper> getSourceVesselCollection(boolean sort) {
        List<SourceVesselWrapper> ssCollection = (List<SourceVesselWrapper>) propertiesMap
            .get("sourceVesselCollection");
        if (ssCollection == null) {
            Collection<SourceVessel> children = wrappedObject
                .getSourceVesselCollection();
            if (children != null) {
                ssCollection = new ArrayList<SourceVesselWrapper>();
                for (SourceVessel study : children) {
                    ssCollection
                        .add(new SourceVesselWrapper(appService, study));
                }
                propertiesMap.put("sourceVesselCollection", ssCollection);
            }
        }
        if ((ssCollection != null) && sort)
            Collections.sort(ssCollection);
        return ssCollection;
    }

    public List<SourceVesselWrapper> getSourceVesselCollection() {
        return getSourceVesselCollection(false);
    }

    private void setSourceVessels(Collection<SourceVessel> allSsObject,
        List<SourceVesselWrapper> allSsWrappers) {
        Collection<SourceVessel> oldSourceVessels = wrappedObject
            .getSourceVesselCollection();
        wrappedObject.setSourceVesselCollection(allSsObject);
        propertyChangeSupport.firePropertyChange("sourceVesselCollection",
            oldSourceVessels, allSsObject);
        propertiesMap.put("sourceVesselCollection", allSsWrappers);
    }

    public void addSourceVessels(List<SourceVesselWrapper> newSourceVessels) {
        if (newSourceVessels != null && newSourceVessels.size() > 0) {
            Collection<SourceVessel> allSsObjects = new HashSet<SourceVessel>();
            List<SourceVesselWrapper> allSsWrappers = new ArrayList<SourceVesselWrapper>();
            // already in list
            List<SourceVesselWrapper> currentList = getSourceVesselCollection();
            if (currentList != null) {
                for (SourceVesselWrapper ss : currentList) {
                    allSsObjects.add(ss.getWrappedObject());
                    allSsWrappers.add(ss);
                }
            }
            // new
            for (SourceVesselWrapper ss : newSourceVessels) {
                allSsObjects.add(ss.getWrappedObject());
                allSsWrappers.add(ss);
                deletedSourceVessels.remove(ss);
            }
            setSourceVessels(allSsObjects, allSsWrappers);
        }
    }

    public void removeSourceVessels(
        List<SourceVesselWrapper> sourceVesselsToDelete) {
        if (sourceVesselsToDelete != null && sourceVesselsToDelete.size() > 0) {
            deletedSourceVessels.addAll(sourceVesselsToDelete);
            Collection<SourceVessel> allSsObjects = new HashSet<SourceVessel>();
            List<SourceVesselWrapper> allSsWrappers = new ArrayList<SourceVesselWrapper>();
            // already in list
            List<SourceVesselWrapper> currentList = getSourceVesselCollection();
            if (currentList != null) {
                for (SourceVesselWrapper ss : currentList) {
                    if (!deletedSourceVessels.contains(ss)) {
                        allSsObjects.add(ss.getWrappedObject());
                        allSsWrappers.add(ss);
                    }
                }
            }
            setSourceVessels(allSsObjects, allSsWrappers);
        }
    }

    /**
     * Removes the source vessel objects that are not contained in the
     * collection.
     */
    private void deleteSourceVessels() throws Exception {
        for (SourceVesselWrapper ss : deletedSourceVessels) {
            if (!ss.isNew()) {
                ss.delete();
            }
        }
    }

    protected Collection<StudyPvAttrWrapper> getStudyPvAttrCollection() {
        Map<String, StudyPvAttrWrapper> map = getStudyPvAttrMap();
        if (map == null) {
            return null;
        }
        return map.values();
    }

    private Map<String, StudyPvAttrWrapper> getStudyPvAttrMap() {
        if (studyPvAttrMap != null)
            return studyPvAttrMap;

        studyPvAttrMap = new HashMap<String, StudyPvAttrWrapper>();
        Collection<StudyPvAttr> studyPvAttrCollection = wrappedObject
            .getStudyPvAttrCollection();
        if (studyPvAttrCollection != null) {
            for (StudyPvAttr studyPvAttr : studyPvAttrCollection) {
                studyPvAttrMap.put(studyPvAttr.getLabel(),
                    new StudyPvAttrWrapper(appService, studyPvAttr));
            }
        }
        return studyPvAttrMap;
    }

    public String[] getStudyPvAttrLabels() {
        getStudyPvAttrMap();
        return studyPvAttrMap.keySet().toArray(new String[] {});
    }

    protected StudyPvAttrWrapper getStudyPvAttr(String label) throws Exception {
        getStudyPvAttrMap();
        StudyPvAttrWrapper studyPvAttr = studyPvAttrMap.get(label);
        if (studyPvAttr == null) {
            throw new Exception("StudyPvAttr with label \"" + label
                + "\" is invalid");
        }
        return studyPvAttr;
    }

    public String getStudyPvAttrType(String label) throws Exception {
        return getStudyPvAttr(label).getPvAttrType().getName();
    }

    /**
     * Retrieves the permissible values for a patient visit attribute.
     * 
     * @param label The label to be used by the attribute.
     * @return Semicolon separated list of allowed values.
     * @throws Exception hrown if there is no patient visit information item
     *             with the label specified.
     */
    public String[] getStudyPvAttrPermissible(String label) throws Exception {
        String joinedPossibleValues = getStudyPvAttr(label).getPermissible();
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
    public ActivityStatusWrapper getStudyPvAttrActivityStatus(String label)
        throws Exception {
        return getStudyPvAttr(label).getActivityStatus();
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
    public void setStudyPvAttr(String label, String type,
        String[] permissibleValues) throws Exception {
        Map<String, PvAttrTypeWrapper> pvAttrTypeMap = SiteWrapper
            .getPvAttrTypeMap(appService);
        PvAttrTypeWrapper pvAttrType = pvAttrTypeMap.get(type);
        if (pvAttrType == null) {
            throw new Exception("the pv attribute type \"" + type
                + "\" does not exist");
        }

        getStudyPvAttrMap();
        StudyPvAttrWrapper studyPvAttr = studyPvAttrMap.get(label);

        if (type.startsWith("select_")) {
            // type has permissible values
            if ((studyPvAttr == null) && (permissibleValues == null)) {
                // nothing to do
                return;
            }

            if ((studyPvAttr != null) && (permissibleValues == null)) {
                deleteStudyPvAttr(label);
                // studyPvAttr.delete();
                // studyPvAttrMap.remove(label);
                return;
            }
        }

        if (studyPvAttr == null) {
            // does not yet exist
            studyPvAttr = new StudyPvAttrWrapper(appService);
            studyPvAttr.setLabel(label);
            studyPvAttr.setPvAttrType(pvAttrType);
            studyPvAttr.setStudy(wrappedObject);
        }
        deletedStudyPvAttr.remove(studyPvAttr);

        studyPvAttr.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        studyPvAttr.setPermissible(StringUtils.join(permissibleValues, ';'));
        studyPvAttrMap.put(label, studyPvAttr);
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
    public void setStudyPvAttr(String label, String type) throws Exception {
        setStudyPvAttr(label, type, null);
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
    public void setStudyPvAttrActivityStatus(String label,
        ActivityStatusWrapper activityStatus) throws Exception {
        getStudyPvAttrMap();
        StudyPvAttrWrapper studyPvAttr = getStudyPvAttr(label);
        studyPvAttr.setActivityStatus(activityStatus);
    }

    /**
     * Used to delete a patient visit attribute.
     * 
     * @param label The label used for the attribute.
     * @throws Exception if attribute with label does not exist.
     */
    public void deleteStudyPvAttr(String label) throws Exception {
        getStudyPvAttrMap();
        StudyPvAttrWrapper studyPvAttr = getStudyPvAttr(label);
        if (studyPvAttr.isUsedByPatientVisits()) {
            throw new BiobankCheckException("StudyPvAttr with label \"" + label
                + "\" is in use by patient visits");
        }
        studyPvAttrMap.remove(label);
        deletedStudyPvAttr.add(studyPvAttr);
    }

    public List<ClinicWrapper> getClinicCollection()
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct clinics from "
            + Contact.class.getName() + " as contacts"
            + " inner join contacts.clinic as clinics"
            + " where contacts.studyCollection.id = ?", Arrays
            .asList(new Object[] { getId() }));
        List<Clinic> clinics = appService.query(c);
        List<ClinicWrapper> clinicWrappers = new ArrayList<ClinicWrapper>();
        for (Clinic clinic : clinics) {
            clinicWrappers.add(new ClinicWrapper(appService, clinic));
        }
        return clinicWrappers;
    }

    public boolean hasClinic(String clinicName) throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            "select count(*) from "
                + Study.class.getName()
                + " as study join study.contactCollection as contacts"
                + " join contacts.clinic as clinics where study = ? and clinics.name = ?",
            Arrays.asList(new Object[] { getWrappedObject(), clinicName }));
        List<Long> result = appService.query(criteria);
        return (result.get(0) > 0);
    }

    @SuppressWarnings("unchecked")
    public List<PatientWrapper> getPatientCollection(boolean sort) {
        List<PatientWrapper> patientCollection = (List<PatientWrapper>) propertiesMap
            .get("patientCollection");
        if (patientCollection == null) {
            Collection<Patient> children = wrappedObject.getPatientCollection();
            if (children != null) {
                patientCollection = new ArrayList<PatientWrapper>();
                for (Patient patient : children) {
                    patientCollection.add(new PatientWrapper(appService,
                        patient));
                }
                propertiesMap.put("patientCollection", patientCollection);
            }
        }
        if ((patientCollection != null) && sort)
            Collections.sort(patientCollection);
        return patientCollection;
    }

    public List<PatientWrapper> getPatientCollection() {
        return getPatientCollection(false);
    }

    public PatientWrapper getPatient(String patientNumber) throws Exception {
        HQLCriteria criteria = new HQLCriteria("select patients from "
            + Study.class.getName()
            + " as study inner join study.patientCollection"
            + " as patients where patients.pnumber = ? and study.id = ?",
            Arrays.asList(new Object[] { patientNumber, getId() }));
        List<Patient> result = appService.query(criteria);
        if (result.size() > 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        } else if (result.size() == 1) {
            return new PatientWrapper(appService, result.get(0));
        }
        return null;
    }

    public boolean hasPatients() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria(
            "select count(patient) from "
                + Study.class.getName()
                + " as study inner join study.patientCollection as patient where study.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0) > 0;
    }

    private void setPatientCollection(Collection<Patient> allPatientObjects,
        List<PatientWrapper> allPatientWrappers) {
        Collection<Patient> oldPatients = wrappedObject.getPatientCollection();
        wrappedObject.setPatientCollection(allPatientObjects);
        propertyChangeSupport.firePropertyChange("patientCollection",
            oldPatients, allPatientObjects);
        propertiesMap.put("patientCollection", allPatientWrappers);
    }

    public void addPatients(List<PatientWrapper> newPatients) {
        if (newPatients != null && newPatients.size() > 0) {
            Collection<Patient> allPatientObjects = new HashSet<Patient>();
            List<PatientWrapper> allPatientWrappers = new ArrayList<PatientWrapper>();
            // already added patients
            List<PatientWrapper> currentList = getPatientCollection();
            if (currentList != null) {
                for (PatientWrapper patient : currentList) {
                    allPatientObjects.add(patient.getWrappedObject());
                    allPatientWrappers.add(patient);
                }
            }
            // new patients added
            for (PatientWrapper patient : newPatients) {
                allPatientObjects.add(patient.getWrappedObject());
                allPatientWrappers.add(patient);
            }
            setPatientCollection(allPatientObjects, allPatientWrappers);
        }
    }

    @Override
    public int compareTo(ModelWrapper<Study> wrapper) {
        if (wrapper instanceof StudyWrapper) {
            String nameShort1 = wrappedObject.getNameShort();
            String nameShort2 = wrapper.wrappedObject.getNameShort();

            int compare = 0;
            if ((nameShort1 != null) && (nameShort2 != null)) {
                compare = nameShort1.compareTo(nameShort2);
            }
            if (compare == 0) {
                String name1 = wrappedObject.getName();
                String name2 = wrapper.wrappedObject.getName();

                return name1.compareTo(name2);
            }
            return compare;
        }
        return 0;
    }

    public long getPatientCountForClinic(ClinicWrapper clinic)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(distinct patients) from "
            + Study.class.getName() + " as study"
            + " join study.patientCollection as patients"
            + " join patients.shipmentCollection as shipments"
            + " join shipments.clinic as clinic"
            + " where study.id=? and clinic.id=?", Arrays.asList(new Object[] {
            getId(), clinic.getId() }));

        List<Long> result = appService.query(c);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
    }

    public long getPatientVisitCountForClinic(ClinicWrapper clinic)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(distinct visits) from "
            + Study.class.getName() + " as study"
            + " join study.patientCollection as patients"
            + " join patients.shipmentCollection as shipments"
            + " join shipments.clinic as clinic"
            + " join shipments.patientVisitCollection as visits"
            + " where study.id=? and clinic.id=?"
            + " and visits.patient.study=study", Arrays.asList(new Object[] {
            getId(), clinic.getId() }));

        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0);
    }

    public long getPatientVisitCount() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(visits) from "
            + Study.class.getName() + " as study"
            + " inner join study.patientCollection as patients"
            + " inner join patients.patientVisitCollection as visits"
            + " where study.id=? ", Arrays.asList(new Object[] { getId() }));

        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0);
    }

    @Override
    protected void persistDependencies(Study origObject) throws Exception {
        if (studyPvAttrMap != null) {
            Collection<StudyPvAttr> allStudyPvAttrObjects = new HashSet<StudyPvAttr>();
            List<StudyPvAttrWrapper> allStudyPvAttrWrappers = new ArrayList<StudyPvAttrWrapper>();
            for (StudyPvAttrWrapper ss : studyPvAttrMap.values()) {
                allStudyPvAttrObjects.add(ss.getWrappedObject());
                allStudyPvAttrWrappers.add(ss);
            }
            Collection<StudyPvAttr> oldStudyPvAttrs = wrappedObject
                .getStudyPvAttrCollection();
            wrappedObject.setStudyPvAttrCollection(allStudyPvAttrObjects);
            propertyChangeSupport.firePropertyChange("studyPvAttrCollection",
                oldStudyPvAttrs, allStudyPvAttrObjects);
            propertiesMap.put("studyPvAttrCollection", allStudyPvAttrWrappers);
        }
        deleteSampleStorages();
        deleteSourceVessels();
        deleteStudyPvAttrs();
    }

    /**
     * return true if this study is linked to the given clinic (through
     * contacts)
     */
    public boolean isLinkedToClinic(ClinicWrapper clinic)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(clinics) from "
            + Contact.class.getName() + " as contacts"
            + " join contacts.clinic as clinics"
            + " where contacts.studyCollection.id = ? and clinics.id = ?",
            Arrays.asList(new Object[] { getId(), clinic.getId() }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) != 0;
    }

    @Override
    public void resetInternalField() {
        studyPvAttrMap = null;
        deletedSampleStorages.clear();
        deletedSourceVessels.clear();
        deletedStudyPvAttr.clear();
    }

    @Override
    public String toString() {
        return getName();
    }

}
