package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoPossibleWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends ModelWrapper<Study> {

    private Map<String, PvInfoWrapper> pvInfoMap;

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
        pvInfoMap = null;
    }

    public StudyWrapper(WritableApplicationService appService) {
        super(appService);
        pvInfoMap = null;
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
            "sampleSourceCollection", "pvInfoCollection", "patientCollection" };
    }

    @Override
    public Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkStudyNameUnique();
        checkContactsFromSameSite();
        checkNoPatientRemoved();
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
                                    + p.getNumber()
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
        List<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {
            getSite().getId(), getName() }));
        if (!isNew()) {
            sameString = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + Study.class.getName()
            + " where site.id = ? and name = ?" + sameString, params);
        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A study with name \"" + getName()
                + "\" already exists.");
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
     * Removes the PvInfo objects that are not contained in the collection.
     */
    private void deletePvInfoDifference(Study origStudy)
        throws BiobankCheckException, ApplicationException, WrapperException {
        List<PvInfoWrapper> oldPvInfoList = new StudyWrapper(appService,
            origStudy).getPvInfoCollection();
        if (oldPvInfoList == null) {
            return;
        }
        int newPvInfoCount = pvInfoMap.size();
        for (PvInfoWrapper st : oldPvInfoList) {
            if ((newPvInfoCount == 0) || (pvInfoMap.get(st.getLabel()) == null)) {
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
        throws BiobankCheckException, ApplicationException, WrapperException {
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
    private void deleteSampleSourceDifference(Study origStudy)
        throws BiobankCheckException, ApplicationException, WrapperException {
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
    protected List<PvInfoWrapper> getPvInfoCollection() {
        List<PvInfoWrapper> pvInfoCollection = (List<PvInfoWrapper>) propertiesMap
            .get("pvInfoCollection");
        if (pvInfoCollection == null) {
            Collection<PvInfo> children = wrappedObject.getPvInfoCollection();
            if (children != null) {
                pvInfoCollection = new ArrayList<PvInfoWrapper>();
                for (PvInfo pvInfo : children) {
                    pvInfoCollection.add(new PvInfoWrapper(appService, pvInfo));
                }
                propertiesMap.put("pvInfoCollection", pvInfoCollection);
            }
        }
        return pvInfoCollection;
    }

    private void setPvInfoCollection(Collection<PvInfo> pvInfoCollection,
        boolean setNull) {
        Collection<PvInfo> oldPvInfos = wrappedObject.getPvInfoCollection();
        wrappedObject.setPvInfoCollection(pvInfoCollection);
        propertyChangeSupport.firePropertyChange("pvInfoCollection",
            oldPvInfos, pvInfoCollection);
        if (setNull) {
            propertiesMap.put("pvInfoCollection", null);
        }
    }

    private void setPvInfoCollection(List<PvInfoWrapper> pvInfoCollection) {
        Collection<PvInfo> pvInfosObjects = new HashSet<PvInfo>();
        for (PvInfoWrapper pvInfos : pvInfoCollection) {
            pvInfosObjects.add(pvInfos.getWrappedObject());
        }
        setPvInfoCollection(pvInfosObjects, false);
        propertiesMap.put("pvInfoCollection", pvInfoCollection);
    }

    private Map<String, PvInfoWrapper> getPvInfoMap() {
        if (pvInfoMap != null)
            return pvInfoMap;

        pvInfoMap = new HashMap<String, PvInfoWrapper>();
        List<PvInfoWrapper> pvInfoCollection = getPvInfoCollection();
        if (pvInfoCollection != null) {
            for (PvInfoWrapper pvInfo : pvInfoCollection) {
                pvInfoMap.put(pvInfo.getLabel(), pvInfo);
            }
        }
        return pvInfoMap;
    }

    public String[] getPvInfoLabels() {
        getPvInfoMap();
        return pvInfoMap.keySet().toArray(new String[] {});
    }

    public void setPvInfoLabels(String[] labels) {
        getPvInfoMap();
        List<String> labelList = Arrays.asList(labels);
        for (Iterator<String> i = pvInfoMap.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            if (!labelList.contains(key)) {
                i.remove();
            }
        }
    }

    protected PvInfoWrapper getPvInfo(String label) throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = pvInfoMap.get(label);
        if (pvInfo == null) {
            throw new Exception("PvInfo with label \"" + label
                + "\" is invalid");
        }
        return pvInfo;
    }

    public Integer getPvInfoType(String label) throws Exception {
        return getPvInfo(label).getPvInfoType().getId();
    }

    private PvInfoWrapper getNewPvInfo(String label) throws Exception {
        PvInfoWrapper pvInfo;
        // is label a valid PvInfoPossible value?
        SiteWrapper site = getSite();
        if (site == null) {
            throw new Exception("site is null");
        }
        PvInfoPossibleWrapper pip = site.getPvInfoPossible(label);
        if (pip == null) {
            throw new Exception("PvInfo with label \"" + label
                + "\" is invalid: not in PvInfoPossible");
        }

        // label is a valid PvInfoPossible, add PvInfo to the study
        pvInfo = new PvInfoWrapper(appService);
        pvInfo.setPvInfoPossible(pip);
        pvInfo.setLabel(label);
        return pvInfo;
    }

    /**
     * Retrieves the allowed values for a patient visit additional information
     * item.
     * 
     * @param label The label to be used by the information item.
     * @return Semicolon separated list of allowed values.
     * @throws Exception hrown if there is no patient visit information item
     *             with the label specified.
     */
    public String[] getPvInfoAllowedValues(String label) throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = getPvInfo(label);
        if (pvInfo == null) {
            // this pv info does not exist yet
            pvInfo = getNewPvInfo(label);
        }
        String joinedPossibleValues = pvInfo.getAllowedValues();
        if (joinedPossibleValues == null)
            return null;
        return joinedPossibleValues.split(";");
    }

    /**
     * Assigns patient visit additional information items.
     * 
     * @param label The label to be used for the information item. Note: the
     *            label must already be in the patient visit possible values for
     *            the site associated with the study.
     * @param allowedValues If the information item is of type "select_single"
     *            or "select_multiple" this array contains the possible values
     *            as a String array. Otherwise, this parameter should be set to
     *            null.
     * 
     * @throws Exception Thrown if there is no possible patient visit with the
     *             label specified.
     */
    public void setPvInfo(String label, String[] allowedValues)
        throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = pvInfoMap.get(label);
        if (pvInfo == null) {
            pvInfo = getNewPvInfo(label);
        }
        pvInfo.setAllowedValues(StringUtils.join(allowedValues, ';'));
        pvInfoMap.put(label, pvInfo);
    }

    /**
     * Assigns patient visit additional information items.
     * 
     * @param label The label to be used for the information item. Note: the
     *            label must already be in the patient visit possible values for
     *            the site associated with the study.
     * 
     * @throws Exception Thrown if there is no possible patient visit with the
     *             label specified.
     */
    public void setPvInfo(String label) throws Exception {
        setPvInfo(label, null);
    }

    public void deletePvInfo(String label) throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = pvInfoMap.get(label);
        if (pvInfo == null) {
            throw new Exception("PvInfo with label \"" + label
                + "\" does not exist");
        }
        pvInfoMap.remove(label);
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
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();

            int compare = name1.compareTo(name2);
            if (compare == 0) {
                String nameShort1 = wrappedObject.getNameShort();
                String nameShort2 = wrapper.wrappedObject.getNameShort();

                return ((nameShort1.compareTo(nameShort2) > 0) ? 1
                    : (nameShort1.equals(nameShort2) ? 0 : -1));
            }
            return (compare > 0) ? 1 : -1;
        }
        return 0;
    }

    public long getPatientCountForClinic(ClinicWrapper clinic)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(distinct patient) from "
            + Study.class.getName() + " as study"
            + " inner join study.contactCollection as contacts"
            + " inner join contacts.clinic as clinic"
            + " inner join clinic.shipmentCollection as shipments"
            + " inner join shipments.patientVisitCollection as visits"
            + " inner join visits.patient as patient"
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
            + " inner join study.contactCollection as contacts"
            + " inner join contacts.clinic as clinic"
            + " inner join clinic.shipmentCollection as shipments"
            + " inner join shipments.patientVisitCollection as visits"
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
        HQLCriteria c = new HQLCriteria("select count(visits)" + " from "
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
    protected void persistDependencies(Study origObject)
        throws BiobankCheckException, ApplicationException, WrapperException {
        // add new PvInfos
        if (pvInfoMap != null) {
            List<PvInfoWrapper> list = new ArrayList<PvInfoWrapper>(pvInfoMap
                .values());
            for (PvInfoWrapper pvInfo : list) {
                if (pvInfo.isNew()) {
                    pvInfo.persist();
                }
            }
            setPvInfoCollection(list);
        }

        if (origObject != null) {
            deleteSampleStorageDifference(origObject);
            deleteSampleSourceDifference(origObject);
            deletePvInfoDifference(origObject);
        }
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pvInfoMap = null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
