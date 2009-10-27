package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

    private void setParentContainer(ContainerWrapper parentContainer) {
        setParentContainer(parentContainer.getWrappedObject());
    }

    private ContainerWrapper getParentContainer() {
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

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
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

    @Override
    public ContainerWrapper getParent() {
        return getParentContainer();
    }

    @Override
    public void setParent(ContainerWrapper parent) {
        setParentContainer(parent);
    }

    @Override
    protected void checkObjectAtPosition() throws ApplicationException,
        BiobankCheckException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            // do a hql query because parent might need a reload - but if we are
            // in the middle of parent.persist, don't want to do that !
            HQLCriteria criteria = new HQLCriteria("from "
                + ContainerPosition.class.getName()
                + " where parentContainer.id=? and row=? and col=?", Arrays
                .asList(new Object[] { parent.getId(), getRow(), getCol() }));
            List<ContainerPosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            ContainerPositionWrapper childPosition = new ContainerPositionWrapper(
                appService, positions.get(0));
            if (!childPosition.getContainer().equals(getContainer())) {
                throw new BiobankCheckException("Position "
                    + childPosition.getContainer().getLabel() + " (" + getRow()
                    + ":" + getCol() + ") in container "
                    + getParent().toString()
                    + " is not available in container "
                    + parent.getFullInfoLabel());
            }
        }
    }
}
