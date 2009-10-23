package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractPositionHolder<E, T extends AbstractPosition>
    extends ModelWrapper<E> {

    private RowColPos rowColPosition;
    private AbstractPositionWrapper<T> positionWrapper;

    public AbstractPositionHolder(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    public AbstractPositionHolder(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        boolean positionSet = rowColPosition != null;
        AbstractPositionWrapper<T> posWrapper = getPositionWrapper(positionSet);
        if (posWrapper != null) {
            if (positionSet) {
                posWrapper.setRow(rowColPosition.row);
                posWrapper.setCol(rowColPosition.col);
            }
            posWrapper.persistChecks();
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        rowColPosition = null;
        positionWrapper = null;
    }

    public RowColPos getPosition() {
        if (rowColPosition == null) {
            AbstractPositionWrapper<T> pos = getPositionWrapper();
            if (pos != null) {
                rowColPosition = new RowColPos(pos.getRow(), pos.getCol());
            }
        }
        return rowColPosition;
    }

    public void setPosition(RowColPos position) {
        RowColPos oldPosition = getPosition();
        this.rowColPosition = position;
        propertyChangeSupport.firePropertyChange("position", oldPosition,
            position);
    }

    public void setPosition(Integer row, Integer col) {
        setPosition(new RowColPos(row, col));
    }

    public ContainerWrapper getParent() {
        AbstractPositionWrapper<T> pos = getPositionWrapper();
        if (pos == null) {
            return null;
        }
        return pos.getParent();
    }

    public void setParent(ContainerWrapper parent) {
        ContainerWrapper oldValue = getParent();
        AbstractPositionWrapper<T> pos = getPositionWrapper(true);
        pos.setParent(parent);
        propertyChangeSupport.firePropertyChange("parent", oldValue, parent);
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

    // protected abstract T getPositionObject();
    //
    // protected abstract AbstractPositionWrapper<T> initPositionWrapper(T
    // position);
    //
    // protected abstract AbstractPositionWrapper<T> initPositionWrapper(
    // AbstractPositionHolder<E, T> parent);
}
