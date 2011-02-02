package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
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
    protected List<String> getPropertyChangeNames() {
        return AbstractPositionPeer.PROP_NAMES;
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

    public abstract ContainerWrapper getParent();

    public abstract void setParent(ContainerWrapper parent);

    @Override
    public void persistChecks() throws BiobankCheckException,
        ApplicationException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            checkPositionValid(parent);
            checkObjectAtPosition();
        } else if (getRow() != null || getCol() != null) {
            throw new BiobankCheckException(
                "Position should not be set when no parent set");
        }
    }

    public void checkPositionValid(ContainerWrapper parent)
        throws BiobankCheckException {
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
