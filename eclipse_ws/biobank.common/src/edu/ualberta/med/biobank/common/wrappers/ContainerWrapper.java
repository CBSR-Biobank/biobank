package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
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
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends
    AbstractPositionHolder<Container, ContainerPosition> {

    private static Logger LOGGER = Logger.getLogger(ContainerWrapper.class
        .getName());

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
            "samplePositionCollection", "samples", "childPositionCollection",
            "children", "containerType", "parent" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkSiteNotNull();
        checkContainerTypeNotNull();
        // TODO check type is from same site ?
        // TODO check parent is from same site ?
        checkLabelUniqueForType();
        checkProductBarcodeUnique();
        canHoldCTs();
        super.persistChecks();
    }

    @Override
    protected void persistDependencies(Container origObject)
        throws BiobankCheckException, ApplicationException, WrapperException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            if (isNew()
                || (origObject != null && origObject.getPosition() != null && ((origObject
                    .getPosition().getParentContainer() != null && origObject
                    .getPosition().getParentContainer().getId() != parent
                    .getId()) || (new RowColPos(origObject.getPosition()
                    .getRow(), origObject.getPosition().getCol())
                    .equals(getPosition()))))) {
                String label = parent.getLabel()
                    + LabelingScheme.getPositionString(this);
                setLabel(label);
            }
        }
        persistChildren();
        persistSamples();
    }

    private void persistSamples() throws BiobankCheckException,
        ApplicationException, WrapperException {
        Map<RowColPos, SampleWrapper> samples = getSamples();
        if (samples != null) {
            for (SampleWrapper sample : samples.values()) {
                sample.setParent(this);
                sample.persist();
            }
        }
    }

    private void persistChildren() throws BiobankCheckException,
        ApplicationException, WrapperException {
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children != null) {
            for (ContainerWrapper container : children.values()) {
                container.setParent(this);
                container.persist();
            }
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

    private void checkContainerTypeNotNull() throws BiobankCheckException {
        if (getContainerType() == null) {
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
     * compute the ContainerPosition for this container using its label. If the
     * parent container cannot hold the container type of this container, then
     * an exception is launched
     * 
     * FIX: is this required once code is added to compute child labels?
     */
    public void computePositionFromLabel() throws Exception {
        String parentContainerLabel = getLabel().substring(0,
            getLabel().length() - 2);
        List<ContainerWrapper> possibleParents = ContainerWrapper
            .getContainersHoldingContainerType(appService,
                parentContainerLabel, getSite(), getContainerType());

        if (possibleParents.size() == 0) {
            throw new Exception("Can't find container with label "
                + parentContainerLabel + " holding containers of type "
                + getContainerType().getName());
        }
        if (possibleParents.size() > 1) {
            throw new Exception(
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
        possibleParents.get(0).addChild(
            getLabel().substring(getLabel().length() - 2), this);

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
                throw new Exception("Position " + position
                    + " is invalid in container " + getFullInfoLabel());
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
    public Map<RowColPos, SampleWrapper> getSamples() {
        Map<RowColPos, SampleWrapper> samples = (Map<RowColPos, SampleWrapper>) propertiesMap
            .get("samples");
        if (samples == null) {
            Collection<SamplePosition> positions = wrappedObject
                .getSamplePositionCollection();
            if (positions != null) {
                samples = new HashMap<RowColPos, SampleWrapper>();
                for (SamplePosition position : positions) {
                    samples.put(new RowColPos(position.getRow(), position
                        .getCol()), new SampleWrapper(appService, position
                        .getSample()));
                }
                propertiesMap.put("samples", samples);
            }
        }
        return samples;
    }

    public boolean hasSamples() {
        Collection<SamplePosition> positions = wrappedObject
            .getSamplePositionCollection();
        return ((positions != null) && (positions.size() > 0));
    }

    public SampleWrapper getSample(Integer row, Integer col) {
        Map<RowColPos, SampleWrapper> samples = getSamples();
        if (samples == null) {
            return null;
        }
        return samples.get(new RowColPos(row, col));
    }

    public void addSample(Integer row, Integer col, SampleWrapper sample)
        throws BiobankCheckException {
        Map<RowColPos, SampleWrapper> samples = getSamples();
        if (samples == null) {
            samples = new HashMap<RowColPos, SampleWrapper>();
            propertiesMap.put("samples", samples);
        } else
            try {
                if (!canHoldSample(sample)) {
                    throw new BiobankCheckException("Container "
                        + getFullInfoLabel()
                        + " does not allow inserts of type "
                        + sample.getSampleType().getName() + ".");
                } else {
                    SampleWrapper sampleAtPosition = getSample(row, col);
                    if (sampleAtPosition != null) {
                        throw new BiobankCheckException("Container "
                            + getFullInfoLabel()
                            + " is already holding a sample at position "
                            + sampleAtPosition.getPositionString(false) + " ("
                            + row + ":" + col + ")");
                    }
                }
            } catch (ApplicationException e) {
                LOGGER.error("Adding sample failed. " + "\n" + e.toString());
            }
        sample.setPosition(row, col);
        sample.setParent(this);
        samples.put(new RowColPos(row, col), sample);
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
    public Map<RowColPos, ContainerWrapper> getChildren() {
        Map<RowColPos, ContainerWrapper> children = (Map<RowColPos, ContainerWrapper>) propertiesMap
            .get("children");
        if (children == null) {
            Collection<ContainerPosition> positions = wrappedObject
                .getChildPositionCollection();
            if (positions != null) {
                children = new HashMap<RowColPos, ContainerWrapper>();
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
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children == null) {
            return null;
        }
        return children.get(new RowColPos(row, col));
    }

    public void canHoldCTs() throws BiobankCheckException {
        if (getContainerType().getTopLevel())
            return;

        boolean found = false;
        List<ContainerTypeWrapper> allowed = getParent().getContainerType()
            .getChildContainerTypeCollection();
        for (ContainerTypeWrapper ct : allowed)
            if (ct.getId().equals(getContainerType().getId()))
                found = true;
        if (!found)
            throw new BiobankCheckException("Container "
                + getParent().getFullInfoLabel()
                + " does not allow inserts of type "
                + getContainerType().getName() + ".");
    }

    public void addChild(Integer row, Integer col, ContainerWrapper child)
        throws BiobankCheckException {
        Map<RowColPos, ContainerWrapper> children = getChildren();
        if (children == null) {
            children = new HashMap<RowColPos, ContainerWrapper>();
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
    }

    public void addChild(String string, ContainerWrapper container)
        throws Exception {
        RowColPos position = getPositionFromLabelingScheme(string);
        addChild(position.row, position.col, container);
    }

    /**
     * Return true if this container can hold the type of sample
     */
    public boolean canHoldSample(SampleWrapper sample)
        throws ApplicationException {
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
        if (hasSamples() || hasChildren()) {
            throw new BiobankCheckException("Unable to delete container "
                + getLabel()
                + ". All subcontainers/samples must be removed first.");
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
     * get containers with label label in site which can have children of type
     * container type
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
     * @throws BiobankCheckException
     * @throws WrapperException
     * @throws ApplicationException
     */
    public void initChildrenWithType(ContainerTypeWrapper type)
        throws BiobankCheckException, ApplicationException, WrapperException {
        int rows = getContainerType().getRowCapacity().intValue();
        int cols = getContainerType().getColCapacity().intValue();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Boolean filled = (getChild(i, j) != null);
                if (!filled) {
                    ContainerWrapper newContainer = new ContainerWrapper(
                        appService);
                    newContainer.setContainerType(type.getWrappedObject());
                    newContainer.setSite(getSite().getWrappedObject());
                    newContainer.setTemperature(getTemperature());
                    addChild(i, j, newContainer);
                }
            }
        }
        persist();
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
        for (ContainerWrapper pos : getChildren().values()) {
            if (pos.getContainerType().equals(type)) {
                pos.deleteChecks();
                queries.add(new DeleteExampleQuery(pos.getWrappedObject()));
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

}
