package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerWrapper extends ModelWrapper<Container> {

    public ContainerWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);

    }

    @Override
    protected void firePropertyChanges(Container oldWrappedObject,
        Container newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        return DatabaseResult.OK;
    }

    @Override
    protected Class<Container> getWrappedClass() {
        return Container.class;
    }

    public Site getSite() {
        return wrappedObject.getSite();
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
     * Get one container with given label. If several containers exist with this
     * label, search for the one with type starting with startWithTypeName.
     * 
     * @throws Exception
     */
    public static Container getContainerWithTypeAndLabelInSite(
        WritableApplicationService appService, Site site, String label,
        String startWithTypeName) throws Exception {
        List<Container> containers = getContainersWithLabelInSite(appService,
            site, label);
        if (containers.size() == 1) {
            return containers.get(0);
        } else {
            // this is start of the container type name
            if (startWithTypeName != null) {
                HQLCriteria criteria = new HQLCriteria(
                    "from "
                        + Container.class.getName()
                        + " where site = ? and label = ? and containerType in (select type from "
                        + ContainerType.class.getName()
                        + " as type where name like ? and site = ?)", Arrays
                        .asList(new Object[] { site, label,
                            startWithTypeName + "%", site }));

                containers = appService.query(criteria);
                if (containers.size() == 1) {
                    return containers.get(0);
                } else {
                    if (containers.size() > 1) {
                        throw new Exception(
                            "Multiples containers registered in position "
                                + label
                                + " for container types name starting with "
                                + startWithTypeName);
                    }
                }
            }
        }
        return null;
    }

    /**
     * get the container with label label and type container type and form same
     * site that this containerWrapper
     * 
     * @param label label of the container
     * @param containerType the type of the container
     * @throws ApplicationException
     */
    public Container getContainer(String label, ContainerType containerType)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site = ? and label = ? and containerType = ?", Arrays
            .asList(new Object[] { getSite(), label, containerType }));

        List<Container> containers = appService.query(criteria);
        if (containers.size() == 1) {
            return containers.get(0);
        }
        return null;
    }

    public static List<Container> getContainersWithLabelInSite(
        WritableApplicationService appService, Site site, String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site = ? and label = ?",
            Arrays.asList(new Object[] { site, label }));
        return appService.query(criteria);
    }

    /**
     * if address exists if address is not full if type is valid for slot modify
     * this object's position, label, children
     */
    public void setNewPositionFromLabel(String newAddress) throws Exception {
        if (newAddress.length() < 2)
            throw new Exception(
                "Destination address must be another container.");
        String newParentContainerLabel = newAddress.substring(0, newAddress
            .length() - 2);

        List<Container> newParentContainers = getContainersWithLabelInSite(
            appService, getSite(), newParentContainerLabel);
        String oldLabel = getLabel();

        if (newParentContainers.size() != 1) {
            // invalid parent
            throw new Exception("Unable to find parent container with label "
                + newParentContainerLabel + ".");
        } else {
            List<Container> samePositions = getContainersWithLabelInSite(
                appService, getSite(), newAddress);
            if (samePositions.size() != 0) {
                // filled
                throw new Exception(
                    "The destination "
                        + newAddress
                        + " has already been initialized. You can only move to an uninitialized location.");
            } else {
                // remove from old parent, add to new
                List<Container> oldParentContainers = getContainersWithLabelInSite(
                    appService, getSite(), getLabel().substring(0,
                        getLabel().length() - 2));
                if (oldParentContainers.size() > 0) {
                    // parents
                    Container oldParent = oldParentContainers.get(0);
                    Container newParent = newParentContainers.get(0);

                    // remove from old
                    Collection<ContainerPosition> oldPositions = oldParent
                        .getChildPositionCollection();
                    oldPositions.remove(getPosition());
                    oldParent.setChildPositionCollection(oldPositions);

                    // modify position object
                    ContainerPositionWrapper positionWrapper = new ContainerPositionWrapper(
                        appService, getPosition());
                    positionWrapper.setParentContainer(newParent);
                    positionWrapper.setPosition(newAddress.substring(newAddress
                        .length() - 2));
                    setPosition(positionWrapper.getWrappedObject());

                    // add to new
                    Collection<ContainerPosition> newPositions = newParent
                        .getChildPositionCollection();
                    newPositions.add(getPosition());
                    newParent.setChildPositionCollection(newPositions);

                    // change label
                    if (getLabel().equalsIgnoreCase(getProductBarcode()))
                        setProductBarcode(newAddress);
                    setLabel(newAddress);

                    SDKQuery q = new UpdateExampleQuery(wrappedObject);
                    this.appService.executeQuery(q);
                    // move children
                    setChildLabels(oldLabel);
                } else
                    throw new Exception(
                        "You cannot move a top level container.");
            }
        }
    }

    private void setChildLabels(String oldLabel) throws Exception {
        // inefficient, should be improved
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where label like '" + oldLabel
            + "%'" + " and site= " + getSite().getId());

        List<Container> containers = appService.query(criteria);
        for (Container container : containers) {
            if (container.getLabel().compareToIgnoreCase(oldLabel) == 0)
                continue;
            ContainerWrapper temp = new ContainerWrapper(appService, container);
            temp.setLabel(getLabel()
                + container.getLabel().substring(getLabel().length()));
            SDKQuery q = new UpdateExampleQuery(temp.getWrappedObject());
            this.appService.executeQuery(q);
            temp.setChildLabels(oldLabel
                + container.getLabel().substring(getLabel().length()));
        }
    }

    /**
     * Get the child container of this container with label label
     */
    public Container getChildWithLabel(String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where position.parentContainer = ? and label = ?", Arrays
            .asList(new Object[] { wrappedObject, label }));

        List<Container> containers = appService.query(criteria);
        if (containers.size() == 1) {
            return containers.get(0);
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
        ContainerType type = getContainerType();
        RowColPos rcp = LabelingScheme.getRowColFromPositionString(position,
            type);
        Capacity capacity = type.getCapacity();
        if (rcp.row < capacity.getRowCapacity()
            && rcp.col < capacity.getColCapacity()) {
            return rcp;
        }
        throw new Exception("Can't use position " + position + " in container "
            + getFullInfoLabel() + "\nReason: capacity = "
            + capacity.getRowCapacity() + "*" + capacity.getColCapacity());
    }

    public void setContainerType(ContainerType containerType) {
        ContainerType oldType = getContainerType();
        wrappedObject.setContainerType(containerType);
        propertyChangeSupport.firePropertyChange("containerType", oldType,
            containerType);
    }

    public ContainerType getContainerType() {
        return wrappedObject.getContainerType();
    }

    public ContainerPosition getPosition() {
        return wrappedObject.getPosition();
    }

    public void setPosition(ContainerPosition position) {
        ContainerPosition oldPosition = position;
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

    private String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setSite(Site site) {
        Site oldSite = getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setLabel(String label) {
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public Collection<SamplePosition> getSamplePositionCollection() {
        return wrappedObject.getSamplePositionCollection();
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

    public static Container getContainerWithProductBarcodeInSite(
        WritableApplicationService appService, Site site, String productBarcode)
        throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site = ? and productBarcode = ?", Arrays
            .asList(new Object[] { site, productBarcode }));
        List<Container> containers = appService.query(criteria);
        if (containers.size() == 0) {
            return null;
        } else if (containers.size() > 1) {
            throw new Exception(
                "Multiples containers registered with product barcode "
                    + productBarcode);
        }
        return containers.get(0);
    }

    public void setTemperature(Double temperature) {
        Double oldTemp = getTemperature();
        wrappedObject.setTemperature(temperature);
        propertyChangeSupport.firePropertyChange("position", oldTemp,
            temperature);
    }

    private Double getTemperature() {
        return getWrappedObject().getTemperature();

    }

    public Collection<ContainerPosition> getChildPositionCollection() {
        return getWrappedObject().getChildPositionCollection();
    }

}