package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoPossibleWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoTypeWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> {

    private Map<String, PvInfoPossibleWrapper> pvInfoPossibleMap;

    private Map<String, PvInfoTypeWrapper> pvInfoTypeMap;

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
        pvInfoPossibleMap = null;
        pvInfoTypeMap = null;
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
        pvInfoPossibleMap = null;
        pvInfoTypeMap = null;
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "activityStatus", "comment", "address",
            "clinicCollection", "siteCollection", "containerCollection",
            "sampleTypeCollection", "pvInfoPossibleCollection" };
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
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

    private AddressWrapper getAddress() {
        Address address = wrappedObject.getAddress();
        if (address == null) {
            return null;
        }
        return new AddressWrapper(appService, address);
    }

    private void setAddress(Address address) {
        Address oldAddress = wrappedObject.getAddress();
        wrappedObject.setAddress(address);
        propertyChangeSupport
            .firePropertyChange("address", oldAddress, address);
    }

    private AddressWrapper initAddress() {
        setAddress(new Address());
        return getAddress();
    }

    public String getStreet1() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getStreet1();
    }

    public void setStreet1(String street1) {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet1(street1);
    }

    public String getStreet2() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getStreet2();
    }

    public void setStreet2(String street2) {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet2(street2);
    }

    public String getCity() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getCity();
    }

    public void setCity(String city) {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setCity(city);
    }

    public String getProvince() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getProvince();
    }

    public void setProvince(String province) {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setProvince(province);
    }

    public String getPostalCode() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setPostalCode(postalCode);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        if (getAddress() == null) {
            throw new BiobankCheckException("the site does not have an address");
        }

        if (!checkSiteNameUnique()) {
            throw new BiobankCheckException("A site with name \"" + getName()
                + "\" already exists.");
        }
    }

    private boolean checkSiteNameUnique() throws ApplicationException {
        HQLCriteria c;

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Site.class.getName()
                + " where name = ?", Arrays.asList(new Object[] { getName() }));
        } else {
            c = new HQLCriteria("from " + Site.class.getName()
                + " as site where site <> ? and name = ?", Arrays
                .asList(new Object[] { getWrappedObject(), getName() }));
        }

        List<Object> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    public Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        if ((getClinicCollection() != null && getClinicCollection().size() > 0)
            || (getContainerCollection() != null && getContainerCollection()
                .size() > 0)
            || (getContainerTypeCollection() != null && getContainerTypeCollection()
                .size() > 0)
            || (getStudyCollection() != null && getStudyCollection().size() > 0)) {
            throw new BiobankCheckException(
                "Unable to delete site "
                    + getName()
                    + ". All defined children (studies, clinics, container types, and containers) must be removed first.");
        }

    }

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getStudyCollection(boolean sort) {
        List<StudyWrapper> clinicCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");
        if (clinicCollection == null) {
            Collection<Study> children = wrappedObject.getStudyCollection();
            if (children != null) {
                clinicCollection = new ArrayList<StudyWrapper>();
                for (Study study : children) {
                    clinicCollection.add(new StudyWrapper(appService, study));
                }
                propertiesMap.put("studyCollection", clinicCollection);
            }
        }
        if ((clinicCollection != null) && sort)
            Collections.sort(clinicCollection);
        return clinicCollection;
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
    }

    public void setStudyCollection(Collection<Study> studies, boolean setNull) {
        Collection<Study> oldStudies = wrappedObject.getStudyCollection();
        wrappedObject.setStudyCollection(studies);
        propertyChangeSupport.firePropertyChange("studyCollection", oldStudies,
            studies);
        if (setNull) {
            propertiesMap.put("studyCollection", null);
        }
    }

    public void setStudyCollection(List<StudyWrapper> studies) {
        Collection<Study> studyObjects = new HashSet<Study>();
        for (StudyWrapper study : studies) {
            studyObjects.add(study.getWrappedObject());
        }
        setStudyCollection(studyObjects, false);
        propertiesMap.put("studyCollection", studies);
    }

    @SuppressWarnings("unchecked")
    public List<ClinicWrapper> getClinicCollection(boolean sort) {
        List<ClinicWrapper> clinicCollection = (List<ClinicWrapper>) propertiesMap
            .get("clinicCollection");
        if (clinicCollection == null) {
            Collection<Clinic> children = wrappedObject.getClinicCollection();
            if (children != null) {
                clinicCollection = new ArrayList<ClinicWrapper>();
                for (Clinic clinic : children) {
                    clinicCollection.add(new ClinicWrapper(appService, clinic));
                }
                propertiesMap.put("clinicCollection", clinicCollection);
            }
        }
        if ((clinicCollection != null) && sort)
            Collections.sort(clinicCollection);
        return clinicCollection;
    }

    public List<ClinicWrapper> getClinicCollection() {
        return getClinicCollection(false);
    }

    public void setClinicCollection(Collection<Clinic> clinics, boolean setNull) {
        Collection<Clinic> oldClinics = wrappedObject.getClinicCollection();
        wrappedObject.setClinicCollection(clinics);
        propertyChangeSupport.firePropertyChange("clinicCollection",
            oldClinics, clinics);
        if (setNull) {
            propertiesMap.put("clinicCollection", null);
        }
    }

    public void setClinicCollection(List<ClinicWrapper> clinics) {
        Collection<Clinic> clinicObjects = new HashSet<Clinic>();
        for (ClinicWrapper clinic : clinics) {
            clinicObjects.add(clinic.getWrappedObject());
        }
        setClinicCollection(clinicObjects, false);
        propertiesMap.put("clinicCollection", clinics);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerTypeWrapper> getContainerTypeCollection(boolean sort) {
        List<ContainerTypeWrapper> containerTypeCollection = (List<ContainerTypeWrapper>) propertiesMap
            .get("containerTypeCollection");
        if (containerTypeCollection == null) {
            Collection<ContainerType> children = wrappedObject
                .getContainerTypeCollection();
            if (children != null) {
                containerTypeCollection = new ArrayList<ContainerTypeWrapper>();
                for (ContainerType type : children) {
                    containerTypeCollection.add(new ContainerTypeWrapper(
                        appService, type));
                }
                propertiesMap.put("containerTypeCollection",
                    containerTypeCollection);
            }
        }
        if ((containerTypeCollection != null) && sort)
            Collections.sort(containerTypeCollection);
        return containerTypeCollection;
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection() {
        return getContainerTypeCollection(false);
    }

    public void setContainerTypeCollection(Collection<ContainerType> types,
        boolean setNull) {
        Collection<ContainerType> oldTypes = wrappedObject
            .getContainerTypeCollection();
        wrappedObject.setContainerTypeCollection(types);
        propertyChangeSupport.firePropertyChange("containerTypeCollection",
            oldTypes, types);
        if (setNull) {
            propertiesMap.put("containerTypeCollection", null);
        }
    }

    public void setContainerTypeCollection(List<ContainerTypeWrapper> types) {
        Collection<ContainerType> typeObjects = new HashSet<ContainerType>();
        for (ContainerTypeWrapper type : types) {
            typeObjects.add(type.getWrappedObject());
        }
        setContainerTypeCollection(typeObjects, false);
        propertiesMap.put("containerTypeCollection", types);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getContainerCollection() {
        List<ContainerWrapper> containerCollection = (List<ContainerWrapper>) propertiesMap
            .get("containerCollection");
        if (containerCollection == null) {
            Collection<Container> children = wrappedObject
                .getContainerCollection();
            if (children != null) {
                containerCollection = new ArrayList<ContainerWrapper>();
                for (Container container : children) {
                    containerCollection.add(new ContainerWrapper(appService,
                        container));
                }
                propertiesMap.put("containerCollection", containerCollection);
            }
        }
        return containerCollection;
    }

    public void setContainerCollection(Collection<Container> containers,
        boolean setNull) {
        Collection<Container> oldContainers = wrappedObject
            .getContainerCollection();
        wrappedObject.setContainerCollection(containers);
        propertyChangeSupport.firePropertyChange("containerCollection",
            oldContainers, containers);
        if (setNull) {
            propertiesMap.put("containerCollection", null);
        }
    }

    public void setContainerCollection(List<ContainerWrapper> containers) {
        Collection<Container> containerObjects = new HashSet<Container>();
        for (ContainerWrapper container : containers) {
            containerObjects.add(container.getWrappedObject());
        }
        setContainerCollection(containerObjects, false);
        propertiesMap.put("containerCollection", containers);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getTopContainerCollection(boolean sort)
        throws Exception {
        List<ContainerWrapper> topContainerCollection = (List<ContainerWrapper>) propertiesMap
            .get("topContainerCollection");

        if (topContainerCollection == null) {
            topContainerCollection = new ArrayList<ContainerWrapper>();
            HQLCriteria criteria = new HQLCriteria("from "
                + Container.class.getName()
                + " where site.id = ? and containerType.topLevel = true",
                Arrays.asList(new Object[] { wrappedObject.getId() }));
            List<Container> containers = appService.query(criteria);
            for (Container c : containers) {
                topContainerCollection.add(new ContainerWrapper(appService, c));
            }
            if (sort)
                Collections.sort(topContainerCollection);
            propertiesMap.put("topContainerCollection", topContainerCollection);
        }
        return topContainerCollection;
    }

    public List<ContainerWrapper> getTopContainerCollection() throws Exception {
        return getTopContainerCollection(false);
    }

    public void clearTopContainerCollection() {
        propertiesMap.put("topContainerCollection", null);
    }

    @SuppressWarnings("unchecked")
    public List<SampleTypeWrapper> getSampleTypeCollection(boolean sort) {
        List<SampleTypeWrapper> sampleTypeCollection = (List<SampleTypeWrapper>) propertiesMap
            .get("sampleTypeCollection");
        if (sampleTypeCollection == null) {
            Collection<SampleType> children = wrappedObject
                .getSampleTypeCollection();
            if (children != null) {
                sampleTypeCollection = new ArrayList<SampleTypeWrapper>();
                for (SampleType type : children) {
                    sampleTypeCollection.add(new SampleTypeWrapper(appService,
                        type));
                }
                propertiesMap.put("sampleTypeCollection", sampleTypeCollection);
            }
        }
        if ((sampleTypeCollection != null) && sort)
            Collections.sort(sampleTypeCollection);
        return sampleTypeCollection;
    }

    public List<SampleTypeWrapper> getSampleTypeCollection() {
        return getSampleTypeCollection(false);
    }

    public void setSampleTypeCollection(Collection<SampleType> types,
        boolean setNull) {
        Collection<SampleType> oldTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(types);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldTypes, types);
        if (setNull) {
            propertiesMap.put("sampleTypeCollection", null);
        }
    }

    public void setSampleTypeCollection(List<SampleTypeWrapper> types) {
        Collection<SampleType> typeObjects = new HashSet<SampleType>();
        for (SampleTypeWrapper type : types) {
            type.setSite(wrappedObject);
            typeObjects.add(type.getWrappedObject());
        }
        setSampleTypeCollection(typeObjects, false);
        propertiesMap.put("sampleTypeCollection", types);
    }

    /**
     * Removes the sample type objects that are not contained in the collection.
     * 
     * @param oldCollection
     * @throws Exception
     */
    private void deleteSampleTypeDifference(Site origSite) throws Exception {
        List<SampleTypeWrapper> newSampleType = getSampleTypeCollection();
        List<SampleTypeWrapper> oldSampleSources = new SiteWrapper(appService,
            origSite).getSampleTypeCollection();
        if (oldSampleSources != null) {
            for (SampleTypeWrapper st : oldSampleSources) {
                if ((newSampleType == null) || !newSampleType.contains(st)) {
                    st.delete();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<PvInfoPossibleWrapper> getPvInfoPossibleCollection(
        boolean sort) {
        List<PvInfoPossibleWrapper> PvInfoPossibleCollection = (List<PvInfoPossibleWrapper>) propertiesMap
            .get("PvInfoPossibleCollection");
        if (PvInfoPossibleCollection == null) {
            Collection<PvInfoPossible> children = wrappedObject
                .getPvInfoPossibleCollection();
            if (children != null) {
                PvInfoPossibleCollection = new ArrayList<PvInfoPossibleWrapper>();
                for (PvInfoPossible possible : children) {
                    PvInfoPossibleCollection.add(new PvInfoPossibleWrapper(
                        appService, possible));
                }
                propertiesMap.put("PvInfoPossibleCollection",
                    PvInfoPossibleCollection);
            }
        }
        if ((PvInfoPossibleCollection != null) && sort)
            Collections.sort(PvInfoPossibleCollection);
        return PvInfoPossibleCollection;
    }

    protected List<PvInfoPossibleWrapper> getPvInfoPossibleCollection() {
        return getPvInfoPossibleCollection(false);
    }

    protected void setPvInfoPossibleCollection(
        Collection<PvInfoPossible> collection, boolean setNull) {
        Collection<PvInfoPossible> oldCollection = wrappedObject
            .getPvInfoPossibleCollection();
        wrappedObject.setPvInfoPossibleCollection(collection);
        propertyChangeSupport.firePropertyChange("PvInfoPossibleCollection",
            oldCollection, collection);
        if (setNull) {
            propertiesMap.put("PvInfoPossibleCollection", null);
        }
    }

    protected void setPvInfoPossibleCollection(
        List<PvInfoPossibleWrapper> collection) {
        Collection<PvInfoPossible> pipObjects = new HashSet<PvInfoPossible>();
        for (PvInfoPossibleWrapper pip : collection) {
            pip.setSite(this);
            pipObjects.add(pip.getWrappedObject());
        }
        setPvInfoPossibleCollection(pipObjects, false);
        propertiesMap.put("PvInfoPossibleCollection", collection);
    }

    /**
     * Removes the sample type objects that are not contained in the collection.
     * 
     * @param oldCollection
     * @throws Exception
     */
    private void deletePvInfoPossibleDifference(Site origSite) throws Exception {
        List<PvInfoPossibleWrapper> newPvInfoPossible = getPvInfoPossibleCollection();
        List<PvInfoPossibleWrapper> oldSampleSources = new SiteWrapper(
            appService, origSite).getPvInfoPossibleCollection();
        if (oldSampleSources != null) {
            for (PvInfoPossibleWrapper st : oldSampleSources) {
                if ((newPvInfoPossible == null)
                    || !newPvInfoPossible.contains(st)) {
                    st.delete();
                }
            }
        }
    }

    private Map<String, PvInfoTypeWrapper> getPvInfoTypeMap()
        throws ApplicationException {
        if (pvInfoTypeMap == null) {
            pvInfoTypeMap = new HashMap<String, PvInfoTypeWrapper>();
            for (PvInfoTypeWrapper pit : PvInfoTypeWrapper
                .getAllWrappers(appService)) {
                pvInfoTypeMap.put(pit.getType(), pit);
            }
        }
        return pvInfoTypeMap;
    }

    private Map<String, PvInfoPossibleWrapper> getPvInfoPossibleMap()
        throws ApplicationException {
        if (pvInfoPossibleMap != null)
            return pvInfoPossibleMap;

        pvInfoPossibleMap = new HashMap<String, PvInfoPossibleWrapper>();
        List<PvInfoPossibleWrapper> pipCollection = getPvInfoPossibleCollection();
        if (pipCollection != null) {
            for (PvInfoPossibleWrapper pip : pipCollection) {
                pvInfoPossibleMap.put(pip.getLabel(), pip);
            }
        }

        // get global PIPs now
        for (PvInfoPossibleWrapper pip : PvInfoPossibleWrapper
            .getGlobalPvInfoPossible(appService, false)) {
            pvInfoPossibleMap.put(pip.getLabel(), pip);
        }
        return pvInfoPossibleMap;
    }

    public String[] getPvInfoPossibleLabels() throws ApplicationException {
        getPvInfoPossibleMap();
        return pvInfoPossibleMap.keySet().toArray(new String[] {});
    }

    public String[] getPvInfoTypes() throws ApplicationException {
        getPvInfoTypeMap();
        return pvInfoTypeMap.keySet().toArray(new String[] {});
    }

    public Integer getPvInfoType(String label) {
        PvInfoPossibleWrapper pvInfoPossible = pvInfoPossibleMap.get(label);
        if (pvInfoPossible == null)
            return null;
        return pvInfoPossible.getPvInfoType().getId();
    }

    protected PvInfoPossibleWrapper getPvInfoPossible(String label)
        throws Exception {
        getPvInfoPossibleMap();
        return pvInfoPossibleMap.get(label);
    }

    public void setPvInfoPossible(String label, String type, boolean global)
        throws Exception {
        getPvInfoTypeMap();
        PvInfoTypeWrapper pit = pvInfoTypeMap.get(type);
        if (pit == null) {
            throw new Exception("PvInfoType with type \"" + type
                + "\" is invalid");
        }

        PvInfoPossibleWrapper pip = getPvInfoPossible(label);
        if (pip == null) {
            pip = new PvInfoPossibleWrapper(appService, new PvInfoPossible());
            pip.setLabel(label);
        }
        pip.setSite(global ? null : this);
        pip.setPvInfoType(pit);
    }

    @Override
    protected void persistDependencies(Site origObject)
        throws BiobankCheckException, Exception {
        deleteSampleTypeDifference(origObject);
        deletePvInfoPossibleDifference(origObject);
    }

    @Override
    public int compareTo(ModelWrapper<Site> wrapper) {
        String name1 = wrappedObject.getName();
        String name2 = wrapper.wrappedObject.getName();
        return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
            : -1));
    }

    /**
     * get all site existing
     */
    public static List<SiteWrapper> getAllSites(
        WritableApplicationService appService) throws Exception {
        List<Site> sites = appService.search(Site.class, new Site());
        List<SiteWrapper> wrappers = new ArrayList<SiteWrapper>();
        for (Site s : sites) {
            wrappers.add(new SiteWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     * If "id" is null, then all sites are returned. If not not, then only sites
     * with that id are returned.
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService, Integer id) throws Exception {
        HQLCriteria criteria;

        if (id == null) {
            criteria = new HQLCriteria("from " + Site.class.getName());
        } else {
            criteria = new HQLCriteria("from " + Site.class.getName()
                + " where id = ?", Arrays.asList(new Object[] { id }));
        }

        List<Site> sites = appService.query(criteria);

        List<SiteWrapper> wrappers = new ArrayList<SiteWrapper>();
        for (Site s : sites) {
            wrappers.add(new SiteWrapper(appService, s));
        }
        return wrappers;
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pvInfoPossibleMap = null;
        pvInfoTypeMap = null;
    }

}
