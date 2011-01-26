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

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.DispatchInfoWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchInfo;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> {
    public static final Property<Collection<Study>> STUDY_COLLECTION = Property
        .create("studyCollection", new TypeReference<Collection<Study>>() {
        });

    public Collection<StudyWrapper> getStudies(boolean sort) {
        // TODO: Q: pass StudyWrapper.class?
        return getWrappedCollection(STUDY_COLLECTION, sort);
    }

    public void setStudies(Collection<StudyWrapper> studies) {
        setWrappedCollection(STUDY_COLLECTION, studies);
    }

    private AddressWrapper address;

    private List<DispatchInfoWrapper> removedDispatchInfoWrapper = new ArrayList<DispatchInfoWrapper>();

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
            "postalCode", "sentDispatchCollection", "sentDispatchCollection",
            "notificationCollection", "srcDispatchInfoCollection",
            "studyCollection", "approvedRequestCollection",
            "acceptedRequestCollection", "filledRequestCollection",
            "shippedRequestCollection" };
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
        ActivityStatusWrapper activityStatus = (ActivityStatusWrapper) propertiesMap
            .get("activityStatus");
        if (activityStatus == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activityStatus = new ActivityStatusWrapper(appService, a);
            propertiesMap.put("activityStatus", activityStatus);
        }
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        propertiesMap.put("activityStatus", activityStatus);
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
    protected void persistDependencies(Site origObject) throws Exception {
        for (DispatchInfoWrapper diw : removedDispatchInfoWrapper) {
            if (!diw.isNew()) {
                diw.delete();
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
        if ((getContainerCollection() != null && getContainerCollection()
            .size() > 0)
            || (getContainerTypeCollection() != null && getContainerTypeCollection()
                .size() > 0)
            || (getShipmentCollection() != null && getShipmentCollection()
                .size() > 0)) {
            throw new BiobankCheckException(
                "Unable to delete site "
                    + getName()
                    + ". All defined children (shipments, container types, and containers) must be removed first.");
        }
    }

    @SuppressWarnings("unchecked")
    public List<RequestWrapper> getApprovedRequestCollection() {
        List<RequestWrapper> requestCollection = (List<RequestWrapper>) propertiesMap
            .get("approvedRequestCollection");
        if (requestCollection == null) {
            Collection<Request> children = wrappedObject.getRequestCollection();
            if (children != null) {
                requestCollection = new ArrayList<RequestWrapper>();
                for (Request request : children) {
                    if (request.getState()
                        .equals(RequestState.APPROVED.getId()))
                        requestCollection.add(new RequestWrapper(appService,
                            request));
                }
                propertiesMap.put("approvedRequestCollection",
                    requestCollection);
            }
        }
        return requestCollection;

    }

    @SuppressWarnings("unchecked")
    public List<RequestWrapper> getAcceptedRequestCollection() {
        List<RequestWrapper> requestCollection = (List<RequestWrapper>) propertiesMap
            .get("acceptedRequestCollection");
        if (requestCollection == null) {
            Collection<Request> children = wrappedObject.getRequestCollection();
            if (children != null) {
                requestCollection = new ArrayList<RequestWrapper>();
                for (Request request : children) {
                    if (request.getState()
                        .equals(RequestState.ACCEPTED.getId()))
                        requestCollection.add(new RequestWrapper(appService,
                            request));
                }
                propertiesMap.put("acceptedRequestCollection",
                    requestCollection);
            }
        }
        return requestCollection;

    }

    @SuppressWarnings("unchecked")
    public List<RequestWrapper> getFilledRequestCollection() {
        List<RequestWrapper> requestCollection = (List<RequestWrapper>) propertiesMap
            .get("filledRequestCollection");
        if (requestCollection == null) {
            Collection<Request> children = wrappedObject.getRequestCollection();
            if (children != null) {
                requestCollection = new ArrayList<RequestWrapper>();
                for (Request request : children) {
                    if (request.getState().equals(RequestState.FILLED.getId()))
                        requestCollection.add(new RequestWrapper(appService,
                            request));
                }
                propertiesMap.put("filledRequestCollection", requestCollection);
            }
        }
        return requestCollection;

    }

    @SuppressWarnings("unchecked")
    public List<RequestWrapper> getShippedRequestCollection() {
        List<RequestWrapper> requestCollection = (List<RequestWrapper>) propertiesMap
            .get("shippedRequestCollection");
        if (requestCollection == null) {
            Collection<Request> children = wrappedObject.getRequestCollection();
            if (children != null) {
                requestCollection = new ArrayList<RequestWrapper>();
                for (Request request : children) {
                    if (request.getState().equals(RequestState.SHIPPED.getId()))
                        requestCollection.add(new RequestWrapper(appService,
                            request));
                }
                propertiesMap
                    .put("shippedRequestCollection", requestCollection);
            }
        }
        return requestCollection;

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

    public List<StudyWrapper> getStudiesNotAssoc() throws ApplicationException {
        List<StudyWrapper> studyWrappers = new ArrayList<StudyWrapper>();
        HQLCriteria c = new HQLCriteria("from " + Study.class.getName()
            + " s where " + getId() + " not in elements(s.siteCollection)");
        List<Study> results = appService.query(c);
        for (Study res : results) {
            studyWrappers.add(new StudyWrapper(appService, res));
        }
        return studyWrappers;
    }

    private void setStudyCollection(Collection<Study> allStudyObjects,
        List<StudyWrapper> allStudyWrappers) {
        Collection<Study> oldStudys = wrappedObject.getStudyCollection();
        wrappedObject.setStudyCollection(allStudyObjects);
        propertyChangeSupport.firePropertyChange("contactCollection",
            oldStudys, allStudyObjects);
        propertiesMap.put("studyCollection", allStudyWrappers);
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
        setStudyCollection(allStudyObjects, allStudyWrappers);
    }

    public void removeStudies(List<StudyWrapper> studiesToRemove)
        throws BiobankCheckException {
        if ((studiesToRemove == null) || (studiesToRemove.size() == 0))
            return;

        List<StudyWrapper> currentList = getStudyCollection();
        if (!currentList.containsAll(studiesToRemove)) {
            throw new BiobankCheckException(
                "studies are not associated with site " + getNameShort());
        }

        Collection<Study> allStudyObjects = new HashSet<Study>();
        List<StudyWrapper> allStudyWrappers = new ArrayList<StudyWrapper>();
        // already added studies
        if (currentList != null) {
            for (StudyWrapper study : currentList) {
                if (!studiesToRemove.contains(study)) {
                    allStudyObjects.add(study.getWrappedObject());
                    allStudyWrappers.add(study);
                }
            }
        }
        setStudyCollection(allStudyObjects, allStudyWrappers);
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
    public List<ShipmentWrapper> getShipmentCollection(boolean sort) {
        List<ShipmentWrapper> pvCollection = (List<ShipmentWrapper>) propertiesMap
            .get("shipmentCollection");
        if (pvCollection == null) {
            Collection<Shipment> children = wrappedObject
                .getShipmentCollection();
            if (children != null) {
                pvCollection = new ArrayList<ShipmentWrapper>();
                for (Shipment pv : children) {
                    pvCollection.add(new ShipmentWrapper(appService, pv));
                }
                propertiesMap.put("shipmentCollection", pvCollection);
            }
        }
        if ((pvCollection != null) && sort)
            Collections.sort(pvCollection);
        return pvCollection;
    }

    public List<ShipmentWrapper> getShipmentCollection() {
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
            + Shipment.class.getName() + " where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
    }

    public Long getPatientCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            "select count(distinct patient) from " + Site.class.getName()
                + " as site " + "join site.shipmentCollection as shipments "
                + "join shipments.shipmentPatientCollection as csps "
                + "join csps.patient as patient " + "where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
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
            + "join shipments.shipmentPatientCollection as csps "
            + "join csps.patientVisitCollection as visits "
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
            + "join shipments.shipmentPatientCollection as csps "
            + "join csps.patientVisitCollection as visits "
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

    @Override
    public void resetInternalFields() {
        address = null;
        removedDispatchInfoWrapper.clear();
    }

    public List<StudyWrapper> getDispatchStudiesAsSender() {
        Map<Integer, DispatchInfoWrapper> srcMap = getSrcDispatchInfoCollection();
        if (srcMap == null)
            return null;
        List<StudyWrapper> wrappers = new ArrayList<StudyWrapper>();
        for (DispatchInfoWrapper diw : srcMap.values()) {
            wrappers.add(diw.getStudy());
        }
        return wrappers;
    }

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getDispatchStudiesAsReceiver() {
        List<StudyWrapper> studies = (List<StudyWrapper>) propertiesMap
            .get("dispatchStudiesAsReceiver");
        if (studies == null) {
            Collection<DispatchInfo> children = wrappedObject
                .getDestDispatchInfoCollection();
            if (children != null) {
                studies = new ArrayList<StudyWrapper>();
                for (DispatchInfo di : children) {
                    studies.add(new StudyWrapper(appService, di.getStudy()));
                }
                propertiesMap.put("dispatchStudiesAsReceiver", studies);
            }
        }
        return studies;
    }

    public List<SiteWrapper> getStudyDispachSites(StudyWrapper study)
        throws WrapperException {
        if (study == null) {
            throw new WrapperException("study is null");
        }
        Map<Integer, DispatchInfoWrapper> srcMap = getSrcDispatchInfoCollection();
        if (srcMap == null)
            return null;
        DispatchInfoWrapper info = srcMap.get(study.getId());
        if (info == null)
            return null;
        return info.getDestSiteCollection();
    }

    public void addStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> sites) throws BiobankCheckException {
        if ((sites == null) || (sites.size() == 0))
            return;
        Map<Integer, DispatchInfoWrapper> infos = getSrcDispatchInfoCollection();
        DispatchInfoWrapper diw = null;
        if (infos != null) {
            diw = infos.get(study.getId());
        }
        if (diw == null) {
            List<StudyWrapper> studies = getStudyCollection();
            if (studies == null || !studies.contains(study)) {
                throw new BiobankCheckException("Site " + getNameShort()
                    + " cannot dispatch aliquots from study "
                    + study.getNameShort()
                    + ": this study is not part of its current studies list.");
            }
            diw = new DispatchInfoWrapper(appService);
            diw.setStudy(study);
            diw.setSrcSite(this);
            if (infos != null) {
                infos.put(study.getId(), diw);
            }
            Collection<DispatchInfo> allsInfoObjects = wrappedObject
                .getSrcDispatchInfoCollection();
            if (allsInfoObjects == null) {
                allsInfoObjects = new HashSet<DispatchInfo>();
            } else {
                allsInfoObjects = new HashSet<DispatchInfo>(allsInfoObjects);
            }
            allsInfoObjects.add(diw.wrappedObject);
            wrappedObject.setSrcDispatchInfoCollection(allsInfoObjects);
        }
        diw.addDestSites(sites);
    }

    public void removeStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> sites) {
        if ((sites == null) || (sites.size() == 0))
            return;
        Map<Integer, DispatchInfoWrapper> infos = getSrcDispatchInfoCollection();
        if (infos != null) {
            DispatchInfoWrapper diw = infos.get(study.getId());
            if (diw != null) {
                diw.removeDestSites(sites);
                if (diw.getDestSiteCollection().size() == 0) {
                    infos.remove(study.getId());
                    removedDispatchInfoWrapper.add(diw);
                    Collection<DispatchInfo> diList = wrappedObject
                        .getSrcDispatchInfoCollection();
                    DispatchInfo diToRemove = null;
                    for (DispatchInfo di : diList) {
                        if (di.getId().equals(diw.getId())) {
                            diToRemove = di;
                            break;
                        }
                    }
                    diList.remove(diToRemove);
                    wrappedObject.setSrcDispatchInfoCollection(diList);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivedDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("receivedDispatchCollection");
        if (shipCollection == null) {
            Collection<Dispatch> children = wrappedObject
                .getReceivedDispatchCollection();

            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (Dispatch ship : children) {
                    shipCollection.add(new DispatchWrapper(appService, ship));
                }
                propertiesMap.put("receivedDispatchCollection", shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getSentDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("sentDispatchCollection");
        if (shipCollection == null) {
            Collection<Dispatch> children = wrappedObject
                .getSentDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (Dispatch ship : children) {
                    shipCollection.add(new DispatchWrapper(appService, ship));
                }
                propertiesMap.put("sentDispatchCollection", shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitSentDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inTransitSentDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSentDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper ship : children) {
                    if (ship.isInTransitState()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap.put("inTransitSentDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitReceiveDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inTransitReceiveDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper ship : children) {
                    if (ship.isInTransitState()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap.put("inTransitReceiveDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingNoErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("receivingDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper ship : children) {
                    if (ship.isInReceivedState() && !ship.hasErrors()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap
                    .put("receivingDispatchCollection", shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingWithErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("receivingWithErrorsDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper ship : children) {
                    if (ship.isInReceivedState() && ship.hasErrors()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap.put("receivingWithErrorsDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInCreationDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inCreationDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSentDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper ship : children) {
                    if (ship.isInCreationState()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap.put("inCreationDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    /**
     * For one study, this site has one source dispatch info associated.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<Integer, DispatchInfoWrapper> getSrcDispatchInfoCollection() {
        Map<Integer, DispatchInfoWrapper> infos = (Map<Integer, DispatchInfoWrapper>) propertiesMap
            .get("srcDispatchInfoCollection");
        if (infos == null) {
            Collection<DispatchInfo> children = wrappedObject
                .getSrcDispatchInfoCollection();
            if (children != null) {
                infos = new HashMap<Integer, DispatchInfoWrapper>();
                for (DispatchInfo di : children) {
                    Integer studyId = di.getStudy().getId();
                    infos.put(studyId, new DispatchInfoWrapper(appService, di));
                }
                propertiesMap.put("srcDispatchInfoCollection", infos);
            }
        }
        return infos;
    }

    public Set<ClinicWrapper> getWorkingClinicCollection() {
        List<StudyWrapper> studies = getStudyCollection();
        Set<ClinicWrapper> clinics = new HashSet<ClinicWrapper>();
        for (StudyWrapper study : studies) {
            clinics.addAll(study.getClinicCollection());
        }
        return clinics;
    }

    /**
     * Use an HQL query to quickly get the size of the collection.
     * 
     * @return The number of clinics associated to this repository stie.
     * @throws ApplicationException
     */
    public int getWorkingClinicCollectionSize() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct contact.clinic "
            + "from edu.ualberta.med.biobank.model.Site as site "
            + "inner join site.studyCollection study "
            + "inner join study.contactCollection contact "
            + "where site.id = ?", Arrays.asList(new Object[] { getId() }));
        List<Clinic> clinics = appService.query(c);
        return clinics.size();
    }

    /**
     * Only webadministrator can update the site object itself. To check if a
     * user can modify data inside a site, use user.canUpdateSite method
     */
    @Override
    public boolean canUpdate(User user) {
        return user.isWebsiteAdministrator();
    }

    public static Collection<? extends ModelWrapper<?>> getInTransitReceiveDispatchCollection(
        SiteWrapper site) {
        return site.getInTransitReceiveDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getReceivingNoErrorsDispatchCollection(
        SiteWrapper site) {
        return site.getReceivingNoErrorsDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getInCreationDispatchCollection(
        SiteWrapper site) {
        return site.getInCreationDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getReceivingWithErrorsDispatchCollection(
        SiteWrapper site) {
        return site.getReceivingWithErrorsDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getInTransitSentDispatchCollection(
        SiteWrapper site) {
        return site.getInTransitSentDispatchCollection();
    }

}
