package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractPositionWrapper<E extends AbstractPosition>
    extends ModelWrapper<E> {

    public AbstractPositionWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(E oldWrappedObject, E newWrappedObject) {
        propertyChangeSupport.firePropertyChange("row", oldWrappedObject,
            newWrappedObject);
        propertyChangeSupport.firePropertyChange("col", oldWrappedObject,
            newWrappedObject);
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

}
