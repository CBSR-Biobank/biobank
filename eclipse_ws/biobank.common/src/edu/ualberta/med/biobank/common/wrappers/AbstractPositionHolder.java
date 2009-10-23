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

    protected AbstractPositionWrapper<T> getPositionWrapper() {
        return getPositionWrapper(false);
    }

    protected AbstractPositionWrapper<T> getPositionWrapper(
        boolean initIfNoPosition) {
        if (positionWrapper == null) {
            T pos = getPositionObject();
            if (pos != null) {
                positionWrapper = initPositionWrapper(pos);
            } else if (initIfNoPosition) {
                positionWrapper = initPositionWrapper(this);
            }
        }
        return positionWrapper;
    }

    public abstract T getPositionObject();

    public abstract AbstractPositionWrapper<T> initPositionWrapper(T position);

    public abstract AbstractPositionWrapper<T> initPositionWrapper(
        AbstractPositionHolder<E, T> parent);
}
