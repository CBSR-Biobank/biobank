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
        // RowColPos oldPosition = getPosition();
        this.rowColPosition = position;
        // propertyChangeSupport.firePropertyChange("position", oldPosition,
        // position);
        if (position == null) {
            positionWrapper = null;
            nullPositionSet = true;
        }
    }

    /**
     * @return the parent of this object
     */
    public ContainerWrapper getParent() {
        if (parent == null) {
            if (getPositionWrapper() != null)
                parent = getPositionWrapper().getParent();
        }
        return parent;
    }

    public ContainerWrapper getTop() {
        ContainerWrapper top;
        if (objectAtPosition instanceof ContainerWrapper)
            top = (ContainerWrapper) objectAtPosition;
        else
            top = getParent();

        ContainerPathWrapper path = top.getContainerPath();
        if (path != null) {
            return path.getTopContainer();
        }

        while (top != null && top.getParent() != null) {
            top = top.getParent();
        }
        return top;
    }

    /**
     * Set the parent of this object
     */
    public void setParent(ContainerWrapper container) {
        this.parent = container;
        // AbstractContainerWrapper<?, ?> oldValue = getParent();
        AbstractPositionWrapper<T> pos = getPositionWrapper(true);
        if (pos != null) {
            pos.setParent(container);
        }
        // propertyChangeSupport.firePropertyChange("parent", oldValue,
        // container);
    }

    public boolean hasParent() {
        return getParent() != null;
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
