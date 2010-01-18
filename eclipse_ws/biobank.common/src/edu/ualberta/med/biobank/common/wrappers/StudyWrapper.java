package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends ModelWrapper<Study> {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

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

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(String activityStatus) {
        String oldStatus = getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus", oldStatus,
            activityStatus);
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

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper site) {
        setSite(site.getWrappedObject());
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
            "sampleSourceCollection", "studyPvAttrCollection",
            "patientCollection" };
    }

    @Override
    public Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkNameNotEmpty();
        checkNameShortNotEmpty();
        checkStudyNameUnique();
        checkContactsFromSameSite();
        checkNoPatientRemoved();
    }

    private void checkNameNotEmpty() throws BiobankCheckException {
        if (getName() == null || getName().isEmpty()) {
            throw new BiobankCheckException("Name can't be empty");
        }
    }

    private void checkNameShortNotEmpty() throws BiobankCheckException {
        if (getNameShort() == null || getNameShort().isEmpty()) {
            throw new BiobankCheckException("Short Name can't be empty");
        }
    }

    private void checkNoPatientRemoved() throws BiobankCheckException,
        ApplicationException {
        if (!isNew()) {
            List<PatientWrapper> newPatients = getPatientCollection();
            Study origStudy = new Study();
            origStudy.setId(getId());
            origStudy = (Study) appService.search(Study.class, origStudy)
                .get(0);
            List<PatientWrapper> oldPatients = new StudyWrapper(appService,
                origStudy).getPatientCollection();
            if (oldPatients != null) {
                for (PatientWrapper p : oldPatients) {
                    if ((newPatients == null) || !newPatients.contains(p)) {
                        Patient dbPatient = new Patient();
                        dbPatient.setId(p.getId());
                        // check if still in database
                        if (appService.search(Patient.class, dbPatient).size() == 1) {
                            throw new BiobankCheckException(
                                "Patient "
                                    + p.getPnumber()
                                    + " has been removed from the patients list: this patient should be deleted first.");
                        }
                    }
                }
            }
        }
    }

    private void checkStudyNameUnique() throws BiobankCheckException,
        ApplicationException {
        String sameString = "";
        if (getSite() != null) {
            List<Object> params = new ArrayList<Object>(Arrays
                .asList(new Object[] { getSite().getId(), getName() }));
            if (!isNew()) {
                sameString = " and id <> ?";
                params.add(getId());
            }
            HQLCriteria c = new HQLCriteria("from " + Study.class.getName()
                + " where site.id = ? and name = ?" + sameString, params);
            List<Object> results = appService.query(c);
            if (results.size() > 0) {
                throw new BiobankCheckException("A study with name \""
                    + getName() + "\" already exists.");
            }

            params = new ArrayList<Object>(Arrays.asList(new Object[] {
                getSite().getId(), getNameShort() }));
            if (!isNew()) {
                sameString = " and id <> ?";
                params.add(getId());
            }
            c = new HQLCriteria("from " + Study.class.getName()
                + " where site.id = ? and nameShort = ?" + sameString, params);
            results = appService.query(c);
            if (results.size() > 0) {
                throw new BiobankCheckException("A study with short name \""
                    + getNameShort() + "\" already exists.");
            }
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

    public void setContactCollection(Collection<Contact> contacts,
        boolean setNull) {
        Collection<Contact> oldContacts = wrappedObject.getContactCollection();
        wrappedObject.setContactCollection(contacts);
        propertyChangeSupport.firePropertyChange("contactCollection",
            oldContacts, contacts);
        if (setNull) {
            propertiesMap.put("contactCollection", null);
        }
    }

    public void setContactCollection(List<ContactWrapper> contacts) {
        Collection<Contact> contactObjects = new HashSet<Contact>();
        if (contacts != null) {
            for (ContactWrapper contact : contacts) {
                contactObjects.add(contact.getWrappedObject());
            }
        }
        setContactCollection(contactObjects, false);
        propertiesMap.put("contactCollection", contacts);
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

    public void setSampleStorageCollection(
        Collection<SampleStorage> collection, boolean setNull) {
        Collection<SampleStorage> oldSampleStorage = wrappedObject
            .getSampleStorageCollection();
        wrappedObject.setSampleStorageCollection(collection);
        propertyChangeSupport.firePropertyChange("sampleStorageCollection",
            oldSampleStorage, collection);
        if (setNull) {
            propertiesMap.put("sampleStorageCollection", null);
        }
    }

    public void setSampleStorageCollection(
        List<SampleStorageWrapper> ssCollection) {
        Collection<SampleStorage> ssObjects = new HashSet<SampleStorage>();
        for (SampleStorageWrapper ss : ssCollection) {
            ss.setStudy(wrappedObject);
            ssObjects.add(ss.getWrappedObject());
        }
        setSampleStorageCollection(ssObjects, false);
        propertiesMap.put("sampleStorageCollection", ssCollection);
    }

    /*
     * Removes the StudyPvAttr objects that are not contained in the collection.
     */
    private void deleteStudyPvAttrDifference(Study origStudy) throws Exception {
        List<StudyPvAttrWrapper> oldStudyPvAttrList = new StudyWrapper(
            appService, origStudy).getStudyPvAttrCollection();
        if (oldStudyPvAttrList == null) {
            return;
        }
        getStudyPvAttrMap();
        int newStudyPvAttrCount = studyPvAttrMap.size();
        for (StudyPvAttrWrapper st : oldStudyPvAttrList) {
            if ((newStudyPvAttrCount == 0)
                || (studyPvAttrMap.get(st.getLabel()) == null)) {
                st.delete();
            }
        }
    }

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     * 
     * @param ssCollection
     * @throws BiobankCheckException
     * @throws Exception
     */
    private void deleteSampleStorageDifference(Study origStudy)
        throws Exception {
        List<SampleStorageWrapper> newSampleStorage = getSampleStorageCollection();
        List<SampleStorageWrapper> oldSampleStorage = new StudyWrapper(
            appService, origStudy).getSampleStorageCollection();
        if (oldSampleStorage != null) {
            for (SampleStorageWrapper st : oldSampleStorage) {
                if ((newSampleStorage == null)
                    || !newSampleStorage.contains(st)) {
                    st.delete();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<SampleSourceWrapper> getSampleSourceCollection(boolean sort) {
        List<SampleSourceWrapper> ssCollection = (List<SampleSourceWrapper>) propertiesMap
            .get("SampleSourceCollection");
        if (ssCollection == null) {
            Collection<SampleSource> children = wrappedObject
                .getSampleSourceCollection();
            if (children != null) {
                ssCollection = new ArrayList<SampleSourceWrapper>();
                for (SampleSource study : children) {
                    ssCollection
                        .add(new SampleSourceWrapper(appService, study));
                }
                propertiesMap.put("sampleSourceCollection", ssCollection);
            }
        }
        if ((ssCollection != null) && sort)
            Collections.sort(ssCollection);
        return ssCollection;
    }

    public List<SampleSourceWrapper> getSampleSourceCollection() {
        return getSampleSourceCollection(false);
    }

    public void setSampleSourceCollection(Collection<SampleSource> ss,
        boolean setNull) {
        Collection<SampleSource> oldSampleSource = wrappedObject
            .getSampleSourceCollection();
        wrappedObject.setSampleSourceCollection(ss);
        propertyChangeSupport.firePropertyChange("sampleSourceCollection",
            oldSampleSource, ss);
        if (setNull) {
            propertiesMap.put("sampleSourceCollection", null);
        }
    }

    public void setSampleSourceCollection(List<SampleSourceWrapper> ssCollection) {
        Collection<SampleSource> ssObjects = new HashSet<SampleSource>();
        for (SampleSourceWrapper ss : ssCollection) {
            ssObjects.add(ss.getWrappedObject());
        }
        setSampleSourceCollection(ssObjects, false);
        propertiesMap.put("sampleSourceCollection", ssCollection);
    }

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     * 
     * @param newCollection
     * @throws BiobankCheckException
     * @throws Exception
     */
    private void deleteSampleSourceDifference(Study origStudy) throws Exception {
        List<SampleSourceWrapper> newSampleSource = getSampleSourceCollection();
        List<SampleSourceWrapper> oldSampleSource = new StudyWrapper(
            appService, origStudy).getSampleSourceCollection();
        if (oldSampleSource != null) {
            for (SampleSourceWrapper ss : oldSampleSource) {
                if ((newSampleSource == null) || !newSampleSource.contains(ss)) {
                    ss.delete();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<StudyPvAttrWrapper> getStudyPvAttrCollection() {
        List<StudyPvAttrWrapper> studyPvAttrCollection = (List<StudyPvAttrWrapper>) propertiesMap
            .get("studyPvAttrCollection");
        if (studyPvAttrCollection == null) {
            Collection<StudyPvAttr> children = wrappedObject
                .getStudyPvAttrCollection();
            if (children != null) {
                studyPvAttrCollection = new ArrayList<StudyPvAttrWrapper>();
                for (StudyPvAttr studyPvAttr : children) {
                    studyPvAttrCollection.add(new StudyPvAttrWrapper(
                        appService, studyPvAttr));
                }
                propertiesMap.put("studyPvAttrCollection",
                    studyPvAttrCollection);
            }
        }
        return studyPvAttrCollection;
    }

    private void setStudyPvAttrCollection(
        Collection<StudyPvAttr> studyPvAttrCollection, boolean setNull) {
        Collection<StudyPvAttr> oldStudyPvAttrs = wrappedObject
            .getStudyPvAttrCollection();
        wrappedObject.setStudyPvAttrCollection(studyPvAttrCollection);
        propertyChangeSupport.firePropertyChange("studyPvAttrCollection",
            oldStudyPvAttrs, studyPvAttrCollection);
        if (setNull) {
            propertiesMap.put("studyPvAttrCollection", null);
        }
    }

    private void setStudyPvAttrCollection(
        List<StudyPvAttrWrapper> studyPvAttrCollection) {
        Collection<StudyPvAttr> studyPvAttrObjects = new HashSet<StudyPvAttr>();
        for (StudyPvAttrWrapper studyPvAttr : studyPvAttrCollection) {
            studyPvAttrObjects.add(studyPvAttr.getWrappedObject());
        }
        setStudyPvAttrCollection(studyPvAttrObjects, false);
        propertiesMap.put("studyPvAttrCollection", studyPvAttrCollection);
    }

    private Map<String, StudyPvAttrWrapper> getStudyPvAttrMap() {
        if (studyPvAttrMap != null)
            return studyPvAttrMap;

        studyPvAttrMap = new HashMap<String, StudyPvAttrWrapper>();
        List<StudyPvAttrWrapper> studyPvAttrCollection = getStudyPvAttrCollection();
        if (studyPvAttrCollection != null) {
            for (StudyPvAttrWrapper studyPvAttr : studyPvAttrCollection) {
                studyPvAttrMap.put(studyPvAttr.getLabel(), studyPvAttr);
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
     * Retrieves the locked status for a patient visit attribute. If locked,
     * patient visits will not allow information to be saved for this attribute.
     * 
     * @param label
     * @return True if the attribute is locked. False otherwise.
     * @throws Exception
     */
    public Boolean getStudyPvAttrLocked(String label) throws Exception {
        return getStudyPvAttr(label).getLocked();
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
            // type has permissble values
            if ((studyPvAttr == null) && (permissibleValues == null)) {
                // nothing to do
                return;
            }

            if ((studyPvAttr != null) && (permissibleValues == null)) {
                studyPvAttr.delete();
                studyPvAttrMap.remove(label);
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

        studyPvAttr.setLocked(false);
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
    public void setStudyPvAttrLocked(String label, Boolean enable)
        throws Exception {
        getStudyPvAttrMap();
        StudyPvAttrWrapper studyPvAttr = getStudyPvAttr(label);
        studyPvAttr.setLocked(enable);
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
        setStudyPvAttrCollection(new ArrayList<StudyPvAttrWrapper>(
            studyPvAttrMap.values()));
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
            + " as patients where patients.pnumber = ?", Arrays
            .asList(new Object[] { patientNumber }));
        List<Patient> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return new PatientWrapper(appService, result.get(0));
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

    public void setPatientCollection(Collection<Patient> patients,
        boolean setNull) {
        Collection<Patient> oldPatients = wrappedObject.getPatientCollection();
        wrappedObject.setPatientCollection(patients);
        propertyChangeSupport.firePropertyChange("patientCollection",
            oldPatients, patients);
        if (setNull) {
            propertiesMap.put("patientCollection", null);
        }
    }

    public void setPatientCollection(List<PatientWrapper> patients) {
        Collection<Patient> patientsObjects = new HashSet<Patient>();
        for (PatientWrapper p : patients) {
            patientsObjects.add(p.getWrappedObject());
        }
        setPatientCollection(patientsObjects, false);
        propertiesMap.put("patientCollection", patients);
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
        HQLCriteria c = new HQLCriteria("select count(*) from "
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
        HQLCriteria c = new HQLCriteria("select count(visits) from "
            + Study.class.getName() + " as study"
            + " join study.patientCollection as patients"
            + " join patients.patientVisitCollection as visits"
            + " join visits.shipment as shipments"
            + " join shipments.clinic as clinic"
            + " where study.id=? and clinic.id=?", Arrays.asList(new Object[] {
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
            setStudyPvAttrCollection(new ArrayList<StudyPvAttrWrapper>(
                studyPvAttrMap.values()));
        }
        if (origObject != null) {
            deleteSampleStorageDifference(origObject);
            deleteSampleSourceDifference(origObject);
            deleteStudyPvAttrDifference(origObject);
        }
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        studyPvAttrMap = null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
