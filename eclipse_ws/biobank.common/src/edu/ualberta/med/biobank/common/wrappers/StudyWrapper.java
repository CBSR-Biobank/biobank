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
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
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

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setSiteWrapper(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "nameShort", "activityStatus", "comment",
            "site", "contactCollection", "sampleStorageCollection",
            "sampleSourceCollection", "pvInfoCollection" };
    }

    @Override
    public Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        checkStudyNameUnique();
    }

    private void checkStudyNameUnique() throws BiobankCheckException, Exception {
        HQLCriteria c;

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Study.class.getName()
                + " where site = ? and name = ?", Arrays.asList(new Object[] {
                getSite(), getName() }));
        } else {
            c = new HQLCriteria("from " + Study.class.getName()
                + " as study where site = ? and name = ?  and study <> ?",
                Arrays.asList(new Object[] { getSite(), getName(),
                    getWrappedObject() }));
        }

        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A study with name \"" + getName()
                + "\" already exists.");
        }

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Study.class.getName()
                + " site = ? and study.nameShort = ?", Arrays
                .asList(new Object[] { getSite(), getNameShort() }));
        } else {
            c = new HQLCriteria("from " + Study.class.getName() + " as study"
                + " where site = ? and study.nameShort = ? and study <> ?",
                Arrays.asList(new Object[] { getSite(), getNameShort(),
                    getWrappedObject() }));
        }

        results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A study with short name \""
                + getNameShort() + "\" already exists.");
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
        for (ContactWrapper contact : contacts) {
            contactObjects.add(contact.getWrappedObject());
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
        List<SampleStorageWrapper> ssCollection) throws Exception {
        Collection<SampleStorage> ssObjects = new HashSet<SampleStorage>();
        for (SampleStorageWrapper ss : ssCollection) {
            ss.setStudy(wrappedObject);
            ssObjects.add(ss.getWrappedObject());
        }
        setSampleStorageCollection(ssObjects, false);
        propertiesMap.put("sampleStorageCollection", ssCollection);
    }

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     * 
     * @param ssCollection
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

    public void setSampleSourceCollection(List<SampleSourceWrapper> ssCollection)
        throws Exception {
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
     * @throws Exception
     */
    private void deleteSampleSourceDifference(
        List<SampleSourceWrapper> newCollection) throws Exception {
        // no need to remove if study is not yet in the database or nothing in
        // the collection
        if (isNew())
            return;

        List<SampleSourceWrapper> currSamplesSources = getSampleSourceCollection();
        if (currSamplesSources.size() == 0)
            return;

        if (newCollection.size() == 0) {
            // remove all
            Iterator<SampleSourceWrapper> it = currSamplesSources.iterator();
            while (it.hasNext()) {
                it.next().delete();
            }
            return;
        }

        List<Integer> idList = new ArrayList<Integer>();
        for (SampleSourceWrapper ss : newCollection) {
            idList.add(ss.getId());
        }
        Iterator<SampleSourceWrapper> it = currSamplesSources.iterator();
        while (it.hasNext()) {
            SampleSourceWrapper ss = it.next();
            if (!idList.contains(ss.getId())) {
                ss.delete();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<PvInfoWrapper> getPvInfoCollection() {
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

    public String[] getPvInfoLabels() throws ApplicationException {
        getPvInfoMap();
        return pvInfoMap.keySet().toArray(new String[] {});
    }

    public String[] getPvInfoAllowedValues(String label) throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = pvInfoMap.get(label);
        if (pvInfo == null) {
            throw new Exception("PvInfo with label \"" + label
                + "\" is invalid");
        }
        String joinedPossibleValues = pvInfo.getAllowedValues();
        if (joinedPossibleValues == null)
            return null;
        return joinedPossibleValues.split(";");
    }

    public void setPvInfo(String label, String[] possibleValues)
        throws Exception {
        getPvInfoMap();
        PvInfoWrapper pvInfo = pvInfoMap.get(label);
        if (pvInfo == null) {
            throw new Exception("PvInfo with label \"" + label
                + "\" is invalid");
        }
        pvInfo = new PvInfoWrapper(appService, new PvInfo());
        pvInfo.setLabel(label);
        pvInfo.ssetAllowedValues(StringUtils.join(possibleValues, ';'));
        pvInfoMap.put(label, pvInfo);
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

    @Override
    public int compareTo(ModelWrapper<Study> wrapper) {
        String name1 = wrappedObject.getName();
        String name2 = wrapper.wrappedObject.getName();

        int compare = name1.compareTo(name2);
        if (compare == 0) {
            String nameShort1 = wrappedObject.getNameShort();
            String nameShort2 = wrapper.wrappedObject.getNameShort();

            return ((nameShort1.compareTo(nameShort2) > 0) ? 1 : (nameShort1
                .equals(nameShort2) ? 0 : -1));
        }
        return (compare > 0) ? 1 : -1;
    }

    @Override
    public void persist() throws BiobankCheckException, Exception {
        if (pvInfoMap != null) {
            setPvInfoCollection(new ArrayList<PvInfoWrapper>(pvInfoMap.values()));
        }
        super.persist();

    }

    @Override
    protected void persistDependencies(Site origObject)
        throws BiobankCheckException, Exception {
        deleteSampleStorageDifference(origObject);
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pvInfoMap = null;
    }
}
