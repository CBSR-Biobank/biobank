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

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends ModelWrapper<Study> {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

    private Set<SampleStorageWrapper> deletedSampleStorages = new HashSet<SampleStorageWrapper>();

    private Set<StudySourceVesselWrapper> deletedStudySourceVessels = new HashSet<StudySourceVesselWrapper>();

    private Set<StudyPvAttrWrapper> deletedStudyPvAttr = new HashSet<StudyPvAttrWrapper>();

    private ActivityStatusWrapper activityStatus;

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
        if (activityStatus == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activityStatus = new ActivityStatusWrapper(appService, a);
        }
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        this.activityStatus = activityStatus;
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

    @Deprecated
    public SiteWrapper getSite() {
        return null;
    }

    @SuppressWarnings("unused")
    @Deprecated
    public void setSite(SiteWrapper site) {
    }

    @SuppressWarnings("unchecked")
    public List<SiteWrapper> getSiteCollection(boolean sort) {
        List<SiteWrapper> siteCollection = (List<SiteWrapper>) propertiesMap
            .get("siteCollection");
        if (siteCollection == null) {
            siteCollection = new ArrayList<SiteWrapper>();
            Collection<Site> children = wrappedObject.getSiteCollection();
            if (children != null) {
                for (Site type : children) {
                    siteCollection.add(new SiteWrapper(appService, type));
                }
                propertiesMap.put("siteCollection", siteCollection);
            }
        }
        if ((siteCollection != null) && sort)
            Collections.sort(siteCollection);
        return siteCollection;
    }

    public List<SiteWrapper> getSiteCollection() {
        return getSiteCollection(false);
    }

    private void setSiteCollection(Collection<Site> allSiteObjects,
        List<SiteWrapper> allSiteWrappers) {
        Collection<Site> oldSites = wrappedObject.getSiteCollection();
        wrappedObject.setSiteCollection(allSiteObjects);
        propertyChangeSupport.firePropertyChange("siteCollection", oldSites,
            allSiteObjects);
        propertiesMap.put("siteCollection", allSiteWrappers);
    }

    public void addSites(List<SiteWrapper> newSites) {
        if ((newSites == null) || (newSites.size() == 0))
            return;

        Collection<Site> allSiteObjects = new HashSet<Site>();
        List<SiteWrapper> allSiteWrappers = new ArrayList<SiteWrapper>();
        // already added Sites
        List<SiteWrapper> currentList = getSiteCollection();
        if (currentList != null) {
            for (SiteWrapper Site : currentList) {
                allSiteObjects.add(Site.getWrappedObject());
                allSiteWrappers.add(Site);
            }
        }
        // new Sites added
        for (SiteWrapper Site : newSites) {
            allSiteObjects.add(Site.getWrappedObject());
            allSiteWrappers.add(Site);
        }
        setSiteCollection(allSiteObjects, allSiteWrappers);
    }

    public void removeSites(List<SiteWrapper> SitesToRemove) {
        if (SitesToRemove != null && SitesToRemove.size() > 0) {
            Collection<Site> allSiteObjects = new HashSet<Site>();
            List<SiteWrapper> allSiteWrappers = new ArrayList<SiteWrapper>();
            // already added Sites
            List<SiteWrapper> currentList = getSiteCollection();
            if (currentList != null) {
                for (SiteWrapper Site : currentList) {
                    if (!SitesToRemove.contains(Site)) {
                        allSiteObjects.add(Site.getWrappedObject());
                        allSiteWrappers.add(Site);
                    }
                }
            }
            setSiteCollection(allSiteObjects, allSiteWrappers);
        }
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
            "siteCollection", "contactCollection", "sampleStorageCollection",
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
        checkNotEmpty(getNameShort(), "Short Name");
        checkNoDuplicates(Study.class, "name", getName(),
            "A study with name \"" + getName() + "\" already exists.");
        checkNoDuplicates(Study.class, "nameShort", getNameShort(),
            "A study with short name \"" + getNameShort()
                + "\" already exists.");
        checkValidActivityStatus();
    }

    private void checkValidActivityStatus() throws BiobankCheckException {
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the clinic does not have an activity status");
        }
    }

    @SuppressWarnings("unchecked")
    public List<ContactWrapper> getContactCollection(boolean sort) {
        List<ContactWrapper> contactCollection = (List<ContactWrapper>) propertiesMap
            .get("contactCollection");
        if (contactCollection == null) {
            contactCollection = new ArrayList<ContactWrapper>();
            Collection<Contact> children = wrappedObject.getContactCollection();
            if (children != null) {
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
            + "order by contacts.clinic.name",
            Arrays.asList(new Object[] { getId() }));
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

    /**
     * Removes the study source vessel objects that are not contained in the
     * collection.
     */
    private void deleteStudySourceVessels() throws Exception {
        for (StudySourceVesselWrapper st : deletedStudySourceVessels) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<StudySourceVesselWrapper> getStudySourceVesselCollection(
        boolean sort) {
        List<StudySourceVesselWrapper> ssCollection = (List<StudySourceVesselWrapper>) propertiesMap
            .get("studySourceVesselCollection");
        if (ssCollection == null) {
            Collection<StudySourceVessel> children = wrappedObject
                .getStudySourceVesselCollection();
            if (children != null) {
                ssCollection = new ArrayList<StudySourceVesselWrapper>();
                for (StudySourceVessel study : children) {
                    ssCollection.add(new StudySourceVesselWrapper(appService,
                        study));
                }
                propertiesMap.put("sourceVesselCollection", ssCollection);
            }
        }
        if ((ssCollection != null) && sort)
            Collections.sort(ssCollection);
        return ssCollection;
    }

    public List<StudySourceVesselWrapper> getStudySourceVesselCollection() {
        return getStudySourceVesselCollection(false);
    }

    private void setStudySourceVessels(
        Collection<StudySourceVessel> allSsObject,
        List<StudySourceVesselWrapper> allSsWrappers) {
        Collection<StudySourceVessel> oldSourceVessels = wrappedObject
            .getStudySourceVesselCollection();
        wrappedObject.setStudySourceVesselCollection(allSsObject);
        propertyChangeSupport.firePropertyChange("studySourceVesselCollection",
            oldSourceVessels, allSsObject);
        propertiesMap.put("studySourceVesselCollection", allSsWrappers);
    }

    public void addStudySourceVessels(
        List<StudySourceVesselWrapper> newStudySourceVessels) {
        if (newStudySourceVessels != null && newStudySourceVessels.size() > 0) {
            Collection<StudySourceVessel> allSsObjects = new HashSet<StudySourceVessel>();
            List<StudySourceVesselWrapper> allSsWrappers = new ArrayList<StudySourceVesselWrapper>();
            // already in list
            List<StudySourceVesselWrapper> currentList = getStudySourceVesselCollection();
            if (currentList != null) {
                for (StudySourceVesselWrapper ss : currentList) {
                    allSsObjects.add(ss.getWrappedObject());
                    allSsWrappers.add(ss);
                }
            }
            // new
            for (StudySourceVesselWrapper ss : newStudySourceVessels) {
                allSsObjects.add(ss.getWrappedObject());
                allSsWrappers.add(ss);
                deletedStudySourceVessels.remove(ss);
            }
            setStudySourceVessels(allSsObjects, allSsWrappers);
        }
    }

    public void removeStudySourceVessels(
        List<StudySourceVesselWrapper> studySourceVesselsToDelete) {
        if (studySourceVesselsToDelete != null
            && studySourceVesselsToDelete.size() > 0) {
            deletedStudySourceVessels.addAll(studySourceVesselsToDelete);
            Collection<StudySourceVessel> allSsObjects = new HashSet<StudySourceVessel>();
            List<StudySourceVesselWrapper> allSsWrappers = new ArrayList<StudySourceVesselWrapper>();
            // already in list
            List<StudySourceVesselWrapper> currentList = getStudySourceVesselCollection();
            if (currentList != null) {
                for (StudySourceVesselWrapper ss : currentList) {
                    if (!deletedStudySourceVessels.contains(ss)) {
                        allSsObjects.add(ss.getWrappedObject());
                        allSsWrappers.add(ss);
                    }
                }
            }
            setStudySourceVessels(allSsObjects, allSsWrappers);
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

        for (StudyPvAttrWrapper studyPvAttr : StudyPvAttrWrapper
            .getStudyPvAttrCollection(this)) {
            studyPvAttrMap.put(studyPvAttr.getLabel(), studyPvAttr);
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

    public List<ClinicWrapper> getClinicCollection() {
        List<ContactWrapper> contacts = getContactCollection();
        List<ClinicWrapper> clinicWrappers = new ArrayList<ClinicWrapper>();
        if (contacts != null)
            for (ContactWrapper contact : contacts) {
                clinicWrappers.add(contact.getClinic());
            }
        return clinicWrappers;
    }

    public boolean hasClinic(String clinicNameShort) {
        List<ClinicWrapper> clinics = getClinicCollection();
        if (clinics != null)
            for (ClinicWrapper c : clinics)
                if (c.getNameShort().equals(clinicNameShort))
                    return true;
        return false;
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

    public long getPatientCount() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria(
            "select count(patient) from "
                + Study.class.getName()
                + " as study inner join study.patientCollection as patient where study.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> results = appService.query(criteria);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0);
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
        deleteStudySourceVessels();
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
        deletedStudySourceVessels.clear();
        deletedStudyPvAttr.clear();
    }

    public static List<StudyWrapper> getAllStudies(
        WritableApplicationService appService) {
        List<Study> studies = new ArrayList<Study>();
        List<StudyWrapper> wrappers = new ArrayList<StudyWrapper>();
        HQLCriteria c = new HQLCriteria("from " + Study.class.getName());
        try {
            studies = appService.query(c);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        for (Study study : studies)
            wrappers.add(new StudyWrapper(appService, study));
        return wrappers;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        activityStatus = null;
    }

}
