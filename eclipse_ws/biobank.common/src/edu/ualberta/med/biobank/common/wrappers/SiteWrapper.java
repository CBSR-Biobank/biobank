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
import edu.ualberta.med.biobank.server.CustomApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> {

    private static Map<String, PvAttrTypeWrapper> pvAttrTypeMap;

    private Map<String, SitePvAttrWrapper> sitePvAttrMap;

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
    public void persist() throws Exception {
        boolean newSite = isNew();
        super.persist();
        if (newSite) {
            ((CustomApplicationService) appService).newSite(getId(), getName());
        }
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
        if (!isNew()) {
            Site origSite = new Site();
            origSite.setId(getId());
            origSite = (Site) appService.search(Site.class, origSite).get(0);
            checkNoStudyRemoved(origSite);
            checkNoClinicRemoved(origSite);
            checkNoContainerTypeRemoved(origSite);
            checkNoContainerRemoved(origSite);
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

    private void checkNoStudyRemoved(Site origSite)
        throws BiobankCheckException, ApplicationException {
        if (!isNew()) {
            List<StudyWrapper> newStudies = getStudyCollection();
            List<StudyWrapper> oldStudies = new SiteWrapper(appService,
                origSite).getStudyCollection();
            if (newStudies != null) {
                for (StudyWrapper s : oldStudies) {
                    if ((newStudies == null) || !newStudies.contains(s)) {
                        Study dbStudy = new Study();
                        dbStudy.setId(s.getId());
                        // check if still in database
                        if (appService.search(Study.class, dbStudy).size() == 1) {
                            throw new BiobankCheckException(
                                "Study "
                                    + s.getName()
                                    + " has been remove from the studies list: this study should be deleted first.");
                        }
                    }
                }
            }
        }
    }

    private void checkNoClinicRemoved(Site origSite)
        throws BiobankCheckException, ApplicationException {
        if (!isNew()) {
            List<ClinicWrapper> newClinics = getClinicCollection();
            List<ClinicWrapper> oldClinics = new SiteWrapper(appService,
                origSite).getClinicCollection();
            if (newClinics != null) {
                for (ClinicWrapper c : oldClinics) {
                    if ((newClinics == null) || !newClinics.contains(c)) {
                        Clinic dbClinic = new Clinic();
                        dbClinic.setId(c.getId());
                        // check if still in database
                        if (appService.search(Clinic.class, dbClinic).size() == 1) {
                            throw new BiobankCheckException(
                                "Clinic "
                                    + c.getName()
                                    + " has been remove from the clinics list: this clinic should be deleted first.");
                        }
                    }
                }
            }
        }
    }

    private void checkNoContainerTypeRemoved(Site origSite)
        throws BiobankCheckException, ApplicationException {
        if (!isNew()) {
            List<ContainerTypeWrapper> newContainerTypes = getContainerTypeCollection();
            List<ContainerTypeWrapper> oldContainerTypes = new SiteWrapper(
                appService, origSite).getContainerTypeCollection();
            if (newContainerTypes != null) {
                for (ContainerTypeWrapper c : oldContainerTypes) {
                    if ((newContainerTypes == null)
                        || !newContainerTypes.contains(c)) {
                        ContainerType dbContainerType = new ContainerType();
                        dbContainerType.setId(c.getId());
                        // check if still in database
                        if (appService.search(ContainerType.class,
                            dbContainerType).size() == 1) {
                            throw new BiobankCheckException(
                                "ContainerType "
                                    + c.getName()
                                    + " has been remove from the container types list: this type should be deleted first.");
                        }
                    }
                }
            }
        }
    }

    private void checkNoContainerRemoved(Site origSite)
        throws BiobankCheckException, ApplicationException {
        if (!isNew()) {
            List<ContainerWrapper> newContainers = getContainerCollection();
            List<ContainerWrapper> oldContainers = new SiteWrapper(appService,
                origSite).getContainerCollection();
            if (newContainers != null) {
                for (ContainerWrapper c : oldContainers) {
                    if ((newContainers == null) || !newContainers.contains(c)) {
                        Container dbContainer = new Container();
                        dbContainer.setId(c.getId());
                        // check if still in database
                        if (appService.search(Container.class, dbContainer)
                            .size() == 1) {
                            throw new BiobankCheckException(
                                "Container "
                                    + c.getFullInfoLabel()
                                    + " has been remove from the containers list: this container should be deleted first.");
                        }
                    }
                }
            }
        }
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
     * @throws BiobankCheckException
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

    protected void setSitePvAttrCollection(Collection<SitePvAttr> collection,
        boolean setNull) {
        Collection<SitePvAttr> oldCollection = wrappedObject
            .getSitePvAttrCollection();
        wrappedObject.setSitePvAttrCollection(collection);
        propertyChangeSupport.firePropertyChange("SitePvAttrCollection",
            oldCollection, collection);
        if (setNull) {
            propertiesMap.put("SitePvAttrCollection", null);
        }
    }

    protected void setSitePvAttrCollection(List<SitePvAttrWrapper> collection) {
        Collection<SitePvAttr> pipObjects = new HashSet<SitePvAttr>();
        for (SitePvAttrWrapper pip : collection) {
            pip.setSite(this);
            pipObjects.add(pip.getWrappedObject());
        }
        setSitePvAttrCollection(pipObjects, false);
        propertiesMap.put("SitePvAttrCollection", collection);
    }

    /**
     * Removes the sample type objects that are not contained in the collection.
     * 
     * @param oldCollection
     * @throws BiobankCheckException
     * @throws Exception
     */
    private void deleteSitePvAttrDifference(Site origSite) throws Exception {
        List<SitePvAttrWrapper> oldSitePvAttr = new SiteWrapper(appService,
            origSite).getSitePvAttrCollection();
        if (oldSitePvAttr == null)
            return;
        getSitePvAttrMap();
        int newSitePvAttrCount = sitePvAttrMap.size();
        for (SitePvAttrWrapper pip : oldSitePvAttr) {
            if ((pip.getSite() != null)
                && ((newSitePvAttrCount == 0) || (sitePvAttrMap.get(pip
                    .getLabel()) == null))) {
                pip.delete();
            }
        }
    }

    protected static Map<String, PvAttrTypeWrapper> getPvAttrTypeMap(
        WritableApplicationService appService) throws ApplicationException {
        if (pvAttrTypeMap == null) {
            pvAttrTypeMap = new HashMap<String, PvAttrTypeWrapper>();
            for (PvAttrTypeWrapper pit : PvAttrTypeWrapper
                .getAllWrappers(appService)) {
                pvAttrTypeMap.put(pit.getName(), pit);
            }
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
        getSitePvAttr(label);
        sitePvAttrMap.remove(label);
        setSitePvAttrCollection(new ArrayList<SitePvAttrWrapper>(sitePvAttrMap
            .values()));
    }

    @Override
    protected void persistDependencies(Site origObject) throws Exception {
        if (sitePvAttrMap != null) {
            setSitePvAttrCollection(new ArrayList<SitePvAttrWrapper>(
                sitePvAttrMap.values()));
        }
        if (origObject != null) {
            deleteSampleTypeDifference(origObject);
            deleteSitePvAttrDifference(origObject);
        }
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
    public void reload() throws Exception {
        super.reload();
        sitePvAttrMap = null;
        pvAttrTypeMap = null;
    }

    @Override
    public String toString() {
        return getName();
    }

}
