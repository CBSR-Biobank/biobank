package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerLabelingSchemePeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerTypeBaseWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeWrapper extends ContainerTypeBaseWrapper {
    public static final Property<Integer, ContainerType> ROW_CAPACITY =
        ContainerTypePeer.CAPACITY
            .wrap(CapacityPeer.ROW_CAPACITY);
    public static final Property<Integer, ContainerType> COL_CAPACITY =
        ContainerTypePeer.CAPACITY
            .wrap(CapacityPeer.COL_CAPACITY);

    public static final List<Property<?, ? super ContainerType>> PROPERTIES;
    static {
        List<Property<?, ? super ContainerType>> aList =
            new ArrayList<Property<?, ? super ContainerType>>();
        aList.addAll(ContainerTypePeer.PROPERTIES);
        aList.add(ROW_CAPACITY);
        aList.add(COL_CAPACITY);
        aList.add(ContainerTypePeer.CHILD_LABELING_SCHEME.wrap(
            "childLabelingSchemeName", ContainerLabelingSchemePeer.NAME)); //$NON-NLS-1$
        PROPERTIES = Collections.unmodifiableList(aList);
    };

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<Property<?, ? super ContainerType>> getProperties() {
        return PROPERTIES;
    }

    public Collection<ContainerTypeWrapper> getChildrenRecursively()
        throws ApplicationException {
        List<ContainerTypeWrapper> allChildren =
            new ArrayList<ContainerTypeWrapper>();
        List<ContainerTypeWrapper> children = getChildContainerTypeCollection();
        if (children != null) {
            for (ContainerTypeWrapper type : children) {
                allChildren.addAll(type.getChildrenRecursively());
                allChildren.add(type);
            }
        }
        return allChildren;
    }

    private static final String IS_USED_BY_CONTAINERS_QRY =
        "select count(c) from " //$NON-NLS-1$
            + Container.class.getName()
            + " as c where c." //$NON-NLS-1$
            + ContainerPeer.CONTAINER_TYPE.getName() + "=?"; //$NON-NLS-1$

    public boolean isUsedByContainers() throws ApplicationException,
        BiobankQueryResultSizeException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_CONTAINERS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    public List<ContainerTypeWrapper> getParentContainerTypeCollection() {
        return getParentContainerTypeCollection(false);
    }

    public List<SpecimenTypeWrapper> getSpecimenTypeCollection() {
        return getSpecimenTypeCollection(true);
    }

    public Set<SpecimenTypeWrapper> getSpecimenTypesRecursively()
        throws ApplicationException {
        Set<SpecimenTypeWrapper> SpecimenTypes =
            new HashSet<SpecimenTypeWrapper>();
        List<SpecimenTypeWrapper> sampleSubSet =
            getSpecimenTypeCollection(false);
        if (sampleSubSet != null)
            SpecimenTypes.addAll(sampleSubSet);
        for (ContainerTypeWrapper type : getChildContainerTypeCollection()) {
            SpecimenTypes.addAll(type.getSpecimenTypesRecursively());
        }
        return SpecimenTypes;
    }

    public List<ContainerTypeWrapper> getChildContainerTypeCollection() {
        return getChildContainerTypeCollection(true);
    }

    public Integer getRowCapacity() {
        return getCapacity().getRowCapacity();
    }

    public Integer getColCapacity() {
        return getCapacity().getColCapacity();
    }

    public void setRowCapacity(Integer maxRows) {
        getCapacity().setRowCapacity(maxRows);
    }

    public void setColCapacity(Integer maxCols) {
        getCapacity().setColCapacity(maxCols);
    }

    public Integer getChildLabelingSchemeId() {
        return getProperty(getChildLabelingScheme(),
            ContainerLabelingSchemePeer.ID);
    }

    public void setChildLabelingSchemeById(Integer id) throws Exception {
        ContainerLabelingSchemeWrapper scheme = ContainerLabelingSchemeWrapper
            .getLabelingSchemeById(appService, id);
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
        throw new Exception("labeling scheme with name \"" + name //$NON-NLS-1$
            + "\" does not exist"); //$NON-NLS-1$
    }

    private static final String TOP_CONTAINERS_IN_SITE_QRY = "from " //$NON-NLS-1$
        + ContainerType.class.getName() + " where " //$NON-NLS-1$
        + Property.concatNames(ContainerTypePeer.SITE, SitePeer.ID) + "=? and " //$NON-NLS-1$
        + ContainerTypePeer.TOP_LEVEL.getName() + "=true"; //$NON-NLS-1$

    public static List<ContainerTypeWrapper> getTopContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(TOP_CONTAINERS_IN_SITE_QRY,
            Arrays.asList(new Object[] { site.getId() }));
        List<ContainerType> types = appService.query(criteria);
        return wrapModelCollection(appService, types,
            ContainerTypeWrapper.class);
    }

    private static final String SITE_CONTAINER_TYPES_QRY = "from " //$NON-NLS-1$
        + ContainerType.class.getName() + " where " //$NON-NLS-1$
        + ContainerTypePeer.SITE.getName() + "=? and "; //$NON-NLS-1$

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * should contain containerName as a substring in the name.
     */
    public static List<ContainerTypeWrapper> getContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String containerName, boolean useStrictName)
        throws ApplicationException {
        String nameComparison = "name ="; //$NON-NLS-1$
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "lower(name) like"; //$NON-NLS-1$
            containerNameParameter = new StringBuilder("%") //$NON-NLS-1$
                .append(containerName.toLowerCase()).append("%").toString(); //$NON-NLS-1$
        }
        StringBuilder query = new StringBuilder(SITE_CONTAINER_TYPES_QRY)
            .append(nameComparison).append(" ?"); //$NON-NLS-1$
        HQLCriteria criteria = new HQLCriteria(query.toString(),
            Arrays.asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter.toString() }));
        List<ContainerType> containerTypes = appService.query(criteria);
        return wrapModelCollection(appService, containerTypes,
            ContainerTypeWrapper.class);
    }

    private static final String CONTAINER_TYPES_BY_CAPACITY_QRY =
        "select ct from " //$NON-NLS-1$
            + ContainerType.class.getName()
            + " as ct join ct." //$NON-NLS-1$
            + ContainerTypePeer.CAPACITY.getName()
            + " as cap where ct." //$NON-NLS-1$
            + ContainerTypePeer.SITE.getName()
            + "=? and cap." //$NON-NLS-1$
            + CapacityPeer.ROW_CAPACITY.getName()
            + "=? and cap." //$NON-NLS-1$
            + CapacityPeer.COL_CAPACITY.getName()
            + "=? and ct." //$NON-NLS-1$
            + ContainerTypePeer.SPECIMEN_TYPES.getName()
            + " is not empty and ct." //$NON-NLS-1$
            + ContainerTypePeer.CHILD_CONTAINER_TYPES.getName()
            + " is empty"; //$NON-NLS-1$

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

    private static final String CONTAINER_COUNT_QRY = "select count(*) from " //$NON-NLS-1$
        + Container.class.getName() + " where containerType.id=?"; //$NON-NLS-1$

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
        return getName() + " (" + getNameShort() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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

    public boolean isPallet96() {
        return RowColPos.PALLET_96_ROW_MAX.equals(getRowCapacity())
            && RowColPos.PALLET_96_COL_MAX.equals(getColCapacity());
    }

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.persist(this, ContainerTypePeer.CAPACITY);

        super.addPersistTasks(tasks);
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // When a ContainerType is deleted, remove it from all parent
        // ContainerType-s that use it and persist them. This was chosen to be
        // done because when a parent ContainerType is deleted, it is
        // automatically removed from all child ContainerType-s that use it.
        // Done for symmetrical behaviour.
        for (ContainerTypeWrapper parent : getParentContainerTypeCollection()) {
            parent.removeFromChildContainerTypeCollection(Arrays.asList(this));
            parent.addPersistTasks(tasks);
        }

        super.addDeleteTasks(tasks);
    }
}
