package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.security.SecurityHelper;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AliquotPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends
    AbstractPositionHolder<Container, ContainerPosition> {

    private List<ContainerWrapper> addedChildren = new ArrayList<ContainerWrapper>();

    private List<AliquotWrapper> addedAliquots = new ArrayList<AliquotWrapper>();

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "productBarcode", "position", "activityStatus",
            "site", "label", "temperature", "comment",
            "aliquotPositionCollection", "aliquots", "childPositionCollection",
            "children", "containerType", "parent" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the container does not have an activity status");
        }
        checkSiteNotNull();
        checkContainerTypeNotNull();
        checkLabelUniqueForType();
        checkNoDuplicatesInSite(Container.class, "productBarcode",
            getProductBarcode(), getSite().getId(),
            "A container with product barcode \"" + getProductBarcode()
                + "\" already exists.");
        checkTopAndParent();
        checkParentAcceptContainerType();
        checkContainerTypeSameSite();
        super.persistChecks();
    }

    /**
     * a container can't be a topContainer and have a parent on the same time
     */
    private void checkTopAndParent() throws BiobankCheckException {
        if ((getParent() != null) && (getContainerType() != null)
            && getContainerType().getTopLevel().booleanValue()) {
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
        super.persist();
        persistPath();
    }

    @Override
    protected void persistDependencies(Container origObject) throws Exception {
        ContainerWrapper parent = getParent();
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
                        .equals(getPosition())) {
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
                String label = parent.getLabel()
                    + LabelingScheme.getPositionString(this);
                setLabel(label);
            }
        }
        persistChildren(labelChanged);
        persistAliquots();
    }

    private void persistAliquots() throws Exception {
        for (AliquotWrapper aliquot : addedAliquots) {
            aliquot.setParent(this);
            aliquot.persist();
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
        ContainerPathWrapper containerPath = ContainerPathWrapper
            .getContainerPath(appService, this);
        if (containerPath == null) {
            containerPath = new ContainerPathWrapper(appService);
            containerPath.setContainer(this);
        }
        containerPath.persist();
    }

    private void checkLabelUniqueForType() throws BiobankCheckException,
        ApplicationException {
        String notSameContainer = "";
        List<Object> parameters = new ArrayList<Object>(Arrays
            .asList(new Object[] { getSite().getId(), getLabel(),
                getContainerType().getWrappedObject() }));
        if (!isNew()) {
            notSameContainer = " and id <> ?";
            parameters.add(getId());
        }
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site.id=? and label=? "
            + "and containerType=?" + notSameContainer, parameters);
        List<Object> results = appService.query(criteria);
        if (results.size() > 0) {
            throw new BiobankCheckException("A container with label \""
                + getLabel() + "\" and type \"" + getContainerType().getName()
                + "\" already exists.");
        }
    }

    private void checkSiteNotNull() throws BiobankCheckException {
        if (getSite() == null) {
            throw new BiobankCheckException(
                "This container should be associate to a site");
        }
    }

    private void checkContainerTypeNotNull() throws BiobankCheckException {
        if (getContainerType() == null) {
            throw new BiobankCheckException("This container type should be set");
        }
    }

    @Override
    public Class<Container> getWrappedClass() {
        return Container.class;
    }

    @Override
    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public String getProductBarcode() {
        return wrappedObject.getProductBarcode();
    }

    public void setProductBarcode(String barcode) {
        String oldBarcode = getProductBarcode();
        wrappedObject.setProductBarcode(barcode);
        propertyChangeSupport.firePropertyChange("productBarcode", oldBarcode,
            barcode);
    }

    private ContainerPathWrapper getContainerPath() throws Exception {
        return ContainerPathWrapper.getContainerPath(appService, this);
    }

    public String getPath() throws Exception {
        ContainerPathWrapper containerPath = getContainerPath();
        if (containerPath == null) {
            return null;
        }
        return containerPath.getPath();
    }

    /**
     * get the containers with same label than this container and from same site
     * that this container. The container type should be in the list given
     * 
     * @throws ApplicationException
     */
    public List<ContainerWrapper> getContainersWithSameLabelWithType(
        List<ContainerTypeWrapper> types) throws ApplicationException {
        String typeIds = "";
        for (ContainerTypeWrapper type : types) {
            typeIds += "," + type.getId();
        }
        typeIds = typeIds.replaceFirst(",", "");
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and label = ? and containerType.id in ( "
            + typeIds + " )", Arrays.asList(new Object[] { getSite().getId(),
            getLabel() }));
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
     * an exception is launched
     */
    public void setPositionAndParentFromLabel(String label,
        List<ContainerTypeWrapper> types) throws Exception {
        String parentContainerLabel = label.substring(0, label.length() - 2);
        List<ContainerWrapper> possibleParents = ContainerWrapper
            .getContainersHoldingContainerTypes(appService,
                parentContainerLabel, getSite(), types);
        if (possibleParents.size() == 0) {
            String typesString = "";
            for (ContainerTypeWrapper type : types) {
                typesString += " or '" + type.getName() + "'";
            }
            typesString = typesString.replaceFirst(" or", "");
            throw new BiobankCheckException("Can't find container with label "
                + parentContainerLabel + " holding containers of types "
                + typesString);
        }
        if (possibleParents.size() > 1) {
            throw new BiobankCheckException(
                possibleParents.size()
                    + " containers with label "
                    + parentContainerLabel
                    + " and holding container types "
                    + getContainerType().getName()
                    + " have been found. This is ambiguous: check containers definitions.");
        }
        // has the parent container. Can now find the position using the
        // parent labelling scheme
        setParent(possibleParents.get(0));
        possibleParents.get(0).addChild(label.substring(label.length() - 2),
            this);
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
        RowColPos rcp = LabelingScheme.getRowColFromPositionString(position,
            type);
        if (rcp != null) {
            if (rcp.row >= type.getRowCapacity()
                || rcp.col >= type.getColCapacity()) {
                throw new Exception("Can't use position " + position
                    + " in container " + getFullInfoLabel()
                    + "\nReason: capacity = " + type.getRowCapacity() + "*"
                    + type.getColCapacity());
            }
            if (rcp.row < 0 || rcp.col < 0) {
                throw new Exception("Position \"" + position
                    + "\" is invalid for this container " + getFullInfoLabel());
            }
        }
        return rcp;
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

    public void setContainerType(ContainerTypeWrapper containerType) {
        if (containerType == null) {
            setContainerType((ContainerType) null);
        } else {
            setContainerType(containerType.getWrappedObject());
        }
    }

    protected void setContainerType(ContainerType containerType) {
        ContainerType oldType = wrappedObject.getContainerType();
        wrappedObject.setContainerType(containerType);
        propertyChangeSupport.firePropertyChange("containerType", oldType,
            containerType);
    }

    public ContainerTypeWrapper getContainerType() {
        ContainerType type = wrappedObject.getContainerType();
        if (type == null) {
            return null;
        }
        return new ContainerTypeWrapper(appService, type);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatus activityStatus = wrappedObject.getActivityStatus();
        if (activityStatus == null)
            return null;
        return new ActivityStatusWrapper(appService, activityStatus);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    protected void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper siteWrapper) {
        if (siteWrapper == null) {
            setSite((Site) null);
        } else {
            setSite(siteWrapper.getWrappedObject());
        }
    }

    public void setLabel(String label) {
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    @SuppressWarnings("unchecked")
    public Map<RowColPos, AliquotWrapper> getAliquots() {
        Map<RowColPos, AliquotWrapper> aliquots = (Map<RowColPos, AliquotWrapper>) propertiesMap
            .get("aliquots");
        if (aliquots == null) {
            Collection<AliquotPosition> positions = wrappedObject
                .getAliquotPositionCollection();
            if (positions != null) {
                aliquots = new TreeMap<RowColPos, AliquotWrapper>();
                for (AliquotPosition position : positions) {
                    aliquots.put(new RowColPos(position.getRow(), position
                        .getCol()), new AliquotWrapper(appService, position
                        .getAliquot()));
                }
                propertiesMap.put("aliquots", aliquots);
            }
        }
        return aliquots;
    }

    public boolean hasAliquots() {
        Collection<AliquotPosition> positions = wrappedObject
            .getAliquotPositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public AliquotWrapper getAliquot(Integer row, Integer col)
        throws BiobankCheckException {
        AliquotPositionWrapper aliquotPosition = new AliquotPositionWrapper(
            appService);
        aliquotPosition.setRow(row);
        aliquotPosition.setCol(col);
        aliquotPosition.checkPositionValid(this);
        Map<RowColPos, AliquotWrapper> aliquots = getAliquots();
        if (aliquots == null) {
            return null;
        }
        return aliquots.get(new RowColPos(row, col));
    }

    public void addAliquot(Integer row, Integer col, AliquotWrapper aliquot)
        throws Exception {
        AliquotPositionWrapper aliquotPosition = new AliquotPositionWrapper(
            appService);
        aliquotPosition.setRow(row);
        aliquotPosition.setCol(col);
        aliquotPosition.checkPositionValid(this);
        Map<RowColPos, AliquotWrapper> aliquots = getAliquots();
        if (aliquots == null) {
            aliquots = new TreeMap<RowColPos, AliquotWrapper>();
            propertiesMap.put("aliquots", aliquots);
        } else if (!canHoldAliquot(aliquot)) {
            throw new BiobankCheckException("Container " + getFullInfoLabel()
                + " does not allow inserts of type "
                + aliquot.getSampleType().getName() + ".");
        } else {
            AliquotWrapper sampleAtPosition = getAliquot(row, col);
            if (sampleAtPosition != null) {
                throw new BiobankCheckException("Container "
                    + getFullInfoLabel()
                    + " is already holding an aliquot at position "
                    + sampleAtPosition.getPositionString(false, false) + " ("
                    + row + ":" + col + ")");
            }
        }
        aliquot.setPosition(row, col);
        aliquot.setParent(this);
        aliquots.put(new RowColPos(row, col), aliquot);
        addedAliquots.add(aliquot);
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

    public void setTemperature(Double temperature) {
        Double oldTemp = getTemperature();
        wrappedObject.setTemperature(temperature);
        propertyChangeSupport.firePropertyChange("temperature", oldTemp,
            temperature);
    }

    public Double getTemperature() {
        return getWrappedObject().getTemperature();
    }

    @SuppressWarnings("unchecked")
    public int getChildCount() {
        Map<RowColPos, ContainerWrapper> children = (Map<RowColPos, ContainerWrapper>) propertiesMap
            .get("children");
        if (children != null) {
            return children.size();
        }
        return wrappedObject.getChildPositionCollection().size();
    }

    @SuppressWarnings("unchecked")
    public Map<RowColPos, ContainerWrapper> getChildren() {
        Map<RowColPos, ContainerWrapper> children = (Map<RowColPos, ContainerWrapper>) propertiesMap
            .get("children");
        if (children == null) {
            Collection<ContainerPosition> positions = wrappedObject
                .getChildPositionCollection();
            if (positions != null) {
                children = new TreeMap<RowColPos, ContainerWrapper>();
                for (ContainerPosition position : positions) {
                    ContainerWrapper child = new ContainerWrapper(appService,
                        position.getContainer());
                    try {
                        // try to reload - will start with a fresh ModelObject
                        // not containing the whole object hierarchy it can hold
                        child.reload();
                    } catch (Exception e) {
                    }
                    children.put(new RowColPos(position.getRow(), position
                        .getCol()), child);
                }
                propertiesMap.put("children", children);
            }
        }
        return children;
    }

    public boolean hasChildren() {
        Collection<ContainerPosition> positions = wrappedObject
            .getChildPositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public ContainerWrapper getChild(Integer row, Integer col) {
        return getChild(new RowColPos(row, col));
    }

    public ContainerWrapper getChild(RowColPos rcp) {
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
        RowColPos pos;
        switch (containerType.getChildLabelingScheme()) {
        case 1:
            pos = LabelingScheme.sbsToRowCol(label);
            break;
        case 2:
            pos = LabelingScheme.cbsrTwoCharToRowCol(label, getRowCapacity(),
                getColCapacity(), containerType.getName());
            break;
        case 3:
        default:
            pos = LabelingScheme.twoCharNumericToRowCol(containerType, label);
        }
        return getChild(pos);
    }

    private void checkParentAcceptContainerType() throws BiobankCheckException {
        if (Boolean.TRUE.equals(getContainerType().getTopLevel()))
            return;

        ContainerWrapper parent = getParent();
        if (parent == null)
            throw new BiobankCheckException("Container " + this
                + " does not have a parent container");
        ContainerTypeWrapper parentType = getParent().getContainerType();
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
                + getParent().getFullInfoLabel()
                + " does not allow inserts of type "
                + getContainerType().getName() + ".");
        }
    }

    public void addChild(Integer row, Integer col, ContainerWrapper child)
        throws BiobankCheckException {
        ContainerPositionWrapper tempPosition = new ContainerPositionWrapper(
            appService);
        tempPosition.setRow(row);
        tempPosition.setCol(col);
        tempPosition.checkPositionValid(this);
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children == null) {
            children = new TreeMap<RowColPos, ContainerWrapper>();
            propertiesMap.put("children", children);
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
        child.setPosition(row, col);
        child.setParent(this);
        children.put(new RowColPos(row, col), child);
        addedChildren.add(child);
    }

    /**
     * Add a child in this container
     * 
     * @param positionString position where the child should be added. e.g. AA
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
    public boolean canHoldAliquot(AliquotWrapper aliquot) throws Exception {
        SampleTypeWrapper type = aliquot.getSampleType();
        if (type == null) {
            throw new WrapperException("sample type is null");
        }
        HQLCriteria criteria = new HQLCriteria("select sampleType from "
            + ContainerType.class.getName()
            + " as ct inner join ct.sampleTypeCollection as sampleType"
            + " where ct = ? and sampleType = ?", Arrays.asList(new Object[] {
            wrappedObject.getContainerType(), type.getWrappedObject() }));
        List<SampleType> types = appService.query(criteria);
        return types.size() == 1;
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    @Override
    public boolean checkIntegrity() {
        if (wrappedObject != null)
            if (((getContainerType() != null)
                && (getContainerType().getRowCapacity() != null) && (getContainerType()
                .getColCapacity() != null))
                || (getContainerType() == null))
                if (((getPosition() != null) && (getPosition().row != null) && (getPosition().col != null))
                    || (getPosition() == null))
                    if (wrappedObject.getSite() != null)
                        return true;
        return false;

    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (hasAliquots()) {
            throw new BiobankCheckException("Unable to delete container "
                + getLabel() + ". All samples must be removed first.");
        }
        if (hasChildren()) {
            throw new BiobankCheckException("Unable to delete container "
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
     */
    public List<ContainerWrapper> getPossibleParents(String parentLabel)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("select c from "
            + Container.class.getName()
            + " as c left join c.containerType.childContainerTypeCollection "
            + "as ct where c.site = ? and c.label = ? and ct=?", Arrays
            .asList(new Object[] { getSite().getWrappedObject(), parentLabel,
                getContainerType().getWrappedObject() }));
        List<Container> containers = appService.query(criteria);
        return transformToWrapperList(appService, containers);
    }

    /**
     * get containers with label label in site which can have children of types
     * container type
     */
    public static List<ContainerWrapper> getContainersHoldingContainerTypes(
        WritableApplicationService appService, String label, SiteWrapper site,
        List<ContainerTypeWrapper> types) throws ApplicationException {
        String typeIds = "";
        for (ContainerTypeWrapper type : types) {
            typeIds += "," + type.getId();
        }
        typeIds = typeIds.replaceFirst(",", "");
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Container.class.getName()
                + " where site.id = ? and label = ? and containerType in (select parent from "
                + ContainerType.class.getName()
                + " as parent where parent.id in (select ct.id" + " from "
                + ContainerType.class.getName() + " as ct"
                + " left join ct.childContainerTypeCollection as child "
                + " where child.id in (" + typeIds + ")))", Arrays
                .asList(new Object[] { site.getId(), label }));
        List<Container> containers = appService.query(criteria);
        return transformToWrapperList(appService, containers);
    }

    /**
     * get the containers with label label and from same site that this
     * containerWrapper and holding sample type
     */
    public static List<ContainerWrapper> getContainersHoldingSampleType(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String label, SampleTypeWrapper sampleType) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Container.class.getName()
                + " where site.id = ? and label = ? and containerType in (select parent from "
                + ContainerType.class.getName()
                + " as parent where parent.id in (select ct.id" + " from "
                + ContainerType.class.getName() + " as ct"
                + " left join ct.sampleTypeCollection as sampleType "
                + " where sampleType = ?))", Arrays.asList(new Object[] {
                siteWrapper.getId(), label, sampleType.getWrappedObject() }));
        List<Container> containers = appService.query(criteria);
        return transformToWrapperList(appService, containers);
    }

    /**
     * Get all containers form a given site with a given label
     */
    public static List<ContainerWrapper> getContainersInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String label) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site.id = ? and label = ?",
            Arrays.asList(new Object[] { siteWrapper.getId(), label }));
        List<Container> containers = appService.query(criteria);
        return transformToWrapperList(appService, containers);
    }

    /**
     * Get the container with the given productBarcode in a site
     */
    public static ContainerWrapper getContainerWithProductBarcodeInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String productBarcode) throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and productBarcode = ?", Arrays
            .asList(new Object[] { siteWrapper.getId(), productBarcode }));
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

    public static List<ContainerWrapper> transformToWrapperList(
        WritableApplicationService appService, List<Container> containers) {
        List<ContainerWrapper> list = new ArrayList<ContainerWrapper>();
        for (Container container : containers) {
            list.add(new ContainerWrapper(appService, container));
        }
        return list;
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
        Boolean filled = (getChild(i, j) != null);
        if (!filled) {
            ContainerWrapper newContainer = new ContainerWrapper(appService);
            newContainer.setContainerType(type.getWrappedObject());
            newContainer.setSite(getSite().getWrappedObject());
            newContainer.setTemperature(getTemperature());
            newContainer.setPosition(i, j);
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
    protected AbstractPositionWrapper<ContainerPosition> getSpecificPositionWrapper(
        boolean initIfNoPosition) {
        ContainerPosition pos = wrappedObject.getPosition();
        if (pos != null) {
            return new ContainerPositionWrapper(appService, pos);
        } else if (initIfNoPosition) {
            ContainerPositionWrapper posWrapper = new ContainerPositionWrapper(
                appService);
            posWrapper.setContainer(this);
            wrappedObject.setPosition(posWrapper.getWrappedObject());
            return posWrapper;
        }
        return null;
    }

    /**
     * init this wrapper with the given containerWrapper.
     * 
     * @throws WrapperException
     */
    public void initObjectWith(ContainerWrapper containerWrapper)
        throws WrapperException {
        if (containerWrapper == null) {
            throw new WrapperException(
                "Cannot init internal object with a null container");
        }
        setWrappedObject(containerWrapper.wrappedObject);
    }

    @Override
    protected void resetInternalField() {
        super.resetInternalField();
        addedChildren.clear();
        addedAliquots.clear();
    }

    @Override
    public boolean canEdit() {
        return super.canEdit()
            && SecurityHelper.isContainerAdministrator(appService);
    }

}
