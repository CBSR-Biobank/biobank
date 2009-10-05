package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerPositionWrapper extends
    AbstractPositionWrapper<ContainerPosition> implements
    Comparable<ContainerPositionWrapper> {

    public ContainerPositionWrapper(WritableApplicationService appService,
        ContainerPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        List<String> properties = Arrays
            .asList(super.getPropertyChangesNames());
        properties.add("parentContainer");
        properties.add("container");
        return (String[]) properties.toArray();
    }

    @Override
    protected Class<ContainerPosition> getWrappedClass() {
        return ContainerPosition.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    public void setParentContainer(Container parentContainer) {
        Container oldParent = wrappedObject.getParentContainer();
        wrappedObject.setParentContainer(parentContainer);
        propertyChangeSupport.firePropertyChange("parentContainer", oldParent,
            parentContainer);
    }

    public void setParentContainer(ContainerWrapper parentContainer) {
        setParentContainer(parentContainer.getWrappedObject());
    }

    public ContainerWrapper getParentContainer() {
        Container parent = wrappedObject.getParentContainer();
        if (parent == null) {
            return null;
        }
        return new ContainerWrapper(appService, parent);
    }

    public ContainerWrapper getContainer() {
        Container container = wrappedObject.getContainer();
        if (container == null) {
            return null;
        }
        return new ContainerWrapper(appService, container);
    }

    public void setContainer(ContainerWrapper container) {
        setContainer(container.getWrappedObject());
    }

    public void setContainer(Container container) {
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    /**
     * position is 2 letters, or 2 number or 1 letter and 1 number... this
     * position string is used to set the correct row and column index for this
     * position. Labeling scheme of parent container is used to get the correct
     * indexes.
     * 
     * @throws Exception
     */
    public void setPosition(String position) throws Exception {
        ContainerWrapper parent = getParentContainer();
        RowColPos rowColPos = parent.getPositionFromLabelingScheme(position);
        setRow(rowColPos.row);
        setCol(rowColPos.col);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public int compareTo(ContainerPositionWrapper o) {
        return getContainer().compareTo(o.getContainer());
    }

}
