package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.exception.ContainerLabelSearchException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends ContainerBaseWrapper {

    private static final String CHILDREN_CACHE_KEY = "children";

    private static final String SPECIMENS_CACHE_KEY = "specimens";

    private AbstractObjectWithPositionManagement<ContainerPosition, ContainerWrapper> objectWithPositionManagement;

    private List<ContainerWrapper> addedChildren = new ArrayList<ContainerWrapper>();

    private List<SpecimenWrapper> addedSpecimens = new ArrayList<SpecimenWrapper>();

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
        initManagement();
    }

    public ContainerWrapper(WritableApplicationService appService) {
        super(appService);
        initManagement();
    }

    private void initManagement() {
        objectWithPositionManagement = new AbstractObjectWithPositionManagement<ContainerPosition, ContainerWrapper>(
            this) {

            @Override
            protected AbstractPositionWrapper<ContainerPosition> getSpecificPositionWrapper(
                boolean initIfNoPosition) {
                if (nullPositionSet) {
                    if (rowColPosition != null) {
                        ContainerPositionWrapper posWrapper = new ContainerPositionWrapper(
                            appService);
                        posWrapper.setRow(rowColPosition.row);
                        posWrapper.setCol(rowColPosition.col);
                        posWrapper.setContainer(ContainerWrapper.this);
                        wrappedObject
                            .setPosition(posWrapper.getWrappedObject());
                        return posWrapper;
                    }
                } else {
                    ContainerPosition pos = wrappedObject.getPosition();
                    if (pos != null) {
                        return new ContainerPositionWrapper(appService, pos);
                    } else if (initIfNoPosition) {
                        ContainerPositionWrapper posWrapper = new ContainerPositionWrapper(
                            appService);
                        posWrapper.setContainer(ContainerWrapper.this);
                        wrappedObject
                            .setPosition(posWrapper.getWrappedObject());
                        return posWrapper;
                    }
                }
                return null;
            }
        };
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkLabelUniqueForType();
        checkNoDuplicatesInSite(Container.class,
            ContainerPeer.PRODUCT_BARCODE.getName(), getProductBarcode(),
            getSite().getId(), "A container with product barcode \""
                + getProductBarcode() + "\" already exists.");
        checkTopAndParent();
        checkParentAcceptContainerType();
        checkHasPosition();
        objectWithPositionManagement.persistChecks();
        checkParentFromSameSite();
        checkContainerTypeSameSite();
    }

    private void checkParentFromSameSite() throws BiobankCheckException {
        if (getParentContainer() != null
            && !getParentContainer().getSite().equals(getSite())) {
            throw new BiobankCheckException(
                "Parent should be part of the same site");
        }
    }

    private void checkHasPosition() throws BiobankCheckException {
        if ((getContainerType() != null)
            && !Boolean.TRUE.equals(getContainerType().getTopLevel())
            && (getPositionAsRowCol() == null)) {
            throw new BiobankCheckException(
                "A child container must have a position");
        }
    }

    /**
     * a container can't be a topContainer and have a parent on the same time
     */
    private void checkTopAndParent() throws BiobankCheckException {
        if ((getParentContainer() != null) && (getContainerType() != null)
            && Boolean.TRUE.equals(getContainerType().getTopLevel())) {
            throw new BiobankCheckException(
                "A top level container can't have a parent");
        }
    }

    private void checkContainerTypeSameSite() throws BiobankCheckException {
        if ((getContainerType() != null)
            && !getContainerType().getSite().equals(getSite())) {
            throw new BiobankCheckException(
                "Type should be part of the same site");
        }
    }

    @Override
    public void persist() throws Exception {
        objectWithPositionManagement.persist();
        super.persist();
        persistPath();
    }

    @Override
    protected void persistDependencies(Container origObject) throws Exception {
        ContainerWrapper parent = getParentContainer();
        boolean labelChanged = false;
        if (parent == null) {
            if ((origObject != null) && (getLabel() != null)
                && !getLabel().equals(origObject.getLabel())) {
                labelChanged = true;
            }
        } else {
            if (isNew()) {
                labelChanged = true;
            } else {
                if (origObject != null && origObject.getPosition() != null) {
                    // check the parent is the same
                    if (origObject.getPosition().getParentContainer() != null) {
                        if (!origObject.getPosition().getParentContainer()
                            .getId().equals(parent.getId())) {
                            labelChanged = true;
                        }
                    }
                    // check the position is the same
                    if (!new RowColPos(origObject.getPosition().getRow(),
                        origObject.getPosition().getCol())
                        .equals(getPositionAsRowCol())) {
                        labelChanged = true;
                    }
                }
                // check the parent label is the same
                if (getLabel() == null
                    || !getLabel().startsWith(parent.getLabel())) {
                    labelChanged = true;
                }
            }
            if (labelChanged) {
                // the label need to be modified
                String label = parent.getLabel() + getPositionString();
                setLabel(label);
                checkLabelUniqueForType();
            }
        }
        persistChildren(labelChanged);
        persistSpecimens();
    }

    public RowColPos getPositionAsRowCol() {
        return objectWithPositionManagement.getPosition();
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

    public void setPositionAsRowCol(RowColPos rcp) {
        objectWithPositionManagement.setPosition(rcp);
    }

    public ContainerWrapper getParentContainer() {
        return objectWithPositionManagement.getParentContainer();
    }

    public void setParent(ContainerWrapper container) {
        objectWithPositionManagement.setParent(container);
    }

    public boolean hasParentContainer() {
        return objectWithPositionManagement.hasParentContainer();
    }

    private void persistSpecimens() throws Exception {
        for (SpecimenWrapper specimen : addedSpecimens) {
            specimen.setParent(this);
            specimen.persist();
        }
    }

    private void persistChildren(boolean labelChanged) throws Exception {
        Collection<ContainerWrapper> childrenToUpdate = null;
        if (labelChanged) {
            Map<RowColPos, ContainerWrapper> map = getChildren();
            if (map != null) {
                childrenToUpdate = map.values();
            }
        } else {
            childrenToUpdate = addedChildren;
        }
        if (childrenToUpdate != null) {
            for (ContainerWrapper container : childrenToUpdate) {
                container.setParent(this);
                container.persist();
            }
        }
    }

    private void persistPath() throws Exception {
        // TODO: why does persisting always just get the current path, ignoring
        // the one we just set?
        ContainerPathWrapper containerPath = ContainerPathWrapper
            .getContainerPath(appService, this);
        if (containerPath == null) {
            containerPath = new ContainerPathWrapper(appService);
            containerPath.setContainer(this);
        }
        containerPath.persist();
    }

    private static final String LABEL_UNIQUE_FOR_TYPE_BASE_QRY = "select count(c) from "
        + Container.class.getName()
        + " as c where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + ContainerPeer.LABEL.getName()
        + "=? and "
        + ContainerPeer.CONTAINER_TYPE.getName() + "=?";

    private void checkLabelUniqueForType() throws BiobankException,
        ApplicationException {
        SiteWrapper site = getSite();
        if (site == null) {
            throw new BiobankException("container has no site");
        }
        ContainerTypeWrapper type = getContainerType();
        if (type == null) {
            throw new BiobankException("container has no type");
        }
        String notSameContainer = "";
        List<Object> parameters = new ArrayList<Object>(
            Arrays.asList(new Object[] { site.getId(), getLabel(),
                type.getWrappedObject() }));
        if (!isNew()) {
            notSameContainer = " and id <> ?";
            parameters.add(getId());
        }
        String qry = new StringBuilder(LABEL_UNIQUE_FOR_TYPE_BASE_QRY).append(
            notSameContainer).toString();
        if (getCountResult(appService, new HQLCriteria(qry, parameters)) > 0) {
            throw new DuplicateEntryException("A container with label \""
                + getLabel() + "\" and type \"" + getContainerType().getName()
                + "\" already exists.");
        }
    }

    public Integer getRowCapacity() {
        ContainerTypeWrapper type = getContainerType();
        if (type == null) {
            return null;
        }
        return type.getRowCapacity();
    }

    public Integer getColCapacity() {
        ContainerTypeWrapper type = getContainerType();
        if (type == null) {
            return null;
        }
        return type.getColCapacity();
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        ContainerWrapper container = this;

        while (container != null) {
            if (container.isNew()) {
                return null;
            }

            sb.insert(0, container.getId());
            sb.insert(0, "/");
            container = container.getParentContainer();
        }
        sb.deleteCharAt(0);

        return sb.toString();
    }

    private static final String CONTAINERS_WITH_SAME_LABEL_WITH_TYPE_BASE_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + ContainerPeer.LABEL.getName()
        + "=? and "
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.ID) + " in ( ";

    /**
     * get the containers with same label than this container and from same site
     * that this container. The container type should be in the list given
     * 
     * @throws ApplicationException
     */
    public List<ContainerWrapper> getContainersWithSameLabelWithType(
        List<ContainerTypeWrapper> types) throws ApplicationException {
        List<Integer> typeIds = new ArrayList<Integer>();
        for (ContainerTypeWrapper type : types) {
            typeIds.add(type.getId());
        }
        String qry = new StringBuilder(
            CONTAINERS_WITH_SAME_LABEL_WITH_TYPE_BASE_QRY)
            .append(StringUtils.join(typeIds, ',')).append(")").toString();
        HQLCriteria criteria = new HQLCriteria(qry, Arrays.asList(new Object[] {
            getSite().getId(), getLabel() }));
        List<Container> res = appService.query(criteria);
        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
        for (Container cont : res) {
            containers.add(new ContainerWrapper(appService, cont));
        }
        return containers;
    }

    /**
     * compute the ContainerPosition for this container using its label. If the
     * parent container cannot hold the container type of this container, then
     * an exception is thrown.
     */
    public void setPositionAndParentFromLabel(String label,
        List<ContainerTypeWrapper> types) throws Exception {
        // FIXME used only in ScanAssign, so its ok to use only last 2
        // characters. But what if it is use in others places
        String parentContainerLabel = label.substring(0, label.length() - 2);
        List<ContainerWrapper> possibleParents = ContainerWrapper
            .getContainersHoldingContainerTypes(appService,
                parentContainerLabel, getSite(), types);
        if ((possibleParents.size() == 0) || (possibleParents.size() > 1)) {
            List<String> typesString = new ArrayList<String>();
            for (ContainerTypeWrapper type : types) {
                typesString.add(new StringBuilder("\"").append(type.getName())
                    .append("\"").toString());
            }

            if (possibleParents.size() == 0) {
                throw new ContainerLabelSearchException(
                    "Can't find container with label \""
                        + parentContainerLabel
                        + "\" holding containers of types "
                        + StringUtils.join(typesString, " or ")
                        + " and in site "
                        + (getSite() == null ? "'none'" : getSite()
                            .getNameShort()));
            }
            if (possibleParents.size() > 1) {
                throw new ContainerLabelSearchException(
                    possibleParents.size()
                        + " containers with label "
                        + parentContainerLabel
                        + " and holding container types "
                        + StringUtils.join(typesString, " or ")
                        + " have been found. This is ambiguous: check containers definitions.");
            }
        }
        // has the parent container. Can now find the position using the
        // parent labelling scheme
        ContainerWrapper parent = possibleParents.get(0);
        setParent(parent);
        RowColPos position = parent.getPositionFromLabelingScheme(label
            .substring(label.length() - 2));
        setPositionAsRowCol(position);
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
            if (rcp.row >= type.getRowCapacity()
                || rcp.col >= type.getColCapacity()) {
                throw new Exception("Can't use position " + position
                    + " in container " + getFullInfoLabel()
                    + ". Reason: capacity = " + type.getRowCapacity() + "*"
                    + type.getColCapacity());
            }
            if (rcp.row < 0 || rcp.col < 0) {
                throw new Exception("Position \"" + position
                    + "\" is invalid for this container " + getFullInfoLabel());
            }
        }
        return rcp;
    }

    @SuppressWarnings("unchecked")
    public Map<RowColPos, SpecimenWrapper> getSpecimens() {
        Map<RowColPos, SpecimenWrapper> specimens = (Map<RowColPos, SpecimenWrapper>) cache
            .get(SPECIMENS_CACHE_KEY);
        if (specimens == null) {
            List<SpecimenPositionWrapper> positions = getWrapperCollection(
                ContainerPeer.SPECIMEN_POSITION_COLLECTION,
                SpecimenPositionWrapper.class, false);

            specimens = new TreeMap<RowColPos, SpecimenWrapper>();
            for (SpecimenPositionWrapper position : positions) {
                try {
                    position.reload();
                } catch (Exception ex) {
                    // do nothing
                }
                SpecimenWrapper spc = position.getSpecimen();
                specimens.put(
                    new RowColPos(position.getRow(), position.getCol()), spc);
            }
            cache.put(SPECIMENS_CACHE_KEY, specimens);
        }
        return specimens;
    }

    public boolean hasSpecimens() {
        Collection<SpecimenPosition> positions = wrappedObject
            .getSpecimenPositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public SpecimenWrapper getSpecimen(Integer row, Integer col)
        throws BiobankCheckException {
        SpecimenPositionWrapper specimenPosition = new SpecimenPositionWrapper(
            appService);
        specimenPosition.setRow(row);
        specimenPosition.setCol(col);
        specimenPosition.checkPositionValid(this);
        Map<RowColPos, SpecimenWrapper> specimens = getSpecimens();
        if (specimens == null) {
            return null;
        }
        return specimens.get(new RowColPos(row, col));
    }

    public void addSpecimen(Integer row, Integer col, SpecimenWrapper specimen)
        throws Exception {
        SpecimenPositionWrapper specimenPosition = new SpecimenPositionWrapper(
            appService);
        specimenPosition.setRow(row);
        specimenPosition.setCol(col);
        specimenPosition.checkPositionValid(this);
        Map<RowColPos, SpecimenWrapper> specimens = getSpecimens();
        if (specimens == null) {
            specimens = new TreeMap<RowColPos, SpecimenWrapper>();
            cache.put(SPECIMENS_CACHE_KEY, specimens);
        } else if (!canHoldSpecimen(specimen)) {
            throw new BiobankCheckException("Container " + getFullInfoLabel()
                + " does not allow inserts of type "
                + specimen.getSpecimenType().getName() + ".");
        } else {
            SpecimenWrapper sampleAtPosition = getSpecimen(row, col);
            if (sampleAtPosition != null) {
                throw new BiobankCheckException("Container "
                    + getFullInfoLabel()
                    + " is already holding an specimen at position "
                    + sampleAtPosition.getPositionString(false, false) + " ("
                    + row + ":" + col + ")");
            }
        }
        specimen.setPosition(new RowColPos(row, col));
        specimen.setParent(this);
        specimens.put(new RowColPos(row, col), specimen);
        addedSpecimens.add(specimen);
    }

    /**
     * return a string with the label of this container + the short name of its
     * type
     * 
     * @throws ApplicationException
     */
    public String getFullInfoLabel() {
        if (getContainerType() == null
            || getContainerType().getNameShort() == null) {
            return getLabel();
        }
        return getLabel() + " (" + getContainerType().getNameShort() + ")";
    }

    private static final String CHILD_COUNT_QRY = "select count(pos) from "
        + ContainerPosition.class.getName()
        + " as pos where pos."
        + Property.concatNames(ContainerPositionPeer.PARENT_CONTAINER,
            ContainerTypePeer.ID) + "=?";

    @SuppressWarnings("unchecked")
    public long getChildCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(CHILD_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        Map<RowColPos, ContainerWrapper> children = (Map<RowColPos, ContainerWrapper>) cache
            .get(CHILDREN_CACHE_KEY);
        if (children != null) {
            return children.size();
        }
        Collection<ContainerPosition> positions = wrappedObject
            .getChildPositionCollection();
        if (positions == null)
            return 0;
        return positions.size();
    }

    private static final String GET_CHILD_CONTAINERS_QRY = "from "
        + Container.class.getName() + " as c join fetch c."
        + ContainerPeer.CHILD_POSITION_COLLECTION.getName()
        + " as containerPositions join fetch containerPositions."
        + ContainerPositionPeer.CONTAINER.getName()
        + " as childContainers where c." + ContainerPeer.ID.getName() + "=?";

    @SuppressWarnings("unchecked")
    public Map<RowColPos, ContainerWrapper> getChildren()
        throws BiobankFailedQueryException {
        Map<RowColPos, ContainerWrapper> children = (Map<RowColPos, ContainerWrapper>) cache
            .get(CHILDREN_CACHE_KEY);
        if (children == null) {
            children = new TreeMap<RowColPos, ContainerWrapper>();

            HQLCriteria criteria = new HQLCriteria(GET_CHILD_CONTAINERS_QRY,
                Arrays.asList(new Object[] { getId() }));

            List<Container> results;
            try {
                results = appService.query(criteria);
                if ((results != null) && !results.isEmpty()) {

                    ContainerWrapper c = new ContainerWrapper(appService,
                        results.get(0));

                    for (ContainerPositionWrapper cp : c
                        .getChildPositionCollection(false)) {
                        children.put(new RowColPos(cp.getRow(), cp.getCol()),
                            cp.getContainer());
                    }
                }
                cache.put(CHILDREN_CACHE_KEY, children);
            } catch (ApplicationException e) {
                throw new BiobankFailedQueryException(e);
            }
        }
        return children;
    }

    public boolean hasChildren() {
        Collection<ContainerPosition> positions = wrappedObject
            .getChildPositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public ContainerWrapper getChild(Integer row, Integer col)
        throws BiobankFailedQueryException {
        return getChild(new RowColPos(row, col));
    }

    public ContainerWrapper getChild(RowColPos rcp)
        throws BiobankFailedQueryException {
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children == null) {
            return null;
        }
        return children.get(rcp);
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

    private void checkParentAcceptContainerType() throws BiobankCheckException {
        if (Boolean.TRUE.equals(getContainerType().getTopLevel()))
            return;

        ContainerWrapper parent = getParentContainer();
        if (parent == null)
            throw new BiobankCheckException("Container " + this
                + " does not have a parent container");
        ContainerTypeWrapper parentType = getParentContainer()
            .getContainerType();
        try {
            // need to reload the type to avoid loop problems (?) from the
            // spring server side in specific cases. (on
            // getChildContainerTypeCollection).
            // Ok if nothing linked to the type.
            parentType.reload();
        } catch (Exception e) {
            throw new BiobankCheckException(e);
        }
        List<ContainerTypeWrapper> types = parentType
            .getChildContainerTypeCollection();
        if (types == null || !types.contains(getContainerType())) {
            throw new BiobankCheckException("Container "
                + getParentContainer().getFullInfoLabel()
                + " does not allow inserts of container type "
                + getContainerType().getName() + ".");
        }
    }

    public void addChild(Integer row, Integer col, ContainerWrapper child)
        throws BiobankException {
        ContainerPositionWrapper tempPosition = new ContainerPositionWrapper(
            appService);
        tempPosition.setRow(row);
        tempPosition.setCol(col);
        tempPosition.checkPositionValid(this);
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children == null) {
            children = new TreeMap<RowColPos, ContainerWrapper>();
            cache.put(CHILDREN_CACHE_KEY, children);
        } else {
            ContainerWrapper containerAtPosition = getChild(row, col);
            if (containerAtPosition != null) {
                throw new BiobankCheckException("Container "
                    + getFullInfoLabel()
                    + " is already holding a container at position "
                    + containerAtPosition.getLabel() + " (" + row + ":" + col
                    + ")");
            }
        }
        child.setPositionAsRowCol(new RowColPos(row, col));
        child.setParent(this);
        children.put(new RowColPos(row, col), child);
        addedChildren.add(child);
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
        RowColPos position = getPositionFromLabelingScheme(positionString);
        addChild(position.row, position.col, child);
    }

    /**
     * Return true if this container can hold the type of sample
     * 
     * @throws Exception if the sample type is null.
     */
    public boolean canHoldSpecimen(SpecimenWrapper specimen) throws Exception {
        SpecimenTypeWrapper type = specimen.getSpecimenType();
        if (type == null) {
            throw new BiobankCheckException("sample type is null");
        }
        return getContainerType().getSpecimenTypeCollection(false).contains(
            type);
    }

    public void moveSpecimens(ContainerWrapper destination) throws Exception {
        Map<RowColPos, SpecimenWrapper> aliquots = getSpecimens();
        for (Entry<RowColPos, SpecimenWrapper> e : aliquots.entrySet()) {
            destination.addSpecimen(e.getKey().row, e.getKey().col,
                e.getValue());
        }
        destination.persist();
    }

    @Override
    public boolean checkIntegrity() {
        /*
         * outdated? if (wrappedObject != null) if (((getContainerType() !=
         * null) && (getContainerType().getRowCapacity() != null) &&
         * (getContainerType() .getColCapacity() != null)) ||
         * (getContainerType() == null)) if (((getPosition() != null) &&
         * (getPosition().row != null) && (getPosition().col != null)) ||
         * (getPosition() == null)) if (wrappedObject.getSite() != null) return
         * true; return false;
         */
        return true;

    }

    @Override
    protected void deleteChecks() throws BiobankDeleteException,
        ApplicationException {
        if (hasSpecimens()) {
            throw new BiobankDeleteException("Unable to delete container "
                + getLabel() + ". All specimens must be removed first.");
        }
        if (hasChildren()) {
            throw new BiobankDeleteException("Unable to delete container "
                + getLabel() + ". All subcontainers must be removed first.");
        }
    }

    @Override
    protected void deleteDependencies() throws Exception {
        ContainerPathWrapper path = ContainerPathWrapper.getContainerPath(
            appService, this);
        if (path != null) {
            path.delete();
        }
    }

    /**
     * Get containers with a given label that can hold this type of container
     * (in this container site)
     * 
     * @throws BiobankException
     */
    public List<ContainerWrapper> getPossibleParents(String childLabel)
        throws ApplicationException, BiobankException {
        return getPossibleParents(appService, childLabel, getSite(), this);
    }

    private static final String POSSIBLE_PARENTS_BASE_QRY = "select distinct(c) from "
        + Container.class.getName()
        + " as c left join c."
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION)
        + " as ct where c."
        + ContainerPeer.SITE.getName()
        + "=? and c."
        + ContainerPeer.LABEL.getName() + " in (";

    /**
     * Get containers with a given label that can have a child (container or
     * specimen) with label 'childLabel'. If child is not null and is a
     * container, then will check that the parent can contain this type of
     * container
     * 
     * @throws BiobankException
     */
    public static List<ContainerWrapper> getPossibleParents(
        WritableApplicationService appService, String childLabel,
        SiteWrapper site, ModelWrapper<?> child) throws ApplicationException,
        BiobankException {
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

        List<ContainerWrapper> filteredWrappers = new ArrayList<ContainerWrapper>();
        if (validParents.size() > 0) {
            List<Object> params = new ArrayList<Object>();
            params.add(site.getWrappedObject());
            StringBuilder parentQuery = new StringBuilder(
                POSSIBLE_PARENTS_BASE_QRY).append(
                StringUtils.join(validParents, ',')).append(")");
            if (child != null && child instanceof ContainerWrapper) {
                parentQuery.append(" and ct=?");
                params.add(((ContainerWrapper) child).getContainerType()
                    .getWrappedObject());
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
                    throw new BiobankException(e);
                }
            }
        }
        return filteredWrappers;
    }

    private static final String CONTAINERS_HOLDING_CONTAINER_TYPES_PART1_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + ContainerPeer.LABEL.getName() + "=? ";
    private static final String CONTAINERS_HOLDING_CONTAINER_TYPES_PART2_QRY = "and "
        + ContainerPeer.CONTAINER_TYPE.getName()
        + " in (select parent from "
        + ContainerType.class.getName()
        + " as parent where "
        + ContainerTypePeer.ID.getName()
        + " in (select ct."
        + ContainerTypePeer.ID.getName()
        + " from "
        + ContainerType.class.getName()
        + " as ct"
        + " left join ct."
        + ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION.getName()
        + " as child where child." + ContainerTypePeer.ID.getName() + " in (";

    /**
     * get containers with label label in site which can have children of types
     * container type
     * 
     * don't check the type is types list is null or empty
     */
    public static List<ContainerWrapper> getContainersHoldingContainerTypes(
        WritableApplicationService appService, String label, SiteWrapper site,
        List<ContainerTypeWrapper> types) throws ApplicationException {
        if (site == null) {
            throw new NullPointerException();
        }
        List<Integer> typeIds = new ArrayList<Integer>();
        if (types != null)
            for (ContainerTypeWrapper type : types) {
                typeIds.add(type.getId());
            }
        StringBuffer qry = new StringBuffer(
            CONTAINERS_HOLDING_CONTAINER_TYPES_PART1_QRY);
        if (typeIds.size() > 0) {
            qry.append(CONTAINERS_HOLDING_CONTAINER_TYPES_PART2_QRY)
                .append(StringUtils.join(typeIds, ',')).append(")))")
                .toString();
        }
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { site.getId(), label }));
        List<Container> containers = appService.query(criteria);
        return wrapModelCollection(appService, containers,
            ContainerWrapper.class);
    }

    private static final String CONTAINERS_HOLDING_SPECIMEN_TYPES_QRY = "from "
        + Container.class.getName() + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID) + "=? and "
        + ContainerPeer.LABEL.getName() + "=? and "
        + ContainerPeer.CONTAINER_TYPE.getName() + " in (select parent from "
        + ContainerType.class.getName() + " as parent where parent."
        + ContainerTypePeer.ID.getName() + " in (select ct."
        + ContainerTypePeer.ID.getName() + " from "
        + ContainerType.class.getName() + " as ct" + " left join ct."
        + ContainerTypePeer.SPECIMEN_TYPE_COLLECTION.getName()
        + " as sampleType where sampleType = ?))";

    /**
     * get the containers with label label and site siteWrapper and holding
     * given sample type
     */
    public static List<ContainerWrapper> getContainersHoldingSampleType(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String label, SpecimenTypeWrapper sampleType)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            CONTAINERS_HOLDING_SPECIMEN_TYPES_QRY, Arrays.asList(new Object[] {
                siteWrapper.getId(), label, sampleType.getWrappedObject() }));
        List<Container> containers = appService.query(criteria);
        return wrapModelCollection(appService, containers,
            ContainerWrapper.class);
    }

    private static final String EMPTY_CONTAINERS_HOLDING_SPECIMEN_TYPE_BASE_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + ContainerPeer.SPECIMEN_POSITION_COLLECTION.getName()
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
        + ContainerTypePeer.SPECIMEN_TYPE_COLLECTION.getName()
        + " as sampleType where sampleType."
        + SpecimenTypePeer.ID.getName()
        + " in (";

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
            .append(StringUtils.join(typeIds, ',')).append("))").toString();
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

    private static final String CONTAINER_WITH_PRODUCT_BARCODE_IN_SITE_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + ContainerPeer.PRODUCT_BARCODE.getName() + "=?";

    /**
     * Get the container with the given productBarcode in a site
     */
    public static ContainerWrapper getContainerWithProductBarcodeInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String productBarcode) throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            CONTAINER_WITH_PRODUCT_BARCODE_IN_SITE_QRY,
            Arrays.asList(new Object[] { siteWrapper.getId(), productBarcode }));
        List<Container> containers = appService.query(criteria);
        if (containers.size() == 0) {
            return null;
        } else if (containers.size() > 1) {
            throw new Exception(
                "Multiples containers registered with product barcode "
                    + productBarcode);
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
                initPositionIfEmpty(type, rcp.row, rcp.col);
            }
        }
        reload();
    }

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
            newContainer.setTemperature(getTemperature());
            newContainer.setPositionAsRowCol(new RowColPos(i, j));
            newContainer.setParent(this);
            newContainer.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
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
        reload();
        return oneChildrenDeleted;
    }

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

    @Override
    public String toString() {
        return getLabel() + " (" + getProductBarcode() + ")";
    }

    @Override
    protected void resetInternalFields() {
        addedChildren.clear();
        addedSpecimens.clear();
        objectWithPositionManagement.resetInternalFields();
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

    public ContainerWrapper getTop() {
        return objectWithPositionManagement.getTop();
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
    public static List<ContainerWrapper> getPossibleContainersFromPosition(
        BiobankApplicationService appService, User user, String positionText,
        boolean isContainerPosition, ContainerTypeWrapper contType)
        throws ApplicationException, BiobankException {
        String fullLabel = positionText;
        List<ContainerWrapper> foundContainers = new ArrayList<ContainerWrapper>();
        int removeSize = 2;
        List<String> parentLabelsTested = new ArrayList<String>();
        List<ContainerTypeWrapper> typesList = new ArrayList<ContainerTypeWrapper>();
        if (contType != null)
            typesList.add(contType);
        while (removeSize < 5) { // we are assuming that an object position
                                 // in its parent won't be bigger than 3 !
            int cutIndex = fullLabel.length() - removeSize;
            if (cutIndex > 0) {
                String parentLabel = fullLabel.substring(0, cutIndex);
                parentLabelsTested.add(parentLabel);
                for (ContainerWrapper cont : ContainerWrapper
                    .getContainersHoldingContainerTypes(appService,
                        parentLabel, user.getCurrentWorkingSite(), typesList)) {
                    // need to know if can contain specimen if this is a
                    // specimen position
                    boolean canContainSpecimens = cont.getContainerType()
                        .getSpecimenTypeCollection() != null
                        && cont.getContainerType().getSpecimenTypeCollection()
                            .size() > 0;
                    if (isContainerPosition || canContainSpecimens) {
                        RowColPos rcp = null;
                        try {
                            // check if the string position is possible in
                            // this container
                            ContainerTypeWrapper type = cont.getContainerType();
                            rcp = ContainerLabelingSchemeWrapper
                                .getRowColFromPositionString(appService,
                                    fullLabel.substring(cutIndex),
                                    type.getChildLabelingSchemeId(),
                                    type.getRowCapacity(),
                                    type.getColCapacity());
                        } catch (Exception ex) {
                            // the test failed
                            continue;
                        }
                        if (rcp != null) // the full position string is
                                         // valid:
                            foundContainers.add(cont);
                    }
                }
            }
            removeSize++;
        }
        if (foundContainers.size() == 0) {
            StringBuffer res = new StringBuffer();
            for (int i = 0; i < parentLabelsTested.size(); i++) {
                if (i != 0) {
                    res.append(", "); //$NON-NLS-1$
                }
                String binLabel = parentLabelsTested.get(i);
                res.append(binLabel).append("(") //$NON-NLS-1$
                    .append(fullLabel.replace(binLabel, "")).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String errorMsg;
            if (contType == null)
                errorMsg = Messages
                    .getString(
                        "ContainerWrapper.getPossibleContainersFromPosition.error.notfound.msg", //$NON-NLS-1$
                        res.toString());
            else
                errorMsg = Messages
                    .getString(
                        "ContainerWrapper.getPossibleContainersFromPosition.error.notfoundWithType.msg",//$NON-NLS-1$
                        res.toString(), contType.getNameShort());

            throw new BiobankException(errorMsg);
        }
        return foundContainers;
    }
}
