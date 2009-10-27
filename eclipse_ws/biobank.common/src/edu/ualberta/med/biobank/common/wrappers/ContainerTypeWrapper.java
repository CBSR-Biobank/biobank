package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    private static Map<Integer, ContainerLabelingSchemeWrapper> labelingSchemeMap;

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
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "comment", "nameShort", "topLevel",
            "defaultTemperature", "activityStatus", "sampleTypeCollection",
            "childContainerTypeCollection", "site", "capacity",
            "childLabelingScheme" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        checkSite();
        checkNameUnique();
        if (getCapacity() == null) {
            throw new BiobankCheckException("Capacity should be set");
        }
        getCapacity().persistChecks();
        if (getChildLabelingScheme() == null) {
            throw new BiobankCheckException("Labeling scheme should be set");
        }
        if (!isNew()) {
            boolean exists = isUsedByContainers();
            ContainerType oldObject = getObjectFromDatabase();
            checkNewCapacity(oldObject, exists);
            checkTopLevel(oldObject, exists);
            checkLabelingScheme(oldObject, exists);
        }
    }

    private void checkSite() throws BiobankCheckException {
        if (getSite() == null) {
            throw new BiobankCheckException(
                "Should assign a site to this container type");
        }
    }

    private void checkNameUnique() throws ApplicationException,
        BiobankCheckException {
        String notSameTypeString = "";
        List<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {
            getSite().getId(), getName() }));
        if (!isNew()) {
            notSameTypeString = " and id<>?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + ContainerType.class.getName()
            + " where site.id = ? and name = ?" + notSameTypeString, params);

        List<Object> results = appService.query(c);
        if (results.size() != 0) {
            throw new BiobankCheckException("A storage type with name \""
                + getName() + "\" already exists.");
        }
    }

    @Override
    public Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

    public Collection<ContainerTypeWrapper> getAllChildren()
        throws ApplicationException {
        List<ContainerTypeWrapper> allChildren = new ArrayList<ContainerTypeWrapper>();
        for (ContainerTypeWrapper type : getChildContainerTypeCollection()) {
            allChildren.addAll(type.getAllChildren());
            allChildren.add(type);
        }
        return allChildren;
    }

    @Override
    public void delete() throws BiobankCheckException, ApplicationException,
        WrapperException {
        if (!isNew()) {
            deleteChecks();
            // should remove this containerType from its parents
            for (ContainerTypeWrapper parent : getParentContainerTypes()) {
                List<ContainerTypeWrapper> children = parent
                    .getChildContainerTypeCollection();
                children.remove(this);
                parent.setChildContainerTypeCollection(children);
                parent.persist();
            }
            appService.executeQuery(new DeleteExampleQuery(wrappedObject));
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
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject }));
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
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject.getId() }));
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
        wrappedObject.setName(comment);
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

    public void setActivityStatus(String activityStatus) {
        String oldActivityStatus = wrappedObject.getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setSampleTypeCollection(Collection<SampleType> sampleTypes,
        boolean setNull) {
        Collection<SampleType> oldTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(sampleTypes);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldTypes, sampleTypes);
        if (setNull) {
            propertiesMap.put("sampleTypeCollection", null);
        }
    }

    public void setSampleTypeCollection(
        Collection<SampleTypeWrapper> sampleTypes) {
        Collection<SampleType> sampleTypesObjects = new HashSet<SampleType>();
        for (SampleTypeWrapper type : sampleTypes) {
            sampleTypesObjects.add(type.getWrappedObject());
        }
        setSampleTypeCollection(sampleTypesObjects, false);
        propertiesMap.put("sampleTypeCollection", sampleTypes);
    }

    @SuppressWarnings("unchecked")
    public Collection<SampleTypeWrapper> getSampleTypeCollection() {
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

    public Collection<SampleTypeWrapper> getSampleTypeCollectionRecursively()
        throws ApplicationException {
        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        sampleTypes.addAll(getSampleTypeCollection());
        for (ContainerTypeWrapper type : getChildContainerTypeCollection()) {
            sampleTypes.addAll(type.getSampleTypeCollectionRecursively());
        }
        return sampleTypes;
    }

    public void setChildContainerTypeCollection(
        Collection<ContainerType> containerTypes, boolean setNull) {
        Collection<ContainerType> oldContainerTypes = wrappedObject
            .getChildContainerTypeCollection();
        wrappedObject.setChildContainerTypeCollection(containerTypes);
        propertyChangeSupport.firePropertyChange(
            "childContainerTypeCollection", oldContainerTypes, containerTypes);
        if (setNull) {
            propertiesMap.put("childContainerTypeCollection", null);
        }
    }

    public void setChildContainerTypeCollection(
        List<ContainerTypeWrapper> containerTypes) {
        Collection<ContainerType> children = new HashSet<ContainerType>();
        for (ContainerTypeWrapper pos : containerTypes) {
            children.add(pos.getWrappedObject());
        }
        setChildContainerTypeCollection(children, false);
        propertiesMap.put("childContainerTypeCollection", containerTypes);
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

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper site) {
        setSite(site.getWrappedObject());
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    private CapacityWrapper getCapacity() {
        Capacity capacity = wrappedObject.getCapacity();
        if (capacity == null) {
            return null;
        }
        return new CapacityWrapper(appService, capacity);
    }

    private void setCapacity(Capacity capacity) {
        Capacity oldCapacity = wrappedObject.getCapacity();
        wrappedObject.setCapacity(capacity);
        propertyChangeSupport.firePropertyChange("capacity", oldCapacity,
            capacity);
    }

    private void setCapacity(CapacityWrapper capacity) {
        setCapacity(capacity.wrappedObject);
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
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            capacity = new CapacityWrapper(appService, new Capacity());
        }
        capacity.setRow(maxRows);
        setCapacity(capacity);
    }

    public void setColCapacity(Integer maxCols) {
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            capacity = new CapacityWrapper(appService, new Capacity());
        }
        capacity.setCol(maxCols);
        setCapacity(capacity);
    }

    public void setChildLabelingScheme(Integer id) {
        setChildLabelingScheme(labelingSchemeMap.get(id));
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

    public void setSampleTypes(List<Integer> sampleTypesIds,
        List<SampleTypeWrapper> allSampleTypes) throws BiobankCheckException {
        Set<SampleTypeWrapper> selSampleTypes = new HashSet<SampleTypeWrapper>();
        if (sampleTypesIds != null) {
            for (SampleTypeWrapper sampleType : allSampleTypes) {
                if (sampleTypesIds.indexOf(sampleType.getId()) >= 0) {
                    selSampleTypes.add(sampleType);
                }
            }
            if (selSampleTypes.size() != sampleTypesIds.size()) {
                throw new BiobankCheckException(
                    "Problem with sample type selections");
            }
        }
        setSampleTypeCollection(selSampleTypes);
    }

    public void setChildContainerTypes(List<Integer> containerTypesIds,
        List<ContainerTypeWrapper> allContainerTypes)
        throws BiobankCheckException, ApplicationException {
        List<ContainerTypeWrapper> selContainerTypes = new ArrayList<ContainerTypeWrapper>();
        if (containerTypesIds != null && containerTypesIds.size() > 0
            && allContainerTypes != null) {
            for (ContainerTypeWrapper containerType : allContainerTypes) {
                if (containerTypesIds.indexOf(containerType.getId()) >= 0) {
                    selContainerTypes.add(containerType);
                }
            }
        }
        List<ContainerTypeWrapper> children = getChildContainerTypeCollection();
        List<Integer> missing = new ArrayList<Integer>();
        if (children != null) {
            for (ContainerTypeWrapper child : children) {
                int id = child.getId();
                if (containerTypesIds.indexOf(id) < 0) {
                    missing.add(id);
                }
            }
        }

        if (missing.size() == 0 || canRemoveChildrenContainer(missing)) {
            setChildContainerTypeCollection(selContainerTypes);
        } else {
            throw new BiobankCheckException(
                "Unable to remove child type. This parent/child relationship exists in storage. Remove all instances before attempting to delete a child type.");
        }
    }

    private boolean canRemoveChildrenContainer(List<Integer> missing)
        throws ApplicationException {
        String queryString = "from "
            + ContainerPosition.class.getName()
            + " as cp inner join cp.parentContainer as cparent"
            + " where cparent.containerType.id=? and cp.container.containerType.id in (select id from "
            + ContainerType.class.getName() + " as ct where ct.id=?";
        List<Object> params = new ArrayList<Object>();
        params.add(getId());
        params.add(missing.get(0));
        for (int i = 1; i < missing.size(); i++) {
            queryString += "OR ct.id=?";
            params.add(missing.get(i));
        }
        queryString += ")";

        List<Object> results = appService.query(new HQLCriteria(queryString,
            params));
        return results.size() == 0;
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
            .equals(dbCapacity.getColCapacity()))
            && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to alter dimensions. A container of this type exists in storage. Remove all instances before attempting to modify this container type.");
        }
    }

    private void checkTopLevel(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        if ((getTopLevel() == null && oldObject.getTopLevel() != null)
            || (getTopLevel() != null && oldObject.getTopLevel() == null)
            || (getTopLevel() != null && oldObject.getTopLevel() != null && !getTopLevel()
                .equals(oldObject.getTopLevel())) && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to change the \"Top Level\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.");
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
                "Unable to change the \"Child Labeling scheme\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.");
        }
    }

    public static List<ContainerTypeWrapper> getTopContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName()
            + " where site.id = ? and topLevel=true", Arrays
            .asList(new Object[] { site.getId() }));
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
        return list;
    }

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * will contains containerName.
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
        HQLCriteria criteria = new HQLCriteria(query, Arrays
            .asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return transformToWrapperList(appService, containerTypes);
    }

    private static Map<Integer, ContainerLabelingSchemeWrapper> getAllLabelingSchemesMap(
        WritableApplicationService appService) throws RuntimeException {
        try {
            if (labelingSchemeMap == null) {
                labelingSchemeMap = new HashMap<Integer, ContainerLabelingSchemeWrapper>();
                for (ContainerLabelingSchemeWrapper labeling : ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemes(appService)) {
                    labelingSchemeMap.put(labeling.getId(), labeling);

                }
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

    @Override
    public int compareTo(ModelWrapper<ContainerType> type) {
        String c1Name = wrappedObject.getName();
        String c2Name = type.wrappedObject.getName();
        return ((c1Name.compareTo(c2Name) > 0) ? 1 : (c1Name.equals(c2Name) ? 0
            : -1));
    }

    @Override
    public String toString() {
        return getName() + " (" + getNameShort() + ")";
    }

}
