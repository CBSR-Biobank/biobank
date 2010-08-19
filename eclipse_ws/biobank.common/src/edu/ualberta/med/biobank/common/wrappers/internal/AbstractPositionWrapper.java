package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AbstractContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractPositionWrapper<E extends AbstractPosition>
    extends ModelWrapper<E> {

    public AbstractPositionWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    public AbstractPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "row", "col" };
    }

    public void setRow(Integer row) {
        Integer oldRow = wrappedObject.getRow();
        wrappedObject.setRow(row);
        propertyChangeSupport.firePropertyChange("row", oldRow, row);
    }

    public Integer getRow() {
        return wrappedObject.getRow();
    }

    public void setCol(Integer col) {
        Integer oldCol = wrappedObject.getCol();
        wrappedObject.setCol(col);
        propertyChangeSupport.firePropertyChange("col", oldCol, col);
    }

    public Integer getCol() {
        return wrappedObject.getCol();
    }

    public abstract AbstractContainerWrapper<?, ?> getParent();

    public abstract void setParent(AbstractContainerWrapper<?, ?> parent);

    @Override
    public void persistChecks() throws BiobankCheckException,
        ApplicationException {
        AbstractContainerWrapper<?, ?> parent = getParent();
        if (parent != null) {
            checkPositionValid(parent);
            checkObjectAtPosition();
        } else if (getRow() != null || getCol() != null) {
            throw new BiobankCheckException(
                "Position should not be set when no parent set");
        }
    }

    public void checkPositionValid(AbstractContainerWrapper<?, ?> parent)
        throws BiobankCheckException {
        if (getRow() == null) {
            throw new BiobankCheckException("Position row can't be null");
        }
        if (getCol() == null) {
            throw new BiobankCheckException("Position col can't be null");
        }
        if (getRow() < 0 || getCol() < 0) {
            throw new BiobankCheckException("Position " + getRow() + ":"
                + getCol() + " is invalid. Need positive numbers.");
        }
        int rowCapacity = parent.getRowCapacity();
        int colCapacity = parent.getColCapacity();
        if (getRow() >= rowCapacity || getCol() >= colCapacity) {
            throw new BiobankCheckException("Position " + getRow() + ":"
                + getCol() + " is invalid. Row should be between 0 and "
                + rowCapacity + " (excluded) and Col should be between 0 and "
                + colCapacity + "(excluded)");
        }
    }

    protected abstract void checkObjectAtPosition()
        throws ApplicationException, BiobankCheckException;
}
