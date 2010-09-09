package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class AbstractObjectWithPositionManagement<T extends AbstractPosition> {
    protected RowColPos rowColPosition;
    private AbstractPositionWrapper<T> positionWrapper;

    // used to allow position to be assigned to null
    protected boolean nullPositionSet = false;

    private ContainerWrapper parent;

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
        checkParentFromSameSite();
    }

    private void checkParentFromSameSite() throws BiobankCheckException {
        if (getParent() != null && !getParent().getSite().equals(getSite())) {
            throw new BiobankCheckException(
                "Parent should be part of the same site");
        }
    }

    public abstract SiteWrapper getSite();

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
        ContainerWrapper top = getParent();

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
        pos.setParent(container);
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
