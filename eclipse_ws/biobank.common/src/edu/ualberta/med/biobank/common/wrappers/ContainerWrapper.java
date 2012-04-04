package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankRuntimeException;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.actions.UpdateContainerChildrenAction;
import edu.ualberta.med.biobank.common.wrappers.actions.UpdateContainerPathAction;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.tasks.NoActionWrapperQueryTask;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends ContainerBaseWrapper {
    public static final String PATH_DELIMITER = "/";

    private static final String CHILD_POSITION_CONFLICT_MSG =
        "Position {0} of container {1} already contains container {2} when trying to add container {3}.";
    private static final String OUT_OF_BOUNDS_POSITION_MSG =
        "Position {0} is invalid. Row should be between 0 and {1} (exclusive) and column should be between 0 and {2} (exclusive).";
    private static final String CANNOT_HOLD_SPECIMEN_TYPE_MSG =
        "Container {0} does not allow inserts of type {1}.";
    private static final String SAMPLE_EXISTS_AT_POSITION_MSG =
        "Container {0} is already holding an specimen at position {1} {2}";
    private static final String CONTAINER_AT_POSITION_MSG =
        "Container {0} is already holding a container {1} at position {2}.";

    private static final Collection<Property<?, ? super Container>> UNIQUE_LABEL_PROPS,
        UNIQUE_BARCODE_PROPS;

    static {
        UNIQUE_LABEL_PROPS = new ArrayList<Property<?, ? super Container>>();
        UNIQUE_LABEL_PROPS.add(ContainerPeer.SITE.to(SitePeer.ID));
        UNIQUE_LABEL_PROPS.add(ContainerPeer.LABEL);
        UNIQUE_LABEL_PROPS.add(ContainerPeer.CONTAINER_TYPE
            .to(ContainerTypePeer.ID));

        UNIQUE_BARCODE_PROPS = new ArrayList<Property<?, ? super Container>>();
        UNIQUE_BARCODE_PROPS.add(ContainerPeer.SITE.to(SitePeer.ID));
        UNIQUE_BARCODE_PROPS.add(ContainerPeer.PRODUCT_BARCODE);
    }

    private Map<RowColPos, SpecimenWrapper> specimens;
    private Map<RowColPos, ContainerWrapper> children;

    private boolean updateChildren = false;

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected Container getNewObject() throws Exception {
        Container newObject = super.getNewObject();
        // by default, any newly created Container will have a null parent, so
        // its top is itself.
        newObject.setTopContainer(newObject);
        return newObject;
    }

    /**
     * Return the top {@code Container} of the top loaded {@code Container}.
     * This will give the correct "in memory" answer of who the top
     * {@code Container} is (whereas super.getTopContainer() will give the value
     * from the underlying model).
     */
    @Override
    public ContainerWrapper getTopContainer() {
        // if parent is cached, return their top Container, otherwise get and
        // return mine (from super).
        if (isPropertyCached(ContainerPeer.POSITION) && getPosition() != null) {
            if (getPosition().isPropertyCached(
                ContainerPositionPeer.PARENT_CONTAINER)
                && getParentContainer() != null) {
                return getParentContainer().getTopContainer();
            }
        }
        return super.getTopContainer();
    }

    /**
     * @return the path, including this {@link Container}'s id.
     * @throws BiobankRuntimeException if this or any parent is new (does not
     *             have an id) as the path is then undefined.
     */
    @Override
    public String getPath() {
        if (isNew()) {
            throw new BiobankRuntimeException(
                "container is not in database yet: no ID");
        }

        String parentPath = "";

        if (isPropertyCached(ContainerPeer.POSITION) && getPosition() != null) {
            if (getPosition().isPropertyCached(
                ContainerPositionPeer.PARENT_CONTAINER)
                && getParentContainer() != null) {
                parentPath = getParentContainer().getPath();
            }
        } else {
            // the persisted path is actually the parent path, although this
            // method returns the parent path plus its id.
            parentPath = super.getPath();
            if (parentPath == null) {
                parentPath = "";
            }
        }

        if (!parentPath.isEmpty()) {
            parentPath += PATH_DELIMITER;
        }

        return parentPath + getId();
    }

    @Override
    public String getLabel() {
        if (isPropertyCached(ContainerPeer.POSITION) && getPosition() != null) {
            if (getPosition().isPropertyCached(
                ContainerPositionPeer.PARENT_CONTAINER)
                && getParentContainer() != null) {
                return getParentContainer().getLabel() + getPositionString();
            }
        }

        return super.getLabel();
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);

        updateChildren = true;
    }

    public String getPositionString() {
        ContainerWrapper parent = getParentContainer();
        if (parent != null) {
            RowColPos pos = getPositionAsRowCol();
            if (pos != null) {
                return parent.getContainerType().getPositionString(pos);
            }
        }
        return null;
    }

    public RowColPos getPositionAsRowCol() {
        ContainerPositionWrapper pos = getPosition();
        return pos == null ? null : pos.getPosition();
    }

    private ContainerPositionWrapper getOrCreatePosition() {
        ContainerPositionWrapper position = getPosition();
        if (position == null) {
            position = new ContainerPositionWrapper(appService);
            setPosition(position);
        }
        return position;
    }

    public ContainerWrapper getParentContainer() {
        AbstractPositionWrapper<?> pos = getPosition();
        return pos == null ? null : pos.getParent();
    }

    @Override
    @Deprecated
    public void setPath(String dummy) {
        throw new BiobankRuntimeException("cannot set path on container");
    }

    /**
     * This sets the parent bidirectionally. If this is not required then use
     * setParentInternal().
     */
    public void setParent(ContainerWrapper container, RowColPos position)
        throws BiobankCheckException {
        if (container != null) {
            container.addChild(position.getRow(), position.getCol(), this);
        } else {
            setParentInternal(null, position);
        }
    }

    /**
     * KLUDGE ALERT: this method is called by ContainerViewForm.openFormFor().
     */
    public void setParentInternal(ContainerWrapper container, RowColPos position) {
        if (container == null) {
            setPosition(null);
        } else {
            getOrCreatePosition().setParent(container, position);
        }

        ContainerWrapper topContainer = container == null ? this : container
            .getTopContainer();
        setTopContainerInternal(topContainer, true);
    }

    @Override
    @Deprecated
    public void setTopContainer(ContainerBaseWrapper container) {
        throw new UnsupportedOperationException(
            "Not allowed to directly set the top Container. Set the parent Container instead.");
    }

    public void setTopContainerInternal(ContainerWrapper container,
        boolean checkDatabase) {
        super.setTopContainer(container);

        // this is overly cautious, assuming that whenever the top Container is
        // set that it is changed. Could be improved to check if the value has
        // actually changed, but would probably require lazy loading.

        if (!isNew() && checkDatabase) {
            // TODO: actually check the database. Get the current
            // topSpecimen through an HQL query and compare it against the
            // one set.
            updateChildren = true;
            // TODO: may want to set to false if set back to the original?
        } else {
            updateChildren = true;
        }
    }

    public boolean hasParentContainer() {
        return getParentContainer() != null;
    }

    public Integer getRowCapacity() {
        ContainerTypeWrapper type = getContainerType();
        return type == null ? null : type.getRowCapacity();
    }

    public Integer getColCapacity() {
        ContainerTypeWrapper type = getContainerType();
        return type == null ? null : type.getColCapacity();
    }

    /**
     * position is 2 letters, or 2 number or 1 letter and 1 number... this
     * position string is used to get the correct row and column index the given
     * position String.
     * 
     * @throws Exception
     */
    public RowColPos getPositionFromLabelingScheme(String position)
        throws Exception {
        ContainerTypeWrapper type = getContainerType();
        RowColPos rcp = type.getRowColFromPositionString(position);
        if (rcp != null) {
            if (rcp.getRow() >= type.getRowCapacity()
                || rcp.getCol() >= type.getColCapacity()) {
                throw new Exception(
                    MessageFormat
                        .format(
                            "Can''t use position {0} in container {1}. Reason: capacity = {2}*{3}",
                            position, getFullInfoLabel(),
                            type.getRowCapacity(), type.getColCapacity()));
            }
            if (rcp.getRow() < 0 || rcp.getCol() < 0) {
                throw new Exception(
                    MessageFormat.format(
                        "Position ''{0}'' is invalid for this container {1}",
                        position, getFullInfoLabel()));
            }
        }
        return rcp;
    }

    public Map<RowColPos, SpecimenWrapper> getSpecimens() {
        if (specimens == null) {
            Map<RowColPos, SpecimenWrapper> specimens =
                new TreeMap<RowColPos, SpecimenWrapper>();

            List<SpecimenPositionWrapper> positions =
                getSpecimenPositionCollection(false);
            for (SpecimenPositionWrapper position : positions) {
                SpecimenWrapper specimen = position.getSpecimen();
                RowColPos rowColPos = getRowColPos(position);
                specimens.put(rowColPos, specimen);
            }

            this.specimens = specimens;
        }

        return specimens;
    }

    public boolean hasSpecimens() {
        return !getSpecimens().isEmpty();
    }

    public SpecimenWrapper getSpecimen(Integer row, Integer col)
        throws BiobankCheckException {
        RowColPos pos = new RowColPos(row, col);
        checkPositionValid(pos);
        return getSpecimens().get(pos);
    }

    public void addSpecimen(Integer row, Integer col, SpecimenWrapper specimen)
        throws Exception {
        RowColPos rowColPos = new RowColPos(row, col);
        checkPositionValid(rowColPos);

        if (!canHoldSpecimenType(specimen)) {
            String label = getFullInfoLabel();
            String specimenType = specimen.getSpecimenType().getName();
            String msg = MessageFormat.format(CANNOT_HOLD_SPECIMEN_TYPE_MSG,
                label, specimenType);
            throw new BiobankCheckException(msg);
        }

        SpecimenWrapper sampleAtPosition = getSpecimen(row, col);
        if (sampleAtPosition != null) {
            String label = getFullInfoLabel();
            String posString = sampleAtPosition.getPositionString(false, false);
            String msg = MessageFormat.format(SAMPLE_EXISTS_AT_POSITION_MSG,
                label, posString, rowColPos);
            throw new BiobankCheckException(msg);
        }

        specimen.setParent(this, rowColPos);

        getSpecimens().put(rowColPos, specimen);
    }

    /**
     * @return a string with the label of this container + the short name of its
     *         type
     * 
     */
    public String getFullInfoLabel() {
        if (getContainerType() == null
            || getContainerType().getNameShort() == null) {
            return getLabel();
        }
        return getLabel() + " (" + getContainerType().getNameShort() + ")";
    }

    public long getChildCount(boolean fast) throws BiobankException,
        ApplicationException {
        return getPropertyCount(ContainerPeer.CHILD_POSITIONS, fast);
    }

    public Map<RowColPos, ContainerWrapper> getChildren() {
        if (children == null) {
            Map<RowColPos, ContainerWrapper> children =
                new TreeMap<RowColPos, ContainerWrapper>();

            List<ContainerPositionWrapper> positions =
                getChildPositionCollection(false);
            for (ContainerPositionWrapper position : positions) {
                // explicitly set the parent container because (1) skip
                // lazy-loading later and (2) will put the parentContainer
                // property in the cache so it is used (see methods getPath(),
                // getLabel(), and getTopContainer() where recursion is used).
                // If no wrapper has been loaded, then the model value is used,
                // which can be inconsistent.
                //
                // setWrappedProperty() is used because it does not
                // bi-directionally set the property.
                position.setWrappedProperty(
                    ContainerPositionPeer.PARENT_CONTAINER, this);

                ContainerWrapper container = position.getContainer();
                RowColPos rowColPos = getRowColPos(position);

                ContainerWrapper previous = children.put(rowColPos, container);
                if (previous != null && !previous.equals(container)) {
                    // this shouldn't ever happen, but just in case
                    String msg = MessageFormat.format(
                        CHILD_POSITION_CONFLICT_MSG, rowColPos, this, previous,
                        container);
                    throw new BiobankRuntimeException(msg);
                }
            }

            this.children = children;
        }

        return children;
    }

    private static RowColPos getRowColPos(AbstractPositionWrapper<?> pos) {
        return new RowColPos(pos.getRow(), pos.getCol());
    }

    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    public ContainerWrapper getChild(Integer row, Integer col) {
        return getChild(new RowColPos(row, col));
    }

    public ContainerWrapper getChild(RowColPos rcp) {
        return getChildren().get(rcp);
    }

    /**
     * Label can start with parent's label as prefix or without.
     * 
     * @param label
     * @return
     * @throws Exception
     */
    public ContainerWrapper getChildByLabel(String label) throws Exception {
        ContainerTypeWrapper containerType = getContainerType();
        if (containerType == null) {
            throw new Exception("container type is null");
        }
        if (label.startsWith(getLabel())) {
            label = label.substring(getLabel().length());
        }
        RowColPos pos = getPositionFromLabelingScheme(label);
        return getChild(pos);
    }

    public void addChild(Integer row, Integer col, ContainerWrapper child)
        throws BiobankCheckException {
        RowColPos rowColPos = new RowColPos(row, col);
        checkPositionValid(rowColPos);

        ContainerWrapper containerAtPosition = getChild(rowColPos);
        if (containerAtPosition != null && !containerAtPosition.equals(child)) {
            String label = getFullInfoLabel();
            String existingContainerLabel = containerAtPosition.getLabel();
            String msg = MessageFormat.format(CONTAINER_AT_POSITION_MSG, label,
                existingContainerLabel, rowColPos);
            throw new BiobankCheckException(msg);
        }

        child.setParentInternal(this, rowColPos);

        getChildren().put(rowColPos, child);
    }

    /**
     * Add a child in this container
     * 
     * @param positionString position where the child should be added. e.g. AA
     *            or B12 or 15
     * @param child
     * @throws Exception
     */
    public void addChild(String positionString, ContainerWrapper child)
        throws Exception {
        RowColPos rowColPos = getPositionFromLabelingScheme(positionString);
        addChild(rowColPos.getRow(), rowColPos.getCol(), child);
    }

    /**
     * Return true if this container can hold the type of sample
     * 
     * @throws Exception if the sample type is null.
     */
    public boolean canHoldSpecimenType(SpecimenWrapper specimen)
        throws Exception {
        SpecimenTypeWrapper type = specimen.getSpecimenType();
        if (type == null) {
            throw new BiobankCheckException("specimen type is null");
        }
        return getContainerType().getSpecimenTypeCollection(false).contains(
            type);
    }

    @Deprecated
    public void moveSpecimens(ContainerWrapper destination) throws Exception {
        Map<RowColPos, SpecimenWrapper> aliquots = getSpecimens();
        for (Entry<RowColPos, SpecimenWrapper> e : aliquots.entrySet()) {
            destination.addSpecimen(e.getKey().getRow(), e.getKey().getCol(),
                e.getValue());
        }
        destination.persist();
    }

    /**
     * Get containers with a given label that can hold this type of container
     * (in this container site)
     * 
     * @throws BiobankException
     */
    public List<ContainerWrapper> getPossibleParents(String childLabel)
        throws ApplicationException {
        return getPossibleParents(appService, childLabel, getSite(),
            getContainerType());
    }

    private static final String POSSIBLE_PARENTS_BASE_QRY =
        "select distinct(c) from "
            + Container.class.getName()
            + " as c left join c."
            + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
                ContainerTypePeer.CHILD_CONTAINER_TYPES)
            + " as ct where c."
            + ContainerPeer.SITE.getName() + "=? and c."
            + ContainerPeer.LABEL.getName() + " in (";

    /**
     * Get containers with a given label that can have a child (container or
     * specimen) with label 'childLabel'. If child is not null and is a
     * container, then will check that the parent can contain this type of
     * container
     * 
     * @param type if the child is a container, this is its type (if available)
     * @throws BiobankException
     */
    public static List<ContainerWrapper> getPossibleParents(
        WritableApplicationService appService, String childLabel,
        SiteWrapper site, ContainerTypeWrapper type)
        throws ApplicationException {
        List<Integer> validLengths = ContainerLabelingSchemeWrapper
            .getPossibleLabelLength(appService);
        List<String> validParents = new ArrayList<String>();

        for (Integer crop : validLengths)
            if (crop < childLabel.length())
                validParents
                    .add(new StringBuilder("'")
                        .append(
                            childLabel.substring(0, childLabel.length() - crop))
                        .append("'").toString());

        List<ContainerWrapper> filteredWrappers =
            new ArrayList<ContainerWrapper>();
        if (validParents.size() > 0) {
            List<Object> params = new ArrayList<Object>();
            params.add(site.getWrappedObject());
            StringBuilder parentQuery = new StringBuilder(
                POSSIBLE_PARENTS_BASE_QRY).append(
                StringUtil.join(validParents, ",")).append(")");
            if (type != null) {
                parentQuery.append(" and ct.id=?");
                params.add(type.getId());
            }
            HQLCriteria criteria = new HQLCriteria(parentQuery.toString(),
                params);
            List<Container> containers = appService.query(criteria);
            for (Container c : containers) {
                ContainerTypeWrapper ct = new ContainerTypeWrapper(appService,
                    c.getContainerType());
                try {
                    if (ct.getRowColFromPositionString(childLabel.substring(c
                        .getLabel().length())) != null)
                        filteredWrappers
                            .add(new ContainerWrapper(appService, c));
                } catch (Exception e) {
                    // can't throw an exception: it means that this label is not
                    // possible in this parent.
                    // Maybe the next one in the list is ok
                }
            }
        }
        return filteredWrappers;
    }

    @SuppressWarnings("nls")
    private static final String EMPTY_CONTAINERS_HOLDING_SPECIMEN_TYPE_BASE_QRY =
        "from "
            + Container.class.getName()
            + " where "
            + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
            + "=? and "
            + ContainerPeer.SPECIMEN_POSITIONS.getName()
            + ".size = 0 and "
            + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
                ContainerTypePeer.CAPACITY, CapacityPeer.ROW_CAPACITY)
            + " >= ? and "
            + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
                ContainerTypePeer.CAPACITY, CapacityPeer.COL_CAPACITY)
            + " >= ? and "
            + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
                ContainerTypePeer.ID)
            + " in (select ct."
            + ContainerTypePeer.ID.getName()
            + " from "
            + ContainerType.class.getName()
            + " as ct left join ct."
            + ContainerTypePeer.SPECIMEN_TYPES.getName()
            + " as sampleType where sampleType."
            + SpecimenTypePeer.ID.getName() + " in (";

    /**
     * Retrieve a list of empty containers in a specific site. These containers
     * should be able to hold specimens of type specimen type and should have a
     * row capacity equals or greater than minRwCapacity and a column capacity
     * equal or greater than minColCapacity.
     * 
     * @param appService
     * @param siteWrapper
     * @param sampleTypes list of sample types the container should be able to
     *            contain
     * @param minRowCapacity min row capacity
     * @param minColCapacity min col capacity
     */
    public static List<ContainerWrapper> getEmptyContainersHoldingSpecimenType(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        List<SpecimenTypeWrapper> sampleTypes, Integer minRowCapacity,
        Integer minColCapacity) throws ApplicationException {
        List<Integer> typeIds = new ArrayList<Integer>();
        for (int i = 0; i < sampleTypes.size(); i++) {
            SpecimenTypeWrapper st = sampleTypes.get(i);
            typeIds.add(st.getId());
        }
        String qry = new StringBuilder(
            EMPTY_CONTAINERS_HOLDING_SPECIMEN_TYPE_BASE_QRY)
            .append(StringUtil.join(typeIds, ",")).append("))").toString();
        HQLCriteria criteria = new HQLCriteria(qry, Arrays.asList(new Object[] {
            siteWrapper.getId(), minRowCapacity, minColCapacity }));
        List<Container> containers = appService.query(criteria);
        return wrapModelCollection(appService, containers,
            ContainerWrapper.class);
    }

    private static final String CONTAINERS_IN_SITE_QRY = "from "
        + Container.class.getName() + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID) + "=? and "
        + ContainerPeer.LABEL.getName() + "=?";

    /**
     * Get all containers form a given site with a given label
     */
    public static List<ContainerWrapper> getContainersInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String label) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(CONTAINERS_IN_SITE_QRY,
            Arrays.asList(new Object[] { siteWrapper.getId(), label }));
        List<Container> containers = appService.query(criteria);
        return wrapModelCollection(appService, containers,
            ContainerWrapper.class);
    }

    private static final String CONTAINERS_BY_LABEL = "from "
        + Container.class.getName() + " where " + ContainerPeer.LABEL.getName()
        + "=?";

    /**
     * Get all containers with a given label
     */
    public static List<ContainerWrapper> getContainersByLabel(
        WritableApplicationService appService, String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(CONTAINERS_BY_LABEL,
            Arrays.asList(new Object[] { label }));
        List<Container> containers = appService.query(criteria);
        return wrapModelCollection(appService, containers,
            ContainerWrapper.class);
    }

    private static final String CONTAINER_WITH_PRODUCT_BARCODE_IN_SITE_QRY =
        "from "
            + Container.class.getName() + " where "
            + Property.concatNames(ContainerPeer.SITE, SitePeer.ID) + "=? and "
            + ContainerPeer.PRODUCT_BARCODE.getName() + "=?";

    /**
     * Get the container with the given productBarcode in a site
     */
    public static ContainerWrapper getContainerWithProductBarcodeInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String productBarcode) throws Exception {
        HQLCriteria criteria =
            new HQLCriteria(
                CONTAINER_WITH_PRODUCT_BARCODE_IN_SITE_QRY,
                Arrays.asList(new Object[] { siteWrapper.getId(),
                    productBarcode }));
        List<Container> containers = appService.query(criteria);
        if (containers.size() == 0) {
            return null;
        } else if (containers.size() > 1) {
            throw new Exception(
                MessageFormat
                    .format(
                        "Multiples containers registered with product barcode {0}.",
                        productBarcode));
        }
        return new ContainerWrapper(appService, containers.get(0));
    }

    /**
     * Initialise children at given position with the given type. If the
     * positions list is null, initialise all the children. <strong>If a
     * position is already filled then it is skipped and no changes are made to
     * it</strong>.
     * 
     * @return true if at least one children has been initialised
     * @throws BiobankCheckException
     * @throws WrapperException
     * @throws ApplicationException
     */
    public void initChildrenWithType(ContainerTypeWrapper type,
        Set<RowColPos> positions) throws Exception {
        if (positions == null) {
            for (int i = 0; i < getContainerType().getRowCapacity().intValue(); i++) {
                for (int j = 0; j < getContainerType().getColCapacity()
                    .intValue(); j++) {
                    initPositionIfEmpty(type, i, j);
                }
            }
        } else {
            for (RowColPos rcp : positions) {
                initPositionIfEmpty(type, rcp.getRow(), rcp.getCol());
            }
        }
        reload();
    }

    @Deprecated
    private void initPositionIfEmpty(ContainerTypeWrapper type, int i, int j)
        throws Exception {
        if (type == null) {
            throw new Exception(
                "Error initializing container. That is not a valid container type.");
        }
        Boolean filled = (getChild(i, j) != null);
        if (!filled) {
            ContainerWrapper newContainer = new ContainerWrapper(appService);
            newContainer.setContainerType(type);
            newContainer.setSite(getSite());
            newContainer.setParent(this, new RowColPos(i, j));
            newContainer.setActivityStatus(ActivityStatus.ACTIVE);
            newContainer.persist();
        }
    }

    /**
     * Delete the children at positions of this container with the given type
     * (or all if positions list is null)- If type== null, delete all types.
     * 
     * @return true if at least one children has been deleted
     * @throws Exception
     * @throws BiobankCheckException
     */
    public boolean deleteChildrenWithType(ContainerTypeWrapper type,
        Set<RowColPos> positions) throws BiobankCheckException, Exception {
        boolean oneChildrenDeleted = false;
        if (positions == null) {
            for (ContainerWrapper child : getChildren().values()) {
                oneChildrenDeleted = deleteChild(type, child);
            }
        } else {
            for (RowColPos rcp : positions) {
                ContainerWrapper child = getChild(rcp);
                if (child != null) {
                    oneChildrenDeleted = deleteChild(type, child);
                }
            }
        }
        // TODO: instead of reloading, remove children after they're deleted?
        // TODO: delete as a transaction instead of individually?
        reload();
        return oneChildrenDeleted;
    }

    @Deprecated
    private boolean deleteChild(ContainerTypeWrapper type,
        ContainerWrapper child) throws Exception {
        if (type == null || child.getContainerType().equals(type)) {
            child.delete();
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(ModelWrapper<Container> wrapper) {
        if (wrapper instanceof ContainerWrapper) {
            String c1Label = wrappedObject.getLabel();
            String c2Label = wrapper.wrappedObject.getLabel();
            return c1Label.compareTo(c2Label);
        }
        return 0;
    }

    // @Override
    // public String toString() {
    //        return getLabel() + " (" + getProductBarcode() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    // }

    @Override
    protected void resetInternalFields() {
        specimens = null;
        children = null;
        updateChildren = false;
    }

    /**
     * @return true if there is no free position for a new child container
     * @throws ApplicationException
     * @throws BiobankCheckException
     */
    public boolean isContainerFull() throws BiobankException,
        ApplicationException {
        return (this.getChildCount(true) == this.getContainerType()
            .getRowCapacity() * this.getContainerType().getColCapacity());
    }

    /**
     * Search possible parents from the position text.
     * 
     * @param positionText the position to use for initialisation
     * @param isContainerPosition if true, the position is a full container
     *            position, if false, it is a full specimen position
     * @param contType if is a container position, will check the type can be
     *            used
     * @throws BiobankException
     */
    @SuppressWarnings({ "null", "unused" })
    public static List<ContainerWrapper> getPossibleContainersFromPosition(
        BiobankApplicationService appService, String positionText,
        boolean isContainerPosition, ContainerTypeWrapper contType)
        throws ApplicationException, BiobankException {
        List<ContainerWrapper> foundContainers;
        List<ContainerWrapper> possibles = getPossibleParents(appService,
            positionText, contType.getSite(), contType);
        if (isContainerPosition)
            foundContainers = possibles;
        else {
            foundContainers = new ArrayList<ContainerWrapper>();
            // need to know if can contain specimen if this is a specimen
            // position
            for (ContainerWrapper cont : possibles) {
                if (cont.getContainerType().getSpecimenTypeCollection() != null
                    && cont.getContainerType().getSpecimenTypeCollection()
                        .size() > 0) {
                    foundContainers.add(cont);
                }
            }
        }
        if (foundContainers.size() == 0) {
            List<Integer> validLengths = ContainerLabelingSchemeWrapper
                .getPossibleLabelLength(appService);
            StringBuffer res = new StringBuffer();

            for (int i = 0; i < validLengths.size(); i++) {
                Integer crop = validLengths.get(i);
                if (res.length() != 0)
                    res.append(", ");

                if (crop < positionText.length())
                    res.append(positionText.substring(0, positionText.length()
                        - crop));
            }
            String errorMsg;
            if (contType == null)
                if (isContainerPosition)
                    errorMsg =
                        MessageFormat
                            .format(
                                "Can''t find container that will match these possible labels: {0}",
                                res.toString());
                else
                    errorMsg =
                        MessageFormat
                            .format(
                                "Can''t find container that can hold specimens and that will match these possible labels: {0}",
                                res.toString());
            else
                errorMsg =
                    MessageFormat
                        .format(
                            "Can''t find container with type {0} that will match these possible labels: {1}",
                            contType.getNameShort(), res.toString());

            throw new BiobankException(errorMsg);
        }
        return foundContainers;
    }

    public boolean isPallet96() {
        return getContainerType().isPallet96();
    }

    private static final String POSITION_FREE_QRY = "from "
        + Specimen.class.getName()
        + " where "
        + SpecimenPeer.SPECIMEN_POSITION.to(SpecimenPositionPeer.ROW).getName()
        + "=? and "
        + SpecimenPeer.SPECIMEN_POSITION.to(SpecimenPositionPeer.COL).getName()
        + "=? and "
        + SpecimenPeer.SPECIMEN_POSITION.to(SpecimenPositionPeer.CONTAINER)
            .getName() + "=?";

    /**
     * Method used to check if the current position of this Specimen is
     * available on the container. Return true if the position is free, false
     * otherwise
     */
    public boolean isPositionFree(RowColPos position)
        throws ApplicationException {
        if (position != null) {
            if (!isPropertyCached(ContainerPeer.CHILD_POSITIONS)) {
                HQLCriteria criteria = new HQLCriteria(POSITION_FREE_QRY,
                    Arrays.asList(new Object[] { position.getRow(),
                        position.getCol(), getWrappedObject() }));

                // TODO: select a count instead?
                List<Specimen> samples = appService.query(criteria);
                return samples.isEmpty();
            }
            return !getChildren().containsKey(position);
        }
        return true;
    }

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        // TODO: is this next line necessary? Causes error w/ hibernate, so
        // allow cascade via hibernate?
        // tasks.deleteRemovedUnchecked(this, ContainerPeer.POSITION);

        super.addPersistTasks(tasks);

        tasks.persist(this, ContainerPeer.POSITION);

        // Need to update the path property after this Container and its
        // position have been saved to ensure that the path is calculated based
        // on persistent (non-new) objects.
        tasks.add(new UpdateContainerPathAction(this));

        tasks.persistAdded(this, ContainerPeer.SPECIMEN_POSITIONS);
        tasks.persistAdded(this, ContainerPeer.CHILD_POSITIONS);

        addTasksToUpdateChildren(tasks);
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // Count on Hibernate to delete-cascade this object. We can't because
        // there's a two-way foreign key constraint. So we could, but it's
        // really confusing.
        // tasks.add(cascade().delete(ContainerPeer.POSITION));

        super.addDeleteTasks(tasks);
    }

    /**
     * For updating children {@link Container}'s: (1) label, (2) path, and (3)
     * top {@link Container} whenever the parent or label is changed.
     * <p>
     * 
     * @return
     */
    private void addTasksToUpdateChildren(TaskList tasks) {
        if (updateChildren) {
            // getLabel() returns the in-memory version, but the underlying
            // value (e.g. super.getLabel()) needs to be updated for persisting.
            setLabel(getLabel());

            ContainerWrapper topContainer = getTopContainer();
            if (isPropertyCached(ContainerPeer.CHILD_POSITIONS)) {
                // if the children have already been loaded, then update their
                // top Container so that they update their children, etc. so
                // that the entire subtree is consistent.
                List<ContainerPositionWrapper> positions =
                    getChildPositionCollection(false);
                for (ContainerPositionWrapper position : positions) {
                    ContainerWrapper child = position.getContainer();

                    child.setTopContainerInternal(topContainer, false);

                    // Save children whether they're are new or not, because the
                    // children's children could be already persistent and need
                    // to be updated (but would then need their parent to be
                    // persisted first).
                    child.addPersistTasks(tasks);
                }
            } else {
                // Use HQL to update all descendants of this Container because
                // they are not loaded and loading them would be unnecessary.
                tasks.add(new UpdateContainerChildrenAction(this));
            }

            tasks.add(new ResetUpdateChildrenFlagQueryTask(this));
        }
    }

    private void checkPositionValid(RowColPos pos) throws BiobankCheckException {
        int maxRow = getRowCapacity();
        int maxCol = getColCapacity();
        if (pos.getRow() >= maxRow || pos.getCol() >= maxCol) {
            String msg = MessageFormat.format(OUT_OF_BOUNDS_POSITION_MSG, pos,
                maxRow, maxCol);
            throw new BiobankCheckException(msg);
        }
    }

    private static class ResetUpdateChildrenFlagQueryTask extends
        NoActionWrapperQueryTask<ContainerWrapper> {
        public ResetUpdateChildrenFlagQueryTask(ContainerWrapper container) {
            super(container);
        }

        @Override
        public void afterExecute(SDKQueryResult result) {
            getWrapper().updateChildren = false;
        }
    }
}
