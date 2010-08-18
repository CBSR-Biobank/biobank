package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    private static Map<Integer, ContainerLabelingSchemeWrapper> labelingSchemeMap;

    private Set<ContainerTypeWrapper> deletedChildTypes = new HashSet<ContainerTypeWrapper>();

    private Set<SampleTypeWrapper> deletedSampleTypes = new HashSet<SampleTypeWrapper>();

    private ActivityStatusWrapper activityStatus;

    private SiteWrapper site;

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
        if (labelingSchemeMap == null) {
            getAllLabelingSchemesMap(appService);
        }
    }

    public ContainerTypeWrapper(WritableApplicationService appService) {
        super(appService);
        if (labelingSchemeMap == null) {
            getAllLabelingSchemesMap(appService);
        }
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "comment", "nameShort", "topLevel",
            "defaultTemperature", "activityStatus", "sampleTypeCollection",
            "childContainerTypeCollection", "site", "capacity",
            "childLabelingScheme", "rowCapacity", "colCapacity" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        checkSite();
        checkNotEmpty(getName(), "Name");
        checkNoDuplicatesInSite(ContainerType.class, "name", getName(),
            getSite().getId(), "A container type with name \"" + getName()
                + "\" already exists.");
        checkNotEmpty(getNameShort(), "Short Name");
        checkNoDuplicatesInSite(ContainerType.class, "nameShort",
            getNameShort(), getSite().getId(),
            "A container type with short name \"" + getNameShort()
                + "\" already exists.");
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the container type does not have an activity status");
        }
        if (getCapacity() == null) {
            throw new BiobankCheckException("Capacity should be set");
        }
        getCapacity().persistChecks();
        if (getChildLabelingScheme() == null) {
            throw new BiobankCheckException("Labeling scheme should be set");
        } else {
            // should throw error if labeling scheme too small for container
            if (!LabelingScheme.checkBounds(appService,
                getChildLabelingScheme(), getCapacity().getRowCapacity(),
                getCapacity().getColCapacity()))
                throw new BiobankCheckException("Labeling scheme cannot label "
                    + getCapacity().getRowCapacity() + " rows and "
                    + getCapacity().getColCapacity() + " columns.");
        }
        if (!isNew()) {
            boolean exists = isUsedByContainers();
            ContainerType oldObject = getObjectFromDatabase();
            checkNewCapacity(oldObject, exists);
            checkTopLevel(oldObject, exists);
            checkLabelingScheme(oldObject, exists);
            checkDeletedChildContainerTypes();
            checkDeletedSampleTypes();
        }
    }

    private void checkDeletedSampleTypes() throws ApplicationException,
        BiobankCheckException {
        if (deletedSampleTypes.size() > 0) {
            String queryString = "from " + AliquotPosition.class.getName()
                + " as sp inner join sp.container as sparent"
                + " where sparent.containerType.id=? and "
                + "sp.aliquot.sampleType.id in (select id from "
                + SampleType.class.getName() + " as st where";
            List<Object> params = new ArrayList<Object>();
            params.add(getId());
            int i = 0;
            for (SampleTypeWrapper type : deletedSampleTypes) {
                if (i != 0) {
                    queryString += " OR";
                }
                queryString += " st.id=?";
                params.add(type.getId());
                i++;
            }
            queryString += ")";
            List<Object> results = appService.query(new HQLCriteria(
                queryString, params));
            if (results.size() != 0) {
                throw new BiobankCheckException(
                    "Unable to remove sample type. This parent/child relationship "
                        + "exists in database. Remove all instances before attempting to "
                        + "delete a sample type.");
            }
        }
    }

    private void checkDeletedChildContainerTypes()
        throws BiobankCheckException, ApplicationException {
        if (deletedChildTypes.size() > 0) {
            String queryString = "from " + ContainerPosition.class.getName()
                + " as cp inner join cp.parentContainer as cparent"
                + " where cparent.containerType.id=? and "
                + "cp.container.containerType.id in (select id from "
                + ContainerType.class.getName() + " as ct where";
            List<Object> params = new ArrayList<Object>();
            params.add(getId());
            int i = 0;
            for (ContainerTypeWrapper type : deletedChildTypes) {
                if (i != 0) {
                    queryString += " OR";
                }
                queryString += " ct.id=?";
                params.add(type.getId());
                i++;
            }
            queryString += ")";
            List<Object> results = appService.query(new HQLCriteria(
                queryString, params));
            if (results.size() != 0) {
                throw new BiobankCheckException(
                    "Unable to remove child type. This parent/child relationship "
                        + "exists in database. Remove all instances before attempting to "
                        + "delete a child type.");
            }
        }
    }

    private void checkSite() throws BiobankCheckException {
        if (getSite() == null) {
            throw new BiobankCheckException(
                "Should assign a site to this container type");
        }
    }

    @Override
    public Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

    public Collection<ContainerTypeWrapper> getChildrenRecursively()
        throws ApplicationException {
        List<ContainerTypeWrapper> allChildren = new ArrayList<ContainerTypeWrapper>();
        List<ContainerTypeWrapper> children = getChildContainerTypeCollection();
        if (children != null) {
            for (ContainerTypeWrapper type : children) {
                allChildren.addAll(type.getChildrenRecursively());
                allChildren.add(type);
            }
        }
        return allChildren;
    }

    @Override
    public void deleteDependencies() throws Exception {
        // should remove this containerType from its parents
        for (ContainerTypeWrapper parent : getParentContainerTypes()) {
            parent.removeChildContainers(Arrays.asList(this));
            parent.persist();
        }
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (isUsedByContainers()) {
            throw new BiobankCheckException("Unable to delete container type "
                + getName() + ". A container of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public boolean isUsedByContainers() throws ApplicationException,
        BiobankCheckException {
        String queryString = "select count(c) from "
            + Container.class.getName() + " as c where c.containerType=?)";
        HQLCriteria c = new HQLCriteria(queryString,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) > 0;
    }

    public List<ContainerTypeWrapper> getParentContainerTypes()
        throws ApplicationException {
        String queryString = "select ct from "
            + ContainerType.class.getName()
            + " as ct inner join ct.childContainerTypeCollection as child where child.id = ?)";
        HQLCriteria c = new HQLCriteria(queryString,
            Arrays.asList(new Object[] { wrappedObject.getId() }));
        List<ContainerType> results = appService.query(c);
        return transformToWrapperList(appService, results);
    }

    public void setName(String name) {
        String oldName = wrappedObject.getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = wrappedObject.getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("nameShort", oldNameShort,
            nameShort);
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setTopLevel(Boolean topLevel) {
        Boolean oldTopLevel = wrappedObject.getTopLevel();
        wrappedObject.setTopLevel(topLevel);
        propertyChangeSupport.firePropertyChange("topLevel", oldTopLevel,
            topLevel);
    }

    public Boolean getTopLevel() {
        return wrappedObject.getTopLevel();
    }

    public void setDefaultTemperature(Double temperature) {
        Double oldTemp = wrappedObject.getDefaultTemperature();
        wrappedObject.setDefaultTemperature(temperature);
        propertyChangeSupport.firePropertyChange("defaultTemperature", oldTemp,
            temperature);
    }

    public Double getDefaultTemperature() {
        return wrappedObject.getDefaultTemperature();
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

    private void setSampleTypeCollection(Collection<SampleType> allTypeObjects,
        List<SampleTypeWrapper> allTypeWrappers) {
        Collection<SampleType> oldTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(allTypeObjects);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldTypes, allTypeObjects);
        propertiesMap.put("sampleTypeCollection", allTypeWrappers);
    }

    public void addSampleTypes(List<SampleTypeWrapper> newSampleTypes) {
        if (newSampleTypes != null && newSampleTypes.size() > 0) {
            Collection<SampleType> allTypeObjects = new HashSet<SampleType>();
            List<SampleTypeWrapper> allTypeWrappers = new ArrayList<SampleTypeWrapper>();
            // already added types
            List<SampleTypeWrapper> currentList = getSampleTypeCollection();
            if (currentList != null) {
                for (SampleTypeWrapper type : currentList) {
                    allTypeObjects.add(type.getWrappedObject());
                    allTypeWrappers.add(type);
                }
            }
            // new types
            for (SampleTypeWrapper type : newSampleTypes) {
                allTypeObjects.add(type.getWrappedObject());
                allTypeWrappers.add(type);
                deletedSampleTypes.remove(type);
            }
            setSampleTypeCollection(allTypeObjects, allTypeWrappers);
        }
    }

    public void removeSampleTypes(List<SampleTypeWrapper> typesToRemove) {
        if (typesToRemove != null && typesToRemove.size() > 0) {
            deletedSampleTypes.addAll(typesToRemove);
            Collection<SampleType> allTypeObjects = new HashSet<SampleType>();
            List<SampleTypeWrapper> allTypeWrappers = new ArrayList<SampleTypeWrapper>();
            // already added types
            List<SampleTypeWrapper> currentList = getSampleTypeCollection();
            if (currentList != null) {
                for (SampleTypeWrapper type : currentList) {
                    if (!deletedSampleTypes.contains(type)) {
                        allTypeObjects.add(type.getWrappedObject());
                        allTypeWrappers.add(type);
                    }
                }
            }
            setSampleTypeCollection(allTypeObjects, allTypeWrappers);
        }
    }

    @SuppressWarnings("unchecked")
    public List<SampleTypeWrapper> getSampleTypeCollection() {
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
        return sampleTypeCollection;
    }

    public Set<SampleTypeWrapper> getSampleTypesRecursively()
        throws ApplicationException {
        Set<SampleTypeWrapper> sampleTypes = new HashSet<SampleTypeWrapper>();
        List<SampleTypeWrapper> sampleSubSet = getSampleTypeCollection();
        if (sampleSubSet != null)
            sampleTypes.addAll(sampleSubSet);
        for (ContainerTypeWrapper type : getChildContainerTypeCollection()) {
            sampleTypes.addAll(type.getSampleTypesRecursively());
        }
        return sampleTypes;
    }

    private void setChildContainerTypes(
        Collection<ContainerType> allTypeObjects,
        List<ContainerTypeWrapper> allTypeWrappers) {
        Collection<ContainerType> oldContainerTypes = wrappedObject
            .getChildContainerTypeCollection();
        wrappedObject.setChildContainerTypeCollection(allTypeObjects);
        propertyChangeSupport.firePropertyChange(
            "childContainerTypeCollection", oldContainerTypes, allTypeObjects);
        propertiesMap.put("childContainerTypeCollection", allTypeWrappers);
    }

    public void addChildContainerTypes(
        List<ContainerTypeWrapper> newContainerTypes) {
        if (newContainerTypes != null && newContainerTypes.size() > 0) {
            Collection<ContainerType> allTypeObjects = new HashSet<ContainerType>();
            List<ContainerTypeWrapper> allTypesWrappers = new ArrayList<ContainerTypeWrapper>();
            // already added types
            List<ContainerTypeWrapper> currentList = getChildContainerTypeCollection();
            if (currentList != null) {
                for (ContainerTypeWrapper type : currentList) {
                    allTypeObjects.add(type.getWrappedObject());
                    allTypesWrappers.add(type);
                }
            }
            // new types
            for (ContainerTypeWrapper type : newContainerTypes) {
                allTypeObjects.add(type.getWrappedObject());
                allTypesWrappers.add(type);
                deletedChildTypes.remove(type);
            }
            setChildContainerTypes(allTypeObjects, allTypesWrappers);
        }
    }

    public void removeChildContainers(List<ContainerTypeWrapper> typesToRemove) {
        if (typesToRemove != null && typesToRemove.size() > 0) {
            deletedChildTypes.addAll(typesToRemove);
            Collection<ContainerType> allTypeObjects = new HashSet<ContainerType>();
            List<ContainerTypeWrapper> allTypesWrappers = new ArrayList<ContainerTypeWrapper>();
            // already added types
            List<ContainerTypeWrapper> currentList = getChildContainerTypeCollection();
            if (currentList != null) {
                for (ContainerTypeWrapper type : currentList) {
                    if (!deletedChildTypes.contains(type)) {
                        allTypeObjects.add(type.getWrappedObject());
                        allTypesWrappers.add(type);
                    }
                }
            }
            setChildContainerTypes(allTypeObjects, allTypesWrappers);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ContainerTypeWrapper> getChildContainerTypeCollection() {
        List<ContainerTypeWrapper> childContainerTypeCollection = (List<ContainerTypeWrapper>) propertiesMap
            .get("childContainerTypeCollection");
        if (childContainerTypeCollection == null) {
            Collection<ContainerType> children = wrappedObject
                .getChildContainerTypeCollection();
            if (children != null) {
                childContainerTypeCollection = transformToWrapperList(
                    appService, children);
                propertiesMap.put("childContainerTypeCollection",
                    childContainerTypeCollection);
            }
        }
        return childContainerTypeCollection;
    }

    public void setSite(SiteWrapper site) {
        this.site = site;
        Site oldSite = wrappedObject.getSite();
        Site newSite = site.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    public SiteWrapper getSite() {
        if (site == null) {
            Site s = wrappedObject.getSite();
            if (s == null)
                return null;
            site = new SiteWrapper(appService, s);
        }
        return site;
    }

    private CapacityWrapper getCapacity() {
        Capacity capacity = wrappedObject.getCapacity();
        if (capacity == null) {
            return null;
        }
        return new CapacityWrapper(appService, capacity);
    }

    private void setCapacity(CapacityWrapper capacity) {
        Capacity oldCapacity = wrappedObject.getCapacity();
        Capacity newCapacity = capacity.wrappedObject;
        wrappedObject.setCapacity(newCapacity);
        propertyChangeSupport.firePropertyChange("capacity", oldCapacity,
            newCapacity);
    }

    public Integer getRowCapacity() {
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            return null;
        }
        return capacity.getRowCapacity();
    }

    public Integer getColCapacity() {
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            return null;
        }
        return capacity.getColCapacity();
    }

    public void setRowCapacity(Integer maxRows) {
        Integer old = getRowCapacity();
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            capacity = new CapacityWrapper(appService, new Capacity());
        }
        capacity.setRow(maxRows);
        setCapacity(capacity);
        propertyChangeSupport.firePropertyChange("rowCapacity", old, maxRows);
    }

    public void setColCapacity(Integer maxCols) {
        Integer old = getColCapacity();
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            capacity = new CapacityWrapper(appService, new Capacity());
        }
        capacity.setCol(maxCols);
        setCapacity(capacity);
        propertyChangeSupport.firePropertyChange("colCapacity", old, maxCols);
    }

    public void setChildLabelingScheme(Integer id) {
        if (id == null)
            return;
        setChildLabelingScheme(labelingSchemeMap.get(id));
    }

    public void setChildLabelingSchemeName(String name) throws Exception {
        if (name == null)
            return;
        for (ContainerLabelingSchemeWrapper scheme : labelingSchemeMap.values()) {
            if (scheme.getName().equals(name)) {
                setChildLabelingScheme(scheme);
                return;
            }
        }
        throw new Exception("labeling scheme with name \"" + name
            + "\" does not exist");
    }

    private void setChildLabelingScheme(ContainerLabelingSchemeWrapper scheme) {
        if (scheme == null) {
            setChildLabelingScheme((ContainerLabelingScheme) null);
        } else {
            setChildLabelingScheme(scheme.getWrappedObject());
        }
    }

    private void setChildLabelingScheme(ContainerLabelingScheme scheme) {
        ContainerLabelingScheme oldLbl = wrappedObject.getChildLabelingScheme();
        wrappedObject.setChildLabelingScheme(scheme);
        propertyChangeSupport.firePropertyChange("childLabelingScheme", oldLbl,
            scheme);
    }

    public Integer getChildLabelingScheme() {
        ContainerLabelingScheme scheme = wrappedObject.getChildLabelingScheme();
        if (scheme == null) {
            return null;
        }
        return scheme.getId();
    }

    public String getChildLabelingSchemeName() {
        ContainerLabelingScheme scheme = wrappedObject.getChildLabelingScheme();
        if (scheme == null) {
            return null;
        }
        return scheme.getName();
    }

    /**
     * Check if we can use the new capacity
     */
    private void checkNewCapacity(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        CapacityWrapper currentCapacity = getCapacity();
        Capacity dbCapacity = oldObject.getCapacity();
        if (!(currentCapacity.getRowCapacity().equals(
            dbCapacity.getRowCapacity()) && currentCapacity.getColCapacity()
            .equals(dbCapacity.getColCapacity())) && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to alter dimensions. A container of this type exists "
                    + "in storage. Remove all instances before attempting to "
                    + "modify this container type.");
        }
    }

    private void checkTopLevel(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        if ((getTopLevel() == null && oldObject.getTopLevel() != null)
            || (getTopLevel() != null && oldObject.getTopLevel() == null)
            || (getTopLevel() != null && oldObject.getTopLevel() != null && !getTopLevel()
                .equals(oldObject.getTopLevel())) && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to change the \"Top Level\" property. A container "
                    + "requiring this property exists in storage. Remove all "
                    + "instances before attempting to modify this container type.");
        }
    }

    private void checkLabelingScheme(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        ContainerTypeWrapper oldWrapper = new ContainerTypeWrapper(appService,
            oldObject);
        if (getChildLabelingScheme() == null
            && oldWrapper.getChildLabelingScheme() == null) {
            return;
        }
        if (getChildLabelingScheme() == null
            || oldWrapper.getChildLabelingScheme() == null
            || !getChildLabelingScheme().equals(
                oldWrapper.getChildLabelingScheme())
            && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to change the \"Child Labeling scheme\" property. "
                    + "A container requiring this property exists in storage. "
                    + "Remove all instances before attempting to modify this "
                    + "container type.");
        }
    }

    public static List<ContainerTypeWrapper> getTopContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName()
            + " where site.id = ? and topLevel=true",
            Arrays.asList(new Object[] { site.getId() }));
        List<ContainerType> types = appService.query(criteria);
        return transformToWrapperList(appService, types);
    }

    public static List<ContainerTypeWrapper> transformToWrapperList(
        WritableApplicationService appService,
        Collection<ContainerType> containerTypes) {
        List<ContainerTypeWrapper> list = new ArrayList<ContainerTypeWrapper>();
        for (ContainerType type : containerTypes) {
            list.add(new ContainerTypeWrapper(appService, type));
        }
        return new ArrayList<ContainerTypeWrapper>(list);
    }

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * should contain containerName as a substring in the name.
     */
    public static List<ContainerTypeWrapper> getContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String containerName, boolean useStrictName)
        throws ApplicationException {
        String nameComparison = "=";
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "like";
            containerNameParameter = "%" + containerName + "%";
        }
        String query = "from " + ContainerType.class.getName()
            + " where site = ? and name " + nameComparison + " ?";
        HQLCriteria criteria = new HQLCriteria(query,
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return transformToWrapperList(appService, containerTypes);
    }

    private static Map<Integer, ContainerLabelingSchemeWrapper> getAllLabelingSchemesMap(
        WritableApplicationService appService) throws RuntimeException {
        try {
            if (labelingSchemeMap == null) {
                labelingSchemeMap = ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemesMap(appService);
            }
            return labelingSchemeMap;
        } catch (ApplicationException e) {
            throw new RuntimeException(
                "could not load container labeling schemes");
        }
    }

    public static Map<Integer, String> getAllLabelingSchemes(
        WritableApplicationService appService) {
        getAllLabelingSchemesMap(appService);
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper labeling : labelingSchemeMap
            .values()) {
            map.put(labeling.getId(), labeling.getName());
        }
        return map;
    }

    /**
     * get count of container which type is this
     */
    public long getContainersCount() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + Container.class.getName() + " where containerType.id=?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0);
    }

    @Override
    public int compareTo(ModelWrapper<ContainerType> type) {
        if (type instanceof ContainerTypeWrapper) {
            String c1Name = wrappedObject.getName();
            String c2Name = type.wrappedObject.getName();
            return ((c1Name.compareTo(c2Name) > 0) ? 1
                : (c1Name.equals(c2Name) ? 0 : -1));
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName() + " (" + getNameShort() + ")";
    }

    @Override
    protected void resetInternalField() {
        deletedChildTypes.clear();
        deletedSampleTypes.clear();
    }

    public String getPositionString(RowColPos position) {
        return LabelingScheme.getPositionString(position,
            getChildLabelingScheme(), getRowCapacity(), getColCapacity());
    }

    public RowColPos getRowColFromPositionString(String position)
        throws Exception {
        return LabelingScheme.getRowColFromPositionString(position,
            getChildLabelingScheme(), getRowCapacity(), getColCapacity());
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        activityStatus = null;
        site = null;
    }
}
