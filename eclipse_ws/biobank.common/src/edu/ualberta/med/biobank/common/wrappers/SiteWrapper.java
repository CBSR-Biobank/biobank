package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.DispatchInfo;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> {

    private AddressWrapper address;

    private ActivityStatusWrapper activityStatus;

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "nameShort", "activityStatus", "comment",
            "address", "clinicCollection", "siteCollection",
            "containerCollection", "shipmentCollection",
            "sitePvAttrCollection", "street1", "street2", "city", "province",
            "postalCode", "toDispatchInfoCollection",
            "fromDispatchInfoCollection" };
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

    private AddressWrapper getAddress() {
        if (address == null) {
            Address a = wrappedObject.getAddress();
            if (a == null)
                return null;
            address = new AddressWrapper(appService, a);
        }
        return address;
    }

    private void setAddress(Address address) {
        if (address == null)
            this.address = null;
        else
            this.address = new AddressWrapper(appService, address);
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
        if (getAddress() == null) {
            return null;
        }
        return address.getStreet1();
    }

    public void setStreet1(String street1) {
        String old = getStreet1();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setStreet1(street1);
        propertyChangeSupport.firePropertyChange("street1", old, street1);
    }

    public String getStreet2() {
        AddressWrapper address = getAddress();
        if (getAddress() == null) {
            return null;
        }
        return address.getStreet2();
    }

    public void setStreet2(String street2) {
        String old = getStreet2();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setStreet2(street2);
        propertyChangeSupport.firePropertyChange("street2", old, street2);
    }

    public String getCity() {
        AddressWrapper address = getAddress();
        if (getAddress() == null) {
            return null;
        }
        return address.getCity();
    }

    public void setCity(String city) {
        String old = getCity();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setCity(city);
        propertyChangeSupport.firePropertyChange("city", old, city);
    }

    public String getProvince() {
        AddressWrapper address = getAddress();
        if (getAddress() == null) {
            return null;
        }
        return address.getProvince();
    }

    public void setProvince(String province) {
        String old = getProvince();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setProvince(province);
        propertyChangeSupport.firePropertyChange("province", old, province);
    }

    public String getPostalCode() {
        AddressWrapper address = getAddress();
        if (getAddress() == null) {
            return null;
        }
        return address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        String old = postalCode;
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setPostalCode(postalCode);
        propertyChangeSupport.firePropertyChange("postalCode", old, postalCode);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getAddress() == null) {
            throw new BiobankCheckException("the site does not have an address");
        }
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the site does not have an activity status");
        }
        checkNotEmpty(getName(), "Name");
        checkNoDuplicates(Site.class, "name", getName(), "A site with name \""
            + getName() + "\" already exists.");
        checkNotEmpty(getNameShort(), "Short Name");
        checkNoDuplicates(Site.class, "nameShort", getNameShort(),
            "A site with short name \"" + getNameShort() + "\" already exists.");
    }

    @Override
    public Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if ((getContainerCollection() != null && getContainerCollection()
            .size() > 0)
            || (getContainerTypeCollection() != null && getContainerTypeCollection()
                .size() > 0)) {
            throw new BiobankCheckException(
                "Unable to delete site "
                    + getName()
                    + ". All defined children (studies, clinics, container types, and containers) must be removed first.");
        }
    }

    public void addStudies(List<StudyWrapper> studies) {
        if ((studies == null) || (studies.size() == 0))
            return;

        Collection<Study> allStudyObjects = new HashSet<Study>();
        List<StudyWrapper> allStudyWrappers = new ArrayList<StudyWrapper>();
        // already added studies
        List<StudyWrapper> currentList = getStudyCollection();
        if (currentList != null) {
            for (StudyWrapper study : currentList) {
                allStudyObjects.add(study.getWrappedObject());
                allStudyWrappers.add(study);
            }
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

    public List<ClinicWrapper> getClinicCollection(boolean sort) {
        return null;
    }

    @Deprecated
    public List<ClinicWrapper> getClinicCollection() {
        return null;
    }

    @Deprecated
    public void addClinics(List<ClinicWrapper> clinics) {
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

    public void addContainerTypes(List<ContainerTypeWrapper> types) {
        if (types != null && types.size() > 0) {
            Collection<ContainerType> allTypeObjects = new HashSet<ContainerType>();
            List<ContainerTypeWrapper> allTypeWrappers = new ArrayList<ContainerTypeWrapper>();
            // already added types
            List<ContainerTypeWrapper> currentList = getContainerTypeCollection();
            if (currentList != null) {
                for (ContainerTypeWrapper type : currentList) {
                    allTypeObjects.add(type.getWrappedObject());
                    allTypeWrappers.add(type);
                }
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

    public void addContainers(List<ContainerWrapper> containers) {
        if (containers != null && containers.size() > 0) {
            Collection<Container> allContainerObjects = new HashSet<Container>();
            List<ContainerWrapper> allContainerWrappers = new ArrayList<ContainerWrapper>();
            // already added containers
            List<ContainerWrapper> currentList = getContainerCollection();
            if (currentList != null) {
                for (ContainerWrapper container : currentList) {
                    allContainerObjects.add(container.getWrappedObject());
                    allContainerWrappers.add(container);
                }
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
    public List<ClinicShipmentWrapper> getShipmentCollection(boolean sort) {
        List<ClinicShipmentWrapper> pvCollection = (List<ClinicShipmentWrapper>) propertiesMap
            .get("shipmentCollection");
        if (pvCollection == null) {
            Collection<ClinicShipment> children = wrappedObject
                .getShipmentCollection();
            if (children != null) {
                pvCollection = new ArrayList<ClinicShipmentWrapper>();
                for (ClinicShipment pv : children) {
                    pvCollection.add(new ClinicShipmentWrapper(appService, pv));
                }
                propertiesMap.put("shipmentCollection", pvCollection);
            }
        }
        if ((pvCollection != null) && sort)
            Collections.sort(pvCollection);
        return pvCollection;
    }

    public List<ClinicShipmentWrapper> getShipmentCollection() {
        return getShipmentCollection(true);
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
     * Search for shipments in the site with the given waybill
     * 
     * @throws BiobankCheckException
     */
    public Long getShipmentCount() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria("select count(*) from "
            + ClinicShipment.class.getName() + " where clinic.site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
    }

    public Long getPatientCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            "select count(distinct patients) from " + Site.class.getName()
                + " as site " + "join site.shipmentCollection as shipments "
                + "join shipments.patientCollection as patients "
                + "where site.id = ?", Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
    }

    public Long getPatientVisitCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria("select count(visits) from "
            + Site.class.getName() + " as site "
            + "join site.shipmentCollection as shipments "
            + "join shipments.patientVisitCollection as visits "
            + "where site.id = ?", Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
    }

    public Long getAliquotCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria("select count(aliquots) from "
            + Site.class.getName() + " as site "
            + "join site.shipmentCollection as shipments "
            + "join shipments.patientVisitCollection as visits "
            + "join visits.aliquotCollection as aliquots where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
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

    /**
     * return true if the user can edit this object
     */
    @Override
    public boolean canEdit() {
        try {
            return ((BiobankApplicationService) appService).canUpdateObject(
                getWrappedClass(), getId());
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void resetInternalFields() {
        activityStatus = null;
        address = null;
    }

    public List<StudyWrapper> getDispatchStudies() throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("select info.study from "
            + DispatchInfo.class.getName()
            + " as info where info.fromSite.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Study> results = appService.query(criteria);
        List<StudyWrapper> wrappers = new ArrayList<StudyWrapper>();
        for (Study res : results) {
            wrappers.add(new StudyWrapper(appService, res));
        }
        return wrappers;
    }

    public List<SiteWrapper> getToSitesDispatchForStudy(StudyWrapper study)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "select info.toSiteCollection from " + DispatchInfo.class.getName()
                + " as info where info.fromSite.id = ? and info.study.id=?",
            Arrays.asList(new Object[] { getId(), study.getId() }));
        List<Site> results = appService.query(criteria);
        List<SiteWrapper> wrappers = new ArrayList<SiteWrapper>();
        for (Site res : results) {
            wrappers.add(new SiteWrapper(appService, res));
        }
        return wrappers;
    }

}
