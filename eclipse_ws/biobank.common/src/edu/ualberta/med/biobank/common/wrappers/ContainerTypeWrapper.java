package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerLabelingSchemePeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerTypeBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeWrapper extends ContainerTypeBaseWrapper {

    private Set<ContainerTypeWrapper> deletedChildTypes = new HashSet<ContainerTypeWrapper>();

    private Set<SpecimenTypeWrapper> deletedSpecimenTypes = new HashSet<SpecimenTypeWrapper>();

    public static final List<String> PROP_NAMES;
    static {
        List<String> aList = new ArrayList<String>();
        aList.addAll(ContainerTypePeer.PROP_NAMES);
        aList.addAll(CapacityPeer.PROP_NAMES);
        PROP_NAMES = Collections.unmodifiableList(aList);
    };

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        List<String> all = new ArrayList<String>(super.getPropertyChangeNames());
        all.addAll(CapacityPeer.PROP_NAMES);
        return all;
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
            checkDeletedSpecimenTypes();
        }
    }

    private static final String DELETED_SPECIMEN_TYPES_BASE_QRY = "from "
        + SpecimenPosition.class.getName()
        + " as ap inner join ap."
        + SpecimenPositionPeer.CONTAINER.getName()
        + " as aparent where aparent."
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.ID)
        + "=? and ap."
        + Property.concatNames(SpecimenPositionPeer.SPECIMEN,
            SpecimenPeer.SPECIMEN_TYPE, SpecimenTypePeer.ID)
        + " in (select id from " + SpecimenType.class.getName()
        + " as st where st." + SpecimenTypePeer.ID.getName() + " in (";

    private void checkDeletedSpecimenTypes() throws ApplicationException,
        BiobankCheckException {
        if (deletedSpecimenTypes.size() == 0)
            return;

        List<String> ids = new ArrayList<String>();
        for (SpecimenTypeWrapper type : deletedSpecimenTypes) {
            ids.add(Integer.toString(type.getId()));
        }
        StringBuilder sb = new StringBuilder(DELETED_SPECIMEN_TYPES_BASE_QRY)
            .append(StringUtils.join(ids, ',')).append("))");
        List<Object> results = appService.query(new HQLCriteria(sb.toString(),
            Arrays.asList(new Object[] { getId() })));
        if (results.size() != 0) {
            throw new BiobankCheckException(
                "Unable to remove sample type. This parent/child relationship "
                    + "exists in database. Remove all instances before attempting to "
                    + "delete a sample type.");
        }
    }

    private static final String DELETED_CHILD_CONTAINER_TYPES = "from "
        + ContainerPosition.class.getName()
        + " as cp inner join cp."
        + ContainerPositionPeer.PARENT_CONTAINER.getName()
        + " as cparent where cparent."
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.ID)
        + "=? and cp."
        + Property.concatNames(ContainerPositionPeer.CONTAINER,
            ContainerPeer.CONTAINER_TYPE, ContainerTypePeer.ID)
        + " in (select id from " + ContainerType.class.getName()
        + " as ct where ct." + ContainerTypePeer.ID.getName() + " in (";

    private void checkDeletedChildContainerTypes()
        throws BiobankCheckException, ApplicationException {
        if (deletedChildTypes.size() > 0) {
            List<Integer> ids = new ArrayList<Integer>();
            for (ContainerTypeWrapper type : deletedChildTypes) {
                ids.add(type.getId());
            }
            StringBuilder sb = new StringBuilder(DELETED_CHILD_CONTAINER_TYPES)
                .append(StringUtils.join(ids, ',')).append("))");
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
            parent.removeFromChildContainerTypeCollection(Arrays.asList(this));
            parent.persist();
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedByContainers()) {
            throw new BiobankDeleteException("Unable to delete container type "
                + getName() + ". A container of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    private static final String IS_USED_BY_CONTAINERS_QRY = "select count(c) from "
        + Container.class.getName()
        + " as c where c."
        + ContainerPeer.CONTAINER_TYPE.getName() + "=?";

    public boolean isUsedByContainers() throws ApplicationException,
        BiobankQueryResultSizeException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_CONTAINERS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
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
        return wrapModelCollection(appService, results,
            ContainerTypeWrapper.class);
    }

    @Override
    public void addToSpecimenTypeCollection(
        List<SpecimenTypeWrapper> newSpecimenTypes) {
        super.addToSpecimenTypeCollection(newSpecimenTypes);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedSpecimenTypes.removeAll(newSpecimenTypes);
    }

    @Override
    public void removeFromSpecimenTypeCollection(
        List<SpecimenTypeWrapper> typesToRemove) {
        deletedSpecimenTypes.addAll(typesToRemove);
        super.removeFromSpecimenTypeCollection(typesToRemove);
    }

    public List<SpecimenTypeWrapper> getSpecimenTypeCollection() {
        return getSpecimenTypeCollection(true);
    }

    public Set<SpecimenTypeWrapper> getSpecimenTypesRecursively()
        throws ApplicationException {
        Set<SpecimenTypeWrapper> SpecimenTypes = new HashSet<SpecimenTypeWrapper>();
        List<SpecimenTypeWrapper> sampleSubSet = getSpecimenTypeCollection(false);
        if (sampleSubSet != null)
            SpecimenTypes.addAll(sampleSubSet);
        for (ContainerTypeWrapper type : getChildContainerTypeCollection()) {
            SpecimenTypes.addAll(type.getSpecimenTypesRecursively());
        }
        return SpecimenTypes;
    }

    @Override
    public void addToChildContainerTypeCollection(
        List<ContainerTypeWrapper> newContainerTypes) {
        super.addToChildContainerTypeCollection(newContainerTypes);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedChildTypes.removeAll(newContainerTypes);
    }

    @Override
    public void removeFromChildContainerTypeCollection(
        List<ContainerTypeWrapper> typesToRemove) {
        deletedChildTypes.addAll(typesToRemove);
        super.removeFromChildContainerTypeCollection(typesToRemove);
    }

    public List<ContainerTypeWrapper> getChildContainerTypeCollection() {
        return getChildContainerTypeCollection(true);
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
        initCapacity().setProperty(CapacityPeer.ROW_CAPACITY, maxRows);
    }

    public void setColCapacity(Integer maxCols) {
        initCapacity().setProperty(CapacityPeer.COL_CAPACITY, maxCols);
    }

    public Integer getChildLabelingSchemeId() {
        return getProperty(getChildLabelingScheme(),
            ContainerLabelingSchemePeer.ID);
    }

    public void setChildLabelingSchemeById(Integer id) throws Exception {
        ContainerLabelingSchemeWrapper scheme = ContainerLabelingSchemeWrapper
            .getLabelingSchemeById(appService, id);
        if (scheme == null) {
            throw new Exception("labeling scheme with id \"" + id
                + "\" does not exist");
        }
        setChildLabelingScheme(scheme);
    }

    public String getChildLabelingSchemeName() {
        return getProperty(getChildLabelingScheme(),
            ContainerLabelingSchemePeer.NAME);
    }

    public void setChildLabelingSchemeName(String name) throws Exception {
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

    private static final String TOP_CONTAINERS_IN_SITE_QRY = "from "
        + ContainerType.class.getName() + " where "
        + Property.concatNames(ContainerTypePeer.SITE, SitePeer.ID) + "=? and "
        + ContainerTypePeer.TOP_LEVEL.getName() + "=true";

    public static List<ContainerTypeWrapper> getTopContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(TOP_CONTAINERS_IN_SITE_QRY,
            Arrays.asList(new Object[] { site.getId() }));
        List<ContainerType> types = appService.query(criteria);
        return wrapModelCollection(appService, types,
            ContainerTypeWrapper.class);
    }

    private static final String SITE_CONTAINER_TYPES_QRY = "from "
        + ContainerType.class.getName() + " where "
        + ContainerTypePeer.SITE.getName() + "=? and ";

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
            containerNameParameter = new StringBuilder("%")
                .append(containerName.toLowerCase()).append("%").toString();
        }
        StringBuilder query = new StringBuilder(SITE_CONTAINER_TYPES_QRY)
            .append(nameComparison).append(" ?");
        HQLCriteria criteria = new HQLCriteria(query.toString(),
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter.toString() }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return wrapModelCollection(appService, containerTypes,
            ContainerTypeWrapper.class);
    }

    private static final String CONTAINER_TYPES_BY_CAPACITY_QRY = "select ct from "
        + ContainerType.class.getName()
        + " as ct join ct."
        + ContainerTypePeer.CAPACITY.getName()
        + " as cap where ct."
        + ContainerTypePeer.SITE.getName()
        + "=? and cap."
        + CapacityPeer.ROW_CAPACITY.getName()
        + "=? and cap."
        + CapacityPeer.COL_CAPACITY.getName()
        + "=? and ct."
        + ContainerTypePeer.SPECIMEN_TYPE_COLLECTION.getName()
        + " is not empty and ct."
        + ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION.getName()
        + " is empty";

    /**
     * Get containers types with the given capacity in the given site. The
     * container types returned are ones that can only hold specimens.
     */
    public static List<ContainerTypeWrapper> getContainerTypesByCapacity(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        int maxRows, int maxCols) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(CONTAINER_TYPES_BY_CAPACITY_QRY,
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                maxRows, maxCols }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return wrapModelCollection(appService, containerTypes,
            ContainerTypeWrapper.class);
    }

    public static List<ContainerTypeWrapper> getContainerTypesPallet96(
        WritableApplicationService appService, SiteWrapper siteWrapper)
        throws ApplicationException {
        return getContainerTypesByCapacity(appService, siteWrapper,
            RowColPos.PALLET_96_ROW_MAX, RowColPos.PALLET_96_COL_MAX);
    }

    private static final String CONTAINER_COUNT_QRY = "select count(*) from "
        + Container.class.getName() + " where containerType.id=?";

    /**
     * get count of container which type is this
     */
    public long getContainersCount() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria(CONTAINER_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, c);
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
        deletedSpecimenTypes.clear();
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

}
