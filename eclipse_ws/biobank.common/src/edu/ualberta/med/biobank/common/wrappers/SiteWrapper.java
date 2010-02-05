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
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SitePvAttrWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SitePvAttr;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> {

    private static Map<String, PvAttrTypeWrapper> pvAttrTypeMap;

    private Map<String, SitePvAttrWrapper> sitePvAttrMap;

    private List<SampleTypeWrapper> deletedSampleTypes = new ArrayList<SampleTypeWrapper>();

    private List<SitePvAttrWrapper> deletedSitePvAttr = new ArrayList<SitePvAttrWrapper>();

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
        sitePvAttrMap = null;
        pvAttrTypeMap = null;
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
        sitePvAttrMap = null;
        pvAttrTypeMap = null;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "activityStatus", "comment", "address",
            "clinicCollection", "siteCollection", "containerCollection",
            "sampleTypeCollection", "sitePvAttrCollection", "street1",
            "street2", "city", "province", "postalCode",
            "allSampleTypeCollection" };
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
        String old = getStreet1();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet1(street1);
        propertyChangeSupport.firePropertyChange("street1", old, street1);
    }

    public String getStreet2() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getStreet2();
    }

    public void setStreet2(String street2) {
        String old = getStreet2();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet2(street2);
        propertyChangeSupport.firePropertyChange("street2", old, street2);
    }

    public String getCity() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getCity();
    }

    public void setCity(String city) {
        String old = getCity();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setCity(city);
        propertyChangeSupport.firePropertyChange("city", old, city);
    }

    public String getProvince() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getProvince();
    }

    public void setProvince(String province) {
        String old = getProvince();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setProvince(province);
        propertyChangeSupport.firePropertyChange("province", old, province);
    }

    public String getPostalCode() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        String old = postalCode;
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setPostalCode(postalCode);
        propertyChangeSupport.firePropertyChange("postalCode", old, postalCode);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
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
        if (isNew()) {
            c = new HQLCriteria("from " + Site.class.getName()
                + " where name = ?", Arrays.asList(new Object[] { getName() }));
        } else {
            c = new HQLCriteria("from " + Site.class.getName()
                + " where id <> ? and name = ?", Arrays.asList(new Object[] {
                getId(), getName() }));
        }

        List<Object> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    public Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
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
        List<StudyWrapper> studyCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");
        if (studyCollection == null) {
            Collection<Study> children = wrappedObject.getStudyCollection();
            if (children != null) {
                studyCollection = new ArrayList<StudyWrapper>();
                for (Study study : children) {
                    studyCollection.add(new StudyWrapper(appService, study));
                }
                propertiesMap.put("studyCollection", studyCollection);
            }
        }
        if ((studyCollection != null) && sort)
            Collections.sort(studyCollection);
        return studyCollection;
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(true);
    }

    public void addStudies(StudyWrapper... studies) {
        Collection<Study> allStudyObjects = new HashSet<Study>();
        List<StudyWrapper> allStudyWrappers = new ArrayList<StudyWrapper>();
        // already added studies
        for (StudyWrapper study : getStudyCollection()) {
            allStudyObjects.add(study.getWrappedObject());
            allStudyWrappers.add(study);
        }
        // new studies added
        for (StudyWrapper study : studies) {
            allStudyObjects.add(study.getWrappedObject());
            allStudyWrappers.add(study);
        }
        Collection<Study> oldStudies = wrappedObject.getStudyCollection();
        wrappedObject.setStudyCollection(allStudyObjects);
        propertyChangeSupport.firePropertyChange("studyCollection", oldStudies,
            allStudyObjects);
        propertiesMap.put("studyCollection", allStudyWrappers);
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
        return getClinicCollection(true);
    }

    public void addClinics(ClinicWrapper... clinics) {
        Collection<Clinic> allClinicObjects = new HashSet<Clinic>();
        List<ClinicWrapper> allClinicWrappers = new ArrayList<ClinicWrapper>();
        // already added clinics
        for (ClinicWrapper clinic : getClinicCollection()) {
            allClinicObjects.add(clinic.getWrappedObject());
            allClinicWrappers.add(clinic);
        }
        // new clinics
        for (ClinicWrapper clinic : clinics) {
            allClinicObjects.add(clinic.getWrappedObject());
            allClinicWrappers.add(clinic);
        }
        Collection<Clinic> oldClinics = wrappedObject.getClinicCollection();
        wrappedObject.setClinicCollection(allClinicObjects);
        propertyChangeSupport.firePropertyChange("clinicCollection",
            oldClinics, allClinicObjects);
        propertiesMap.put("clinicCollection", allClinicWrappers);
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

    public void addContainerTypes(ContainerTypeWrapper... types) {
        Collection<ContainerType> allTypeObjects = new HashSet<ContainerType>();
        List<ContainerTypeWrapper> allTypeWrappers = new ArrayList<ContainerTypeWrapper>();
        // already added types
        for (ContainerTypeWrapper type : getContainerTypeCollection()) {
            allTypeObjects.add(type.getWrappedObject());
            allTypeWrappers.add(type);
        }
        // new types
        for (ContainerTypeWrapper type : types) {
            allTypeObjects.add(type.getWrappedObject());
            allTypeWrappers.add(type);
        }
        Collection<ContainerType> oldTypes = wrappedObject
            .getContainerTypeCollection();
        wrappedObject.setContainerTypeCollection(allTypeObjects);
        propertyChangeSupport.firePropertyChange("containerTypeCollection",
            oldTypes, allTypeObjects);
        propertiesMap.put("containerTypeCollection", allTypeWrappers);
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

    public void addContainers(ContainerWrapper... containers) {
        Collection<Container> allContainerObjects = new HashSet<Container>();
        List<ContainerWrapper> allContainerWrappers = new ArrayList<ContainerWrapper>();
        // already added containers
        for (ContainerWrapper container : getContainerCollection()) {
            allContainerObjects.add(container.getWrappedObject());
            allContainerWrappers.add(container);
        }
        // new containers
        for (ContainerWrapper container : containers) {
            allContainerObjects.add(container.getWrappedObject());
            allContainerWrappers.add(container);
        }
        Collection<Container> oldContainers = wrappedObject
            .getContainerCollection();
        wrappedObject.setContainerCollection(allContainerObjects);
        propertyChangeSupport.firePropertyChange("containerCollection",
            oldContainers, allContainerObjects);
        propertiesMap.put("containerCollection", allContainerWrappers);
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

    /**
     * Include globals sample types
     * 
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    public List<SampleTypeWrapper> getAllSampleTypeCollection(boolean sort)
        throws ApplicationException {
        List<SampleTypeWrapper> sampleTypeCollection = (List<SampleTypeWrapper>) propertiesMap
            .get("allSampleTypeCollection");
        if (sampleTypeCollection == null) {
            sampleTypeCollection = new ArrayList<SampleTypeWrapper>();
            List<SampleTypeWrapper> siteSampleTypes = getSampleTypeCollection();
            if (siteSampleTypes != null) {
                sampleTypeCollection.addAll(siteSampleTypes);
            }
            List<SampleTypeWrapper> globalSampleTypes = SampleTypeWrapper
                .getGlobalSampleTypes(appService, false);
            if (globalSampleTypes != null) {
                sampleTypeCollection.addAll(globalSampleTypes);
            }
            propertiesMap.put("allSampleTypeCollection", sampleTypeCollection);
        }
        if ((sampleTypeCollection != null) && sort)
            Collections.sort(sampleTypeCollection);
        return sampleTypeCollection;
    }

    public List<SampleTypeWrapper> getAllSampleTypeCollection()
        throws ApplicationException {
        return getAllSampleTypeCollection(false);
    }

    public void addSampleTypes(List<SampleTypeWrapper> types) {
        Collection<SampleType> allTypeObjects = new HashSet<SampleType>();
        Collection<SampleTypeWrapper> allTypeWrappers = new ArrayList<SampleTypeWrapper>();
        // already added types
        for (SampleTypeWrapper type : getSampleTypeCollection()) {
            allTypeObjects.add(type.getWrappedObject());
            allTypeWrappers.add(type);
        }
        // new types
        for (SampleTypeWrapper type : types) {
            type.setSite(wrappedObject);
            allTypeObjects.add(type.getWrappedObject());
            allTypeWrappers.add(type);
        }
        Collection<SampleType> oldTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(allTypeObjects);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldTypes, allTypeObjects);
        propertiesMap.put("sampleTypeCollection", allTypeWrappers);
    }

    public void removeSampleTypes(List<SampleTypeWrapper> types) {
        deletedSampleTypes.addAll(types);
        Collection<SampleType> allTypeObjects = new HashSet<SampleType>();
        Collection<SampleTypeWrapper> allTypeWrappers = new ArrayList<SampleTypeWrapper>();
        // already added types
        for (SampleTypeWrapper type : getSampleTypeCollection()) {
            if (!deletedSampleTypes.contains(type)) {
                allTypeObjects.add(type.getWrappedObject());
                allTypeWrappers.add(type);
            }
        }
        Collection<SampleType> oldTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(allTypeObjects);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldTypes, allTypeObjects);
        propertiesMap.put("sampleTypeCollection", allTypeWrappers);
    }

    @SuppressWarnings("unchecked")
    protected List<SitePvAttrWrapper> getSitePvAttrCollection() {
        List<SitePvAttrWrapper> sitePvAttrCollection = (List<SitePvAttrWrapper>) propertiesMap
            .get("SitePvAttrCollection");
        if (sitePvAttrCollection == null) {
            Collection<SitePvAttr> possibleCollection = wrappedObject
                .getSitePvAttrCollection();
            if (possibleCollection != null) {
                sitePvAttrCollection = new ArrayList<SitePvAttrWrapper>();
                for (SitePvAttr possible : possibleCollection) {
                    sitePvAttrCollection.add(new SitePvAttrWrapper(appService,
                        possible));
                }
                propertiesMap.put("SitePvAttrCollection", sitePvAttrCollection);
            }
        }
        return sitePvAttrCollection;
    }

    protected static Map<String, PvAttrTypeWrapper> getPvAttrTypeMap(
        WritableApplicationService appService) throws ApplicationException {
        if (pvAttrTypeMap == null) {
            pvAttrTypeMap = PvAttrTypeWrapper.getAllPvAttrTypesMap(appService);
        }
        return pvAttrTypeMap;
    }

    private Map<String, SitePvAttrWrapper> getSitePvAttrMap() {
        if (sitePvAttrMap != null)
            return sitePvAttrMap;

        sitePvAttrMap = new HashMap<String, SitePvAttrWrapper>();
        List<SitePvAttrWrapper> sitePvAttrCollection = getSitePvAttrCollection();
        if (sitePvAttrCollection != null) {
            for (SitePvAttrWrapper pip : sitePvAttrCollection) {
                sitePvAttrMap.put(pip.getLabel(), pip);
            }
        }
        return sitePvAttrMap;
    }

    public static List<String> getPvAttrTypeNames(
        WritableApplicationService appService) throws ApplicationException {
        getPvAttrTypeMap(appService);
        return new ArrayList<String>(pvAttrTypeMap.keySet());
    }

    protected SitePvAttrWrapper getSitePvAttr(String label) throws Exception {
        getSitePvAttrMap();
        SitePvAttrWrapper sitePvAttr = sitePvAttrMap.get(label);
        if (sitePvAttr == null) {
            throw new Exception("SitePvAttr with label \"" + label
                + "\" is invalid");
        }
        return sitePvAttr;
    }

    public String[] getSitePvAttrLabels() {
        getSitePvAttrMap();
        return sitePvAttrMap.keySet().toArray(new String[] {});
    }

    public String getSitePvAttrTypeName(String label) throws Exception {
        return getSitePvAttr(label).getPvAttrType().getName();
    }

    public Integer getSitePvAttrType(String label) throws Exception {
        return getSitePvAttr(label).getPvAttrType().getId();
    }

    /**
     * Saves a possible patient visit attribute that is global to this site.
     * 
     * @param label The label to be used for the patient visit information item.
     * @param type The patient visit information item's type (See database table
     *            PV_INFO_POSSIBLE).
     * @throws Exception
     */
    public void setSitePvAttr(String label, String type) throws Exception {
        getPvAttrTypeMap(appService);
        getSitePvAttrMap();
        PvAttrTypeWrapper pvAttrType = pvAttrTypeMap.get(type);
        if (pvAttrType == null) {
            throw new Exception("PvAttrType with type \"" + type
                + "\" is invalid");
        }

        SitePvAttrWrapper sitePvAttr = sitePvAttrMap.get(label);
        if (sitePvAttr == null) {
            sitePvAttr = new SitePvAttrWrapper(appService, new SitePvAttr());
            sitePvAttr.setLabel(label);
            sitePvAttr.setSite(this);
            sitePvAttrMap.put(label, sitePvAttr);
        }
        sitePvAttr.setPvAttrType(pvAttrType);
    }

    public void deleteSitePvAttr(String label) throws Exception {
        getSitePvAttrMap();
        // this call generates exception if label does not exist
        SitePvAttrWrapper sitePvAttr = getSitePvAttr(label);
        sitePvAttrMap.remove(label);
        deletedSitePvAttr.add(sitePvAttr);
    }

    @Override
    protected void persistDependencies(Site origObject) throws Exception {
        persistSampleTypes();
        persistSitePvAttr();
    }

    private void persistSitePvAttr() throws Exception {
        if (sitePvAttrMap != null) {
            Collection<SitePvAttr> sitePvAttrObjects = new HashSet<SitePvAttr>();
            Collection<SitePvAttrWrapper> sitePvAttrWrapperList = sitePvAttrMap
                .values();
            for (SitePvAttrWrapper spa : sitePvAttrWrapperList) {
                spa.setSite(this);
                sitePvAttrObjects.add(spa.getWrappedObject());
            }
            Collection<SitePvAttr> oldCollection = wrappedObject
                .getSitePvAttrCollection();
            wrappedObject.setSitePvAttrCollection(sitePvAttrObjects);
            propertyChangeSupport.firePropertyChange("SitePvAttrCollection",
                oldCollection, sitePvAttrObjects);
            propertiesMap.put("SitePvAttrCollection", sitePvAttrWrapperList);
        }
        for (SitePvAttrWrapper sitePvAttr : deletedSitePvAttr) {
            if (!sitePvAttr.isNew()) {
                sitePvAttr.delete();
            }
        }
        deletedSitePvAttr.clear();
    }

    private void persistSampleTypes() throws Exception {
        for (SampleTypeWrapper st : deletedSampleTypes) {
            if (!st.isNew()) {
                st.delete();
            }
        }
        deletedSampleTypes.clear();
    }

    @Override
    public int compareTo(ModelWrapper<Site> wrapper) {
        if (wrapper instanceof SiteWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
                : -1));
        }
        return 0;
    }

    /**
     * get all site existing
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService) throws Exception {
        return getSites(appService, null);
    }

    /**
     * If "id" is null, then all sites are returned. If not, then only sites
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
    public String toString() {
        return getName();
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        sitePvAttrMap = null;
        pvAttrTypeMap = null;
        deletedSampleTypes.clear();
        deletedSitePvAttr.clear();
    }

}
