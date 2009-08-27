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
        // TODO Auto-generated method stub
        return null;
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
    public static Container getContainerWithTypeInSite(
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
     * create the ContainerPosition for this container using its label and using
     * as top parent a container which type name start with topTypeNameStartWith
     */
    public void setNewPositionFromLabel(String topTypeNameStartWith)
        throws Exception {
        String parentContainerLabel = getLabel().substring(0,
            getLabel().length() - 2);
        String topParentContainerLabel = parentContainerLabel.substring(0, 2);
        Container topParentContainer = getContainerWithTypeInSite(appService,
            getSite(), topParentContainerLabel, topTypeNameStartWith);
        if (topParentContainer != null) {
            // we got the top parent Container in which we want to create a new
            // position in one of its child container
            int labelIndex = 4;
            Container currentParentContainer = topParentContainer;
            String currentLabel = "";
            // look for the parent container of this container
            while (labelIndex <= parentContainerLabel.length()
                && currentParentContainer != null) {
                currentLabel = parentContainerLabel.substring(0, labelIndex);
                currentParentContainer = new ContainerWrapper(appService,
                    currentParentContainer).getChildWithLabel(currentLabel);
                labelIndex += 2;
            }
            if (currentParentContainer == null) {
                throw new Exception("Can't find parent container with label "
                    + currentLabel + " under top container "
                    + topParentContainer.getLabel() + "("
                    + topParentContainer.getContainerType().getNameShort()
                    + ")");
            }

            // has the parent container. Can now find the position using the
            // parent labeling scheme
            ContainerPositionWrapper positionWrapper = new ContainerPositionWrapper(
                appService, new ContainerPosition());
            positionWrapper.setParentContainer(currentParentContainer);
            positionWrapper.setPosition(getLabel().substring(
                getLabel().length() - 2));
            setPosition(positionWrapper.getWrappedObject());
        } else {
            throw new Exception(
                "wans't able to find a container with type starting with "
                    + topTypeNameStartWith + " and label "
                    + topParentContainerLabel);
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
        if (rcp.row < capacity.getDimensionOneCapacity()
            && rcp.col < capacity.getDimensionTwoCapacity()) {
            return rcp;
        }
        throw new Exception("Can't use position " + position + " in container "
            + getFullInfoLabel() + "\nReason: capacity = "
            + capacity.getDimensionOneCapacity() + "*"
            + capacity.getDimensionTwoCapacity());
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
        if (getContainerType() == null) {
            return getLabel();
        }
        return getLabel() + "(" + getContainerType().getNameShort() + ")";
    }

}