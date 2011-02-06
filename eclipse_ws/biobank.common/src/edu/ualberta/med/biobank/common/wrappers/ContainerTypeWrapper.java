package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.AliquotPeer;
import edu.ualberta.med.biobank.common.peer.AliquotPositionPeer;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerLabelingSchemePeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SampleTypePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    private Set<ContainerTypeWrapper> deletedChildTypes = new HashSet<ContainerTypeWrapper>();

    private Set<SampleTypeWrapper> deletedSampleTypes = new HashSet<SampleTypeWrapper>();

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ContainerTypePeer.PROP_NAMES;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        if (getSite() != null) {
            checkNoDuplicatesInSite(ContainerType.class,
                ContainerTypePeer.NAME.getName(), getName(), getSite().getId(),
                ContainerTypePeer.NAME.getName());
            checkNoDuplicatesInSite(ContainerType.class,
                ContainerTypePeer.NAME_SHORT.getName(), getNameShort(),
                getSite().getId(), ContainerTypePeer.NAME_SHORT.getName());
        }
        if (getCapacity() == null) {
            throw new ValueNotSetException("capacity");
        }
        if (getChildLabelingSchemeId() != null) {
            // should throw error if labeling scheme too small for container
            if (!ContainerLabelingSchemeWrapper.checkBounds(appService,
                getChildLabelingSchemeId(), getCapacity().getRowCapacity(),
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

    private static final String DELETED_SAMPLE_TYPES_QRY = "from "
        + AliquotPosition.class.getName()
        + " as ap inner join ap."
        + AliquotPositionPeer.CONTAINER.getName()
        + " as aparent where aparent."
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.ID)
        + "=? and ap."
        + Property.concatNames(AliquotPositionPeer.ALIQUOT,
            AliquotPeer.SAMPLE_TYPE, SampleTypePeer.ID) + " in (";

    private void checkDeletedSampleTypes() throws ApplicationException,
        BiobankCheckException {
        if (deletedSampleTypes.size() > 0) {
            List<Integer> ids = new ArrayList<Integer>();
            for (SampleTypeWrapper type : deletedSampleTypes) {
                ids.add(type.getId());
            }
            StringBuffer sb = new StringBuffer(DELETED_SAMPLE_TYPES_QRY)
                .append(StringUtils.join(ids, ',')).append(")");
            List<Object> results = appService.query(new HQLCriteria(sb
                .toString(), Arrays.asList(new Object[] { getId() })));
            if (results.size() != 0) {
                throw new BiobankCheckException(
                    "Unable to remove sample type. This parent/child relationship "
                        + "exists in database. Remove all instances before attempting to "
                        + "delete a sample type.");
            }
        }
    }

    private static final String DELETED_CONTAINER_TYPES_QRY = "from "
        + ContainerPosition.class.getName()
        + " as cp inner join cp."
        + ContainerPositionPeer.PARENT_CONTAINER.getName()
        + " as cparent where cparent."
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.ID)
        + "=? and cp."
        + Property.concatNames(ContainerPositionPeer.CONTAINER,
            ContainerPeer.CONTAINER_TYPE, ContainerTypePeer.ID) + " in (";

    private void checkDeletedChildContainerTypes()
        throws BiobankCheckException, ApplicationException {
        if (deletedChildTypes.size() > 0) {
            List<Integer> ids = new ArrayList<Integer>();
            for (ContainerTypeWrapper type : deletedChildTypes) {
                ids.add(type.getId());
            }
            StringBuffer sb = new StringBuffer(DELETED_CONTAINER_TYPES_QRY)
                .append(StringUtils.join(ids, ',')).append(")");
            List<Object> results = appService.query(new HQLCriteria(sb
                .toString(), Arrays.asList(new Object[] { getId() })));
            if (results.size() != 0) {
                throw new BiobankCheckException(
                    "Unable to remove child type. This parent/child relationship "
                        + "exists in database. Remove all instances before attempting to "
                        + "delete a child type.");
            }
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
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedByContainers()) {
            throw new BiobankCheckException("Unable to delete container type "
                + getName() + ". A container of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    private static final String IS_USED_BY_CONTAINERS_QRY = "select count(c) from "
        + Container.class.getName()
        + " as c where c."
        + ContainerPeer.CONTAINER_TYPE.getName() + "=?";

    public boolean isUsedByContainers() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_CONTAINERS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results.get(0) > 0;
    }

    private static final String PARENT_CONTAINER_TYPES_QRY = "select ct from "
        + ContainerType.class.getName() + " as ct inner join ct."
        + ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION.getName()
        + " as child where child." + ContainerTypePeer.ID.getName() + "=?";

    public List<ContainerTypeWrapper> getParentContainerTypes()
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(PARENT_CONTAINER_TYPES_QRY,
            Arrays.asList(new Object[] { wrappedObject.getId() }));
        List<ContainerType> results = appService.query(c);
        return transformToWrapperList(appService, results);
    }

    public String getName() {
        return getProperty(ContainerTypePeer.NAME);
    }

    public void setName(String name) {
        setProperty(ContainerTypePeer.NAME, name);
    }

    public String getNameShort() {
        return getProperty(ContainerTypePeer.NAME_SHORT);
    }

    public void setNameShort(String nameShort) {
        setProperty(ContainerTypePeer.NAME_SHORT, nameShort);
    }

    public String getComment() {
        return getProperty(ContainerTypePeer.COMMENT);
    }

    public void setComment(String comment) {
        setProperty(ContainerTypePeer.COMMENT, comment);
    }

    public Boolean getTopLevel() {
        return getProperty(ContainerTypePeer.TOP_LEVEL);
    }

    public void setTopLevel(Boolean topLevel) {
        setProperty(ContainerTypePeer.TOP_LEVEL, topLevel);
    }

    public Double getDefaultTemperature() {
        return getProperty(ContainerTypePeer.DEFAULT_TEMPERATURE);
    }

    public void setDefaultTemperature(Double temperature) {
        setProperty(ContainerTypePeer.DEFAULT_TEMPERATURE, temperature);
    }

    public ActivityStatusWrapper getActivityStatus() {
        return getWrappedProperty(ContainerTypePeer.ACTIVITY_STATUS,
            ActivityStatusWrapper.class);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        setWrappedProperty(ContainerTypePeer.ACTIVITY_STATUS, activityStatus);
    }

    public void addSampleTypes(List<SampleTypeWrapper> newSampleTypes) {
        addToWrapperCollection(ContainerTypePeer.SAMPLE_TYPE_COLLECTION,
            newSampleTypes);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedSampleTypes.removeAll(newSampleTypes);
    }

    public void removeSampleTypes(List<SampleTypeWrapper> typesToRemove) {
        deletedSampleTypes.addAll(typesToRemove);
        removeFromWrapperCollection(ContainerTypePeer.SAMPLE_TYPE_COLLECTION,
            typesToRemove);
    }

    public List<SampleTypeWrapper> getSampleTypeCollection() {
        return getWrapperCollection(ContainerTypePeer.SAMPLE_TYPE_COLLECTION,
            SampleTypeWrapper.class, true);
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

    public void addChildContainerTypes(
        List<ContainerTypeWrapper> newContainerTypes) {
        addToWrapperCollection(
            ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION,
            newContainerTypes);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedChildTypes.removeAll(newContainerTypes);
    }

    public void removeChildContainers(List<ContainerTypeWrapper> typesToRemove) {
        deletedChildTypes.addAll(typesToRemove);
        removeFromWrapperCollection(
            ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION, typesToRemove);
    }

    public List<ContainerTypeWrapper> getChildContainerTypeCollection() {
        return getWrapperCollection(
            ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION,
            ContainerTypeWrapper.class, true);
    }

    public SiteWrapper getSite() {
        return getWrappedProperty(ContainerTypePeer.SITE, SiteWrapper.class);
    }

    public void setSite(SiteWrapper site) {
        setWrappedProperty(ContainerTypePeer.SITE, site);
    }

    private CapacityWrapper getCapacity() {
        return getWrappedProperty(ContainerTypePeer.CAPACITY,
            CapacityWrapper.class);
    }

    private void setCapacity(CapacityWrapper capacity) {
        setWrappedProperty(ContainerTypePeer.CAPACITY, capacity);
    }

    public Integer getRowCapacity() {
        return getProperty(getCapacity(), CapacityPeer.ROW_CAPACITY);
    }

    public Integer getColCapacity() {
        return getProperty(getCapacity(), CapacityPeer.COL_CAPACITY);
    }

    private CapacityWrapper initCapacity() {
        CapacityWrapper capacity = getCapacity();
        if (capacity == null) {
            capacity = new CapacityWrapper(appService);
            setCapacity(capacity);
        }
        return capacity;
    }

    public void setRowCapacity(Integer maxRows) {
        setProperty(initCapacity(), CapacityPeer.ROW_CAPACITY, maxRows);
    }

    public void setColCapacity(Integer maxCols) {
        setProperty(initCapacity(), CapacityPeer.COL_CAPACITY, maxCols);
    }

    private ContainerLabelingSchemeWrapper getChildLabelingScheme() {
        return getWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME,
            ContainerLabelingSchemeWrapper.class);
    }

    public Integer getChildLabelingSchemeId() {
        return getProperty(getChildLabelingScheme(),
            ContainerLabelingSchemePeer.ID);
    }

    public String getChildLabelingSchemeName() {
        return getProperty(getChildLabelingScheme(),
            ContainerLabelingSchemePeer.NAME);
    }

    // TODO: stopped here

    public void setChildLabelingScheme(Integer id) throws ApplicationException {
        setWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME,
            ContainerLabelingSchemeWrapper
                .getLabelingSchemeById(appService, id));
    }

    public void setChildLabelingSchemeName(String name) throws Exception {
        if (name == null) {
            throw new Exception("name is null");
        }

        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService).values()) {
            if (scheme.getName().equals(name)) {
                setChildLabelingScheme(scheme);
                return;
            }
        }
        throw new Exception("labeling scheme with name \"" + name
            + "\" does not exist");
    }

    private void setChildLabelingScheme(ContainerLabelingSchemeWrapper scheme) {
        setWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME, scheme);
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
        if (((getTopLevel() == null && oldObject.getTopLevel() != null)
            || (getTopLevel() != null && oldObject.getTopLevel() == null) || (getTopLevel() != null
            && oldObject.getTopLevel() != null && !getTopLevel().equals(
            oldObject.getTopLevel())))
            && existsContainersWithType) {
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
        if (getChildLabelingSchemeId() == null
            && oldWrapper.getChildLabelingSchemeId() == null) {
            return;
        }
        if (getChildLabelingSchemeId() == null
            || oldWrapper.getChildLabelingSchemeId() == null
            || !getChildLabelingSchemeId().equals(
                oldWrapper.getChildLabelingSchemeId())
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
        String nameComparison = "name =";
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "lower(name) like";
            containerNameParameter = "%" + containerName.toLowerCase() + "%";
        }
        String query = "from " + ContainerType.class.getName()
            + " where site = ? and " + nameComparison + " ?";
        HQLCriteria criteria = new HQLCriteria(query,
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return transformToWrapperList(appService, containerTypes);
    }

    /**
     * Get containers types with the given capacity in the given site. The
     * container types returned are ones that can only hold aliquots.
     */
    public static List<ContainerTypeWrapper> getContainerTypesByCapacity(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        int maxRows, int maxCols) throws ApplicationException {
        String query = "select ct from "
            + ContainerType.class.getName()
            + " as ct join ct.capacity as cap"
            + " where ct.site = ? and cap.rowCapacity = ?"
            + " and cap.colCapacity = ? and ct.sampleTypeCollection is not empty"
            + " and ct.childContainerTypeCollection is empty";
        HQLCriteria criteria = new HQLCriteria(query,
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                maxRows, maxCols }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return transformToWrapperList(appService, containerTypes);
    }

    public static List<ContainerTypeWrapper> getContainerTypesPallet96(
        WritableApplicationService appService, SiteWrapper siteWrapper)
        throws ApplicationException {
        return getContainerTypesByCapacity(appService, siteWrapper,
            RowColPos.PALLET_96_ROW_MAX, RowColPos.PALLET_96_COL_MAX);
    }

    /**
     * get count of container which type is this
     */
    public long getContainersCount() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + Container.class.getName() + " where containerType.id=?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
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
    protected void resetInternalFields() {
        deletedChildTypes.clear();
        deletedSampleTypes.clear();
    }

    public String getPositionString(RowColPos position) {
        return ContainerLabelingSchemeWrapper.getPositionString(position,
            getChildLabelingSchemeId(), getRowCapacity(), getColCapacity());
    }

    public RowColPos getRowColFromPositionString(String position)
        throws Exception {
        return ContainerLabelingSchemeWrapper.getRowColFromPositionString(
            getAppService(), position, getChildLabelingSchemeId(),
            getRowCapacity(), getColCapacity());
    }

    @Override
    public SiteWrapper getSiteLinkedToObject() {
        return getSite();
    }

    @Override
    public boolean checkSpecificAccess(User user, Integer siteId) {
        return user.isSiteAdministrator(siteId);
    }
}
