package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerPositionWrapper extends
    AbstractPositionWrapper<ContainerPosition> {

    public ContainerPositionWrapper(WritableApplicationService appService,
        ContainerPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        List<String> properties = new ArrayList<String>(Arrays.asList(super
            .getPropertyChangesNames()));
        properties.add("parentContainer");
        properties.add("container");
        return properties.toArray(new String[properties.size()]);
    }

    @Override
    public Class<ContainerPosition> getWrappedClass() {
        return ContainerPosition.class;
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
    }

    @Override
    public int compareTo(ModelWrapper<ContainerPosition> modelWrapper) {
        if (modelWrapper instanceof ContainerPositionWrapper) {
            return getContainer().compareTo(
                ((ContainerPositionWrapper) modelWrapper).getContainer());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "[" + getRow() + ", " + getCol() + "] "
            + getContainer().toString();
    }

}
