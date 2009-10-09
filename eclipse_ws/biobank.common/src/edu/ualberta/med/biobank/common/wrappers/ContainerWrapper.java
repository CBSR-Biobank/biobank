package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
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
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends ModelWrapper<Container> {

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "productBarcode", "position", "activityStatus",
            "site", "label", "temperature", "comment",
            "samplePositionCollection", "childPositionCollection",
            "containerType" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        checkLabelUniqueForType();
        checkProductBarcodeUnique();
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
        ContainerPositionWrapper position = getPosition();
        if (position == null) {
            position = new ContainerPositionWrapper(appService,
                new ContainerPosition());
        }
        position.setParentContainer(containersWithLabel.get(0));
        position.setPosition(getLabel().substring(getLabel().length() - 2));
        setPosition(position);
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
        CapacityWrapper capacity = type.getCapacity();
        if (rcp.row < capacity.getRowCapacity()
            && rcp.col < capacity.getColCapacity()) {
            return rcp;
        }
        throw new Exception("Can't use position " + position + " in container "
            + getFullInfoLabel() + "\nReason: capacity = "
            + capacity.getRowCapacity() + "*" + capacity.getColCapacity());
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

    public ContainerTypeWrapper getContainerType() {
        ContainerType type = wrappedObject.getContainerType();
        if (type == null) {
            return null;
        }
        return new ContainerTypeWrapper(appService, type);
    }

    public ContainerPositionWrapper getPosition() {
        ContainerPosition pos = wrappedObject.getPosition();
        if (pos == null) {
            return null;
        }
        return new ContainerPositionWrapper(appService, pos);
    }

    public void setPosition(ContainerPositionWrapper position) {
        setPosition(position.getWrappedObject());
    }

    public void setPosition(ContainerPosition position) {
        ContainerPosition oldPosition = wrappedObject.getPosition();
        wrappedObject.setPosition(position);
        propertyChangeSupport.firePropertyChange("position", oldPosition,
            position);
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

    public void setSite(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(siteWrapper.getWrappedObject());
        propertyChangeSupport.firePropertyChange("site", oldSite, siteWrapper
            .getWrappedObject());
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

    /**
     * return a string with the label of this container + the short name of its
     * type
     */
    public String getFullInfoLabel() {
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
    public List<ContainerPositionWrapper> getChildPositionCollection() {
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

    public void setChildPositionCollection(
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

    public void setChildPositionCollection(
        List<ContainerPositionWrapper> positions) {
        Collection<ContainerPosition> positionsObjects = new HashSet<ContainerPosition>();
        for (ContainerPositionWrapper pos : positions) {
            positionsObjects.add(pos.getWrappedObject());
        }
        setChildPositionCollection(positionsObjects, false);
        propertiesMap.put("childPositionCollection", positions);
    }

    /**
     * Return true if this container can hold the type of sample
     */
    public boolean canHold(SampleWrapper sample) throws ApplicationException {
        SampleType type = sample.getSampleType();
        HQLCriteria criteria = new HQLCriteria("select sampleType from "
            + ContainerType.class.getName()
            + " as ct inner join ct.sampleTypeCollection as sampleType"
            + " where ct = ? and sampleType = ?", Arrays.asList(new Object[] {
            wrappedObject.getContainerType(), type }));
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
            if ((getContainerType() != null
                && getContainerType().getCapacity() != null
                && getContainerType().getCapacity().getRowCapacity() != null && getContainerType()
                .getCapacity().getColCapacity() != null)
                || getContainerType() == null)
                if ((getPosition() != null && getPosition().getRow() != null && getPosition()
                    .getCol() != null)
                    || getPosition() == null)
                    if (wrappedObject.getSite() != null)
                        return true;
        return false;

    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        if (getSamplePositionCollection().size() > 0
            || getChildPositionCollection().size() > 0) {
            throw new BiobankCheckException("Unable to delete container "
                + getLabel()
                + ". All subcontainers/samples must be removed first.");
        }
    }

    public void setNewParent(ContainerWrapper newParent, String newLabel)
        throws BiobankCheckException, Exception {
        // remove from old parent, add to new
        ContainerWrapper oldParent = getPosition().getParentContainer();
        if (oldParent != null) {
            checkFreePosition(newParent, newLabel);
            String oldLabel = getLabel();
            // remove from old
            List<ContainerPositionWrapper> oldPositions = oldParent
                .getChildPositionCollection();
            oldPositions.remove(getPosition());
            oldParent.setChildPositionCollection(oldPositions);

            // modify position object
            ContainerPositionWrapper positionWrapper = getPosition();
            positionWrapper.setParentContainer(newParent);
            positionWrapper.setPosition(newLabel
                .substring(newLabel.length() - 2));
            setPosition(positionWrapper);

            // add to new
            List<ContainerPositionWrapper> newPositions = newParent
                .getChildPositionCollection();
            newPositions.add(getPosition());
            newParent.setChildPositionCollection(newPositions);

            // change label
            if (getLabel().equalsIgnoreCase(getProductBarcode()))
                setProductBarcode(newLabel);
            setLabel(newLabel);

            persist();
            // move children
            setChildLabels(oldLabel);
        } else
            throw new BiobankCheckException(
                "You cannot move a top level container.");
    }

    private void checkFreePosition(ContainerWrapper newParent, String newLabel)
        throws BiobankCheckException {
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

    private void setChildLabels(String oldLabel) throws Exception {
        // FIXME inefficient, should be improved
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where label like ? and site= ?",
            Arrays.asList(new Object[] { oldLabel + "%",
                getSite().getWrappedObject() }));
        List<Container> containers = appService.query(criteria);
        for (Container c : containers) {
            if (c.getLabel().compareToIgnoreCase(oldLabel) == 0)
                continue;
            String nameEnd = c.getLabel().substring(oldLabel.length());
            c.setLabel(getLabel() + nameEnd);
            SDKQuery q = new UpdateExampleQuery(c);
            appService.executeQuery(q);
            new ContainerWrapper(appService, c).setChildLabels(oldLabel
                + nameEnd);
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
        String label, SampleType sampleType) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Container.class.getName()
                + " where site.id = ? and label = ? and containerType in (select parent from "
                + ContainerType.class.getName()
                + " as parent where parent.id in (select ct.id" + " from "
                + ContainerType.class.getName() + " as ct"
                + " left join ct.sampleTypeCollection as sampleType "
                + " where sampleType = ?))", Arrays.asList(new Object[] {
                siteWrapper.getId(), label, sampleType }));
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

    public static List<ContainerWrapper> getTopContainersForSite(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id=? and position is null", Arrays
            .asList(new Object[] { siteId }));
        List<Container> containers = appService.query(criteria);
        return transformToWrapperList(appService, containers);
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
        int rows = getContainerType().getCapacity().getRowCapacity().intValue();
        int cols = getContainerType().getCapacity().getColCapacity().intValue();
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
