package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class AbstractObjectWithPositionManagement<T extends AbstractPosition, E extends ModelWrapper<?>> {
    protected RowColPos rowColPosition;
    private AbstractPositionWrapper<T> positionWrapper;

    // used to allow position to be assigned to null
    protected boolean nullPositionSet = false;

    private ContainerWrapper parent;

    private E objectAtPosition;

    protected AbstractObjectWithPositionManagement(E objectAtPosition) {
        this.objectAtPosition = objectAtPosition;
    }

    protected void persist() {
        boolean origPositionSet = (!nullPositionSet && (rowColPosition != null));
        AbstractPositionWrapper<T> posWrapper = getPositionWrapper(origPositionSet);
        if ((posWrapper != null) && origPositionSet) {
            posWrapper.setRow(rowColPosition.row);
            posWrapper.setCol(rowColPosition.col);
        }
    }

    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        boolean origPositionSet = (!nullPositionSet && rowColPosition != null);
        AbstractPositionWrapper<T> posWrapper = getPositionWrapper(origPositionSet);
        if (posWrapper != null) {
            posWrapper.persistChecks();
        }
    }

    protected void resetInternalFields() {
        rowColPosition = null;
        positionWrapper = null;
        nullPositionSet = false;
        parent = null;
    }

    /**
     * @return the position of this object
     */
    public RowColPos getPosition() {
        if (!nullPositionSet && (rowColPosition == null)) {
            AbstractPositionWrapper<T> pos = getPositionWrapper();
            if (pos != null) {
                rowColPosition = new RowColPos(pos.getRow(), pos.getCol());
            }
        }
        return rowColPosition;
    }

    /**
     * Set the position of this object
     */
    public void setPosition(RowColPos position) {
        this.rowColPosition = position;
        if (position == null) {
            positionWrapper = null;
            nullPositionSet = true;
        }
    }

    /**
     * @return the parent container of this object
     */
    public ContainerWrapper getParentContainer() {
        if (parent == null) {
            if (getPositionWrapper() != null)
                parent = getPositionWrapper().getParent();
        }
        return parent;
    }

    public ContainerWrapper getTop() {
        ContainerWrapper container;
        if (objectAtPosition instanceof ContainerWrapper)
            container = (ContainerWrapper) objectAtPosition;
        else
            container = getParentContainer();

        ContainerWrapper top = container.getTopContainer();
        if (top != null) {
            return top;
        }

        while (container != null && container.getParentContainer() != null) {
            container = container.getParentContainer();
        }
        return container;
    }

    /**
     * Set the parent of this object
     */
    public void setParentContainer(ContainerWrapper container) {
        this.parent = container;
        // AbstractContainerWrapper<?, ?> oldValue = getParent();
        AbstractPositionWrapper<T> pos = getPositionWrapper(true);
        if (pos != null) {
            pos.setParent(container);
        }
        // propertyChangeSupport.firePropertyChange("parent", oldValue,
        // container);
    }

    public boolean hasParentContainer() {
        return getParentContainer() != null;
    }

    protected AbstractPositionWrapper<T> getPositionWrapper() {
        return getPositionWrapper(false);
    }

    protected AbstractPositionWrapper<T> getPositionWrapper(
        boolean initIfNoPosition) {
        if (positionWrapper == null) {
            positionWrapper = getSpecificPositionWrapper(initIfNoPosition);
        }
        return positionWrapper;
    }

    protected abstract AbstractPositionWrapper<T> getSpecificPositionWrapper(
        boolean initIfNoPosition);

}
