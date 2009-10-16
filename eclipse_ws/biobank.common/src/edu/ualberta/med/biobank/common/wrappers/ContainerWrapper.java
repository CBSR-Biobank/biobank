package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SamplePositionWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends ModelWrapper<Container> {

    private ContainerPositionWrapper containerPosition;
    private Position position;

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
        ContainerPosition pos = wrappedObject.getPosition();
        if (pos != null) {
            containerPosition = new ContainerPositionWrapper(appService, pos);
            position = new Position(containerPosition.getRow(),
                containerPosition.getCol());
        }
    }

    public ContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "productBarcode", "position", "activityStatus",
            "site", "label", "temperature", "comment",
            "samplePositionCollection", "samples", "childPositionCollection",
            "children", "containerType", "parent" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        checkSiteNotNull();
        checkLabelUniqueForType();
        checkProductBarcodeUnique();
        if (containerPosition != null) {
            containerPosition.persistChecks();
        }
    }

    @Override
    public void persist() throws BiobankCheckException, Exception {
        if (containerPosition != null) {
            wrappedObject.setPosition(containerPosition.getWrappedObject());
        }
        super.persist();
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (containerPosition != null) {
            containerPosition.reset();
        }
    }

    private void checkProductBarcodeUnique() throws BiobankCheckException,
        ApplicationException {
        List<Object> parameters = new ArrayList<Object>(Arrays
            .asList(new Object[] { getSite().getId(), getProductBarcode() }));
        String notSameContainer = "";
        if (!isNew()) {
            notSameContainer = " and id <> ?";
            parameters.add(getId());
        }
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id=? and productBarcode=?" + notSameContainer,
            parameters);
        List<Object> results = appService.query(criteria);
        if (results.size() > 0) {
            throw new BiobankCheckException(
                "A container with product barcode \"" + getProductBarcode()
                    + "\" already exists.");
        }
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

    @Override
    public Class<Container> getWrappedClass() {
        return Container.class;
    }

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

    /**
     * get the container with label label and type container type and from same
     * site that this containerWrapper
     * 
     * @param label label of the container
     * @param containerType the type of the container
     * @throws ApplicationException
     */
    public ContainerWrapper getContainer(String label,
        ContainerTypeWrapper containerType) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and label = ? and containerType = ?", Arrays
            .asList(new Object[] { wrappedObject.getSite().getId(), label,
                containerType.wrappedObject }));
        List<Container> containers = appService.query(criteria);
        if (containers.size() == 1) {
            return new ContainerWrapper(appService, containers.get(0));
        }
        return null;
    }

    /**
     * get the containers with label label and from same site that this
     * containerWrapper
     * 
     * @param label label of the container
     * @throws ApplicationException
     */
    public List<Container> getContainers(String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from " + Container.class.getName()
                + " where site.id = ? and label = ?",
            Arrays
                .asList(new Object[] { wrappedObject.getSite().getId(), label }));
        return appService.query(criteria);
    }

    /**
     * get the containers with label label and from same site that this
     * containerWrapper and holding this container type
     * 
     * @param label label of the container
     * @throws ApplicationException
     */
    public List<ContainerWrapper> getContainersHoldingContainerType(String label)
        throws ApplicationException {
        return getContainersHoldingContainerType(appService, label, getSite(),
            getContainerType());
    }

    /**
     * compute the ContainerPosition for this container using its label. If the
     * parent container cannot hold the container type of this container, then
     * an exception is launched
     */
    public void computePositionFromLabel() throws Exception {
        String parentContainerLabel = getLabel().substring(0,
            getLabel().length() - 2);
        List<ContainerWrapper> containersWithLabel = getContainersHoldingContainerType(parentContainerLabel);
        if (containersWithLabel.size() == 0) {
            throw new Exception("Can't find container with label "
                + parentContainerLabel + " holding containers of type "
                + getContainerType().getName());
        }
        if (containersWithLabel.size() > 1) {
            throw new Exception(
                containersWithLabel.size()
                    + " containers with label "
                    + parentContainerLabel
                    + " and holding container types "
                    + getContainerType().getName()
                    + " have been found. This is ambiguous: check containers definitions.");
        }
        // has the parent container. Can now find the position using the
        // parent labelling scheme
        setParent(containersWithLabel.get(0));
        setPosition(getLabel().substring(getLabel().length() - 2));
    }

    /**
     * Get the child container of this container with label label
     */
    public ContainerWrapper getChildWithLabel(String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where position.parentContainer = ? and label = ?", Arrays
            .asList(new Object[] { wrappedObject, label }));

        List<Container> containers = appService.query(criteria);
        if (containers.size() == 1) {
            return new ContainerWrapper(appService, containers.get(0));
        }
        return null;
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
            type.getWrappedObject());
        if (rcp.row >= type.getRowCapacity()
            || rcp.col >= type.getColCapacity()) {
            throw new Exception("Can't use position " + position
                + " in container " + getFullInfoLabel()
                + "\nReason: capacity = " + type.getRowCapacity() + "*"
                + type.getColCapacity());
        }
        if (rcp.row < 0 || rcp.col < 0) {
            throw new Exception("Position " + position
                + " is invalid in container " + getFullInfoLabel());
        }
        return rcp;
    }

    public void setContainerType(ContainerTypeWrapper containerType) {
        setContainerType(containerType.getWrappedObject());
    }

    public void setContainerType(ContainerType containerType) {
        ContainerType oldType = wrappedObject.getContainerType();
        wrappedObject.setContainerType(containerType);
        propertyChangeSupport.firePropertyChange("containerType", oldType,
            containerType);
    }

    public ContainerTypeWrapper getContainerType() throws ApplicationException {
        ContainerType type = wrappedObject.getContainerType();
        if (type == null) {
            return null;
        }
        return new ContainerTypeWrapper(appService, type);
    }

    public Position getPosition() {
        if (containerPosition == null) {
            return null;
        }
        return position;
    }

    public void setPosition(Position position) {
        Position oldPosition = this.position;
        if (containerPosition == null) {
            initContainerPosition();
        }
        containerPosition.setRow(position.row);
        containerPosition.setCol(position.col);
        this.position = position;
        propertyChangeSupport.firePropertyChange("position", oldPosition,
            position);
    }

    public void setPosition(String positionAsString) throws Exception {
        if (containerPosition == null) {
            initContainerPosition();
        }
        containerPosition.setPosition(positionAsString);
        position.row = containerPosition.getRow();
        position.col = containerPosition.getCol();
    }

    private void initContainerPosition() {
        containerPosition = new ContainerPositionWrapper(appService);
        position = new Position();
        containerPosition.setContainer(this);
        wrappedObject.setPosition(containerPosition.getWrappedObject());
    }

    public ContainerWrapper getParent() {
        if (containerPosition == null) {
            return null;
        }
        return containerPosition.getParentContainer();
    }

    public void setParent(ContainerWrapper parent) {
        ContainerWrapper oldValue = null;
        if (containerPosition == null) {
            initContainerPosition();
        } else {
            oldValue = containerPosition.getParentContainer();
        }
        containerPosition.setParentContainer(parent);
        propertyChangeSupport.firePropertyChange("parent", oldValue, parent);
    }

    public boolean hasParent() {
        return containerPosition != null;
    }

    public void setActivityStatus(String activityStatus) {
        String oldActivityStatus = getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper siteWrapper) {
        setSite(siteWrapper.getWrappedObject());
    }

    public void setLabel(String label) {
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    @SuppressWarnings("unchecked")
    public List<SamplePositionWrapper> getSamplePositionCollection() {
        List<SamplePositionWrapper> samplePositionCollection = (List<SamplePositionWrapper>) propertiesMap
            .get("samplePositionCollection");
        if (samplePositionCollection == null) {
            Collection<SamplePosition> children = wrappedObject
                .getSamplePositionCollection();
            if (children != null) {
                samplePositionCollection = new ArrayList<SamplePositionWrapper>();
                for (SamplePosition position : children) {
                    samplePositionCollection.add(new SamplePositionWrapper(
                        appService, position));
                }
                propertiesMap.put("samplePositionCollection",
                    samplePositionCollection);
            }
        }
        return samplePositionCollection;
    }

    public void setSamplePositionCollection(
        Collection<SamplePosition> positions, boolean setNull) {
        Collection<SamplePosition> oldPositions = wrappedObject
            .getSamplePositionCollection();
        wrappedObject.setSamplePositionCollection(positions);
        propertyChangeSupport.firePropertyChange("samplePositionCollection",
            oldPositions, positions);
        if (setNull) {
            propertiesMap.put("samplePositionCollection", null);
        }
    }

    public void setSamplePositionCollection(
        List<SamplePositionWrapper> positions) {
        Collection<SamplePosition> positionsObjects = new HashSet<SamplePosition>();
        for (SamplePositionWrapper pos : positions) {
            positionsObjects.add(pos.getWrappedObject());
        }
        setSamplePositionCollection(positionsObjects, false);
        propertiesMap.put("samplePositionCollection", positions);
    }

    @SuppressWarnings("unchecked")
    public List<SampleWrapper> getSamples() {
        List<SampleWrapper> samples = (List<SampleWrapper>) propertiesMap
            .get("samples");
        if (samples == null) {
            Collection<SamplePosition> positions = wrappedObject
                .getSamplePositionCollection();
            if (positions != null) {
                samples = new ArrayList<SampleWrapper>();
                for (SamplePosition position : positions) {
                    samples.add(new SampleWrapper(appService, position
                        .getSample()));
                }
                propertiesMap.put("samples", samples);
            }
        }
        return samples;
    }

    /**
     * return a string with the label of this container + the short name of its
     * type
     * 
     * @throws ApplicationException
     */
    public String getFullInfoLabel() throws ApplicationException {
        if (getContainerType() == null
            || getContainerType().getNameShort() == null) {
            return getLabel();
        }
        return getLabel() + "(" + getContainerType().getNameShort() + ")";
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
    private List<ContainerPositionWrapper> getChildPositionCollection() {
        List<ContainerPositionWrapper> childPositionCollection = (List<ContainerPositionWrapper>) propertiesMap
            .get("childPositionCollection");
        if (childPositionCollection == null) {
            Collection<ContainerPosition> children = wrappedObject
                .getChildPositionCollection();
            if (children != null) {
                childPositionCollection = new ArrayList<ContainerPositionWrapper>();
                for (ContainerPosition position : children) {
                    childPositionCollection.add(new ContainerPositionWrapper(
                        appService, position));
                }
                propertiesMap.put("childPositionCollection",
                    childPositionCollection);
            }
        }
        return childPositionCollection;
    }

    private void setChildPositionCollection(
        Collection<ContainerPosition> positions, boolean setNull) {
        Collection<ContainerPosition> oldPositions = wrappedObject
            .getChildPositionCollection();
        wrappedObject.setChildPositionCollection(positions);
        propertyChangeSupport.firePropertyChange("childPositionCollection",
            oldPositions, positions);
        if (setNull) {
            propertiesMap.put("childPositionCollection", null);
        }
    }

    private void setChildPositionCollection(
        List<ContainerPositionWrapper> positions) {
        Collection<ContainerPosition> positionsObjects = new HashSet<ContainerPosition>();
        for (ContainerPositionWrapper pos : positions) {
            positionsObjects.add(pos.getWrappedObject());
        }
        setChildPositionCollection(positionsObjects, false);
        propertiesMap.put("childPositionCollection", positions);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getChildren() {
        List<ContainerWrapper> children = (List<ContainerWrapper>) propertiesMap
            .get("children");
        if (children == null) {
            Collection<ContainerPosition> positions = wrappedObject
                .getChildPositionCollection();
            if (positions != null) {
                children = new ArrayList<ContainerWrapper>();
                for (ContainerPosition position : positions) {
                    children.add(new ContainerWrapper(appService, position
                        .getContainer()));
                }
                propertiesMap.put("children", children);
            }
        }
        return children;
    }

    public boolean hasChildren() {
        Collection<ContainerPosition> positions = wrappedObject
            .getChildPositionCollection();
        return positions != null && positions.size() > 0;
    }

    /**
     * Return true if this container can hold the type of sample
     */
    public boolean canHold(SampleWrapper sample) throws ApplicationException {
        SampleTypeWrapper type = sample.getSampleType();
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
    public boolean checkIntegrity() throws ApplicationException {
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
    protected void deleteChecks() throws BiobankCheckException, Exception {
        List<SamplePositionWrapper> spCollection = getSamplePositionCollection();
        List<ContainerPositionWrapper> childCollection = getChildPositionCollection();

        if (((spCollection != null) && (spCollection.size() > 0))
            || ((childCollection != null) && (childCollection.size() > 0))) {
            throw new BiobankCheckException("Unable to delete container "
                + getLabel()
                + ". All subcontainers/samples must be removed first.");
        }
    }

    public void assignNewParent(ContainerWrapper newParent, String newLabel)
        throws BiobankCheckException, Exception {
        // remove from old parent, add to new
        ContainerWrapper oldParent = containerPosition.getParentContainer();
        if (oldParent != null) {
            checkFreePosition(newParent, newLabel);
            String oldLabel = getLabel();
            // remove from old
            List<ContainerPositionWrapper> oldPositions = oldParent
                .getChildPositionCollection();
            oldPositions.remove(containerPosition);
            oldParent.setChildPositionCollection(oldPositions);

            // modify position object
            setParent(newParent);
            setPosition(newLabel.substring(newLabel.length() - 2));

            // add to new
            List<ContainerPositionWrapper> newPositions = newParent
                .getChildPositionCollection();
            newPositions.add(containerPosition);
            newParent.setChildPositionCollection(newPositions);

            // change label
            if (getLabel().equalsIgnoreCase(getProductBarcode()))
                setProductBarcode(newLabel);
            setLabel(newLabel);

            persist();
            // move children
            assignChildLabels(oldLabel);
        } else
            throw new BiobankCheckException(
                "You cannot move a top level container.");
    }

    private void checkFreePosition(ContainerWrapper newParent, String newLabel)
        throws BiobankCheckException, ApplicationException {
        List<ContainerPositionWrapper> positions = newParent
            .getChildPositionCollection();
        Boolean filled = false;
        // check that the position is free
        for (ContainerPositionWrapper pos : positions)
            if (pos.getContainer().getLabel().compareToIgnoreCase(newLabel) == 0)
                filled = true;
        if (filled) {
            // filled
            throw new BiobankCheckException(
                "The destination "
                    + newLabel
                    + " in container "
                    + newParent.getFullInfoLabel()
                    + " has already been initialized. You can only move to an uninitialized location.");
        }
    }

    public void assignChildLabels(String oldLabel) throws Exception {
        for (ContainerWrapper c : getChildren()) {
            String nameEnd = c.getLabel().substring(oldLabel.length());
            c.setLabel(getLabel() + nameEnd);
            c.persist();
            c.assignChildLabels(oldLabel + nameEnd);
        }
    }

    /**
     * get a list of all containers that are above this container in the
     * hierarchy
     */
    public List<ContainerWrapper> getAllParents() {
        List<ContainerWrapper> list = new ArrayList<ContainerWrapper>();
        ContainerPositionWrapper position = containerPosition;
        if (position != null) {
            ContainerWrapper parent = position.getParentContainer();
            list.add(parent);
            list.addAll(parent.getAllParents());
        }
        return list;
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
     * get the containers with label label, with site site and container type
     * type
     */
    public static List<ContainerWrapper> getContainersHoldingContainerType(
        WritableApplicationService appService, String label, SiteWrapper site,
        ContainerTypeWrapper type) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Container.class.getName()
                + " where site.id = ? and label = ? and containerType in (select parent from "
                + ContainerType.class.getName()
                + " as parent where parent.id in (select ct.id" + " from "
                + ContainerType.class.getName() + " as ct"
                + " left join ct.childContainerTypeCollection as child "
                + " where child = ?))", Arrays.asList(new Object[] {
                site.getId(), label, type.wrappedObject }));
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
     * Initialize all children of this container with the given type (except
     * children already initialized)
     * 
     * @return true if at least one children has been initialized
     */
    public boolean initChildrenWithType(ContainerTypeWrapper type)
        throws ApplicationException {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();
        Collection<ContainerPositionWrapper> positions = getChildPositionCollection();
        int rows = getContainerType().getRowCapacity().intValue();
        int cols = getContainerType().getColCapacity().intValue();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Boolean filled = false;
                for (ContainerPositionWrapper pos : positions) {
                    if (pos.getRow().intValue() == i
                        && pos.getCol().intValue() == j) {
                        filled = true;
                        break;
                    }
                }
                if (!filled) {
                    Container newContainer = new Container();
                    newContainer.setContainerType(type.getWrappedObject());
                    newContainer.setSite(getSite().getWrappedObject());
                    newContainer.setTemperature(getTemperature());

                    ContainerPosition newPos = new ContainerPosition();
                    newPos.setRow(new Integer(i));
                    newPos.setCol(new Integer(j));
                    newPos.setParentContainer(getWrappedObject());
                    newContainer.setPosition(newPos);
                    newContainer.setLabel(getLabel()
                        + LabelingScheme.getPositionString(newPos));
                    queries.add(new InsertExampleQuery(newContainer));
                }
            }
        }
        if (queries.size() > 0) {
            appService.executeBatchQuery(queries);
            return true;
        }
        return false;
    }

    /**
     * Delete all children of this container with the given type
     * 
     * @return true if at least one children has been deleted
     * @throws Exception
     * @throws BiobankCheckException
     */
    public boolean deleteChildrenWithType(ContainerTypeWrapper type)
        throws BiobankCheckException, Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();
        Collection<ContainerPositionWrapper> positions = getChildPositionCollection();
        for (ContainerPositionWrapper pos : positions) {
            ContainerWrapper deletingContainer = pos.getContainer();
            if (deletingContainer.getContainerType().equals(type)) {
                deletingContainer.deleteChecks();
                queries.add(new DeleteExampleQuery(deletingContainer
                    .getWrappedObject()));
            }
        }
        if (queries.size() > 0) {
            appService.executeBatchQuery(queries);
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(ModelWrapper<Container> wrapper) {
        String c1Name = wrappedObject.getLabel();
        String c2Name = wrapper.wrappedObject.getLabel();
        return ((c1Name.compareTo(c2Name) > 0) ? 1 : (c1Name.equals(c2Name) ? 0
            : -1));
    }

}
