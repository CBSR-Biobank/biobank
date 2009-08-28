package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerPositionWrapper extends ModelWrapper<ContainerPosition> {

    public ContainerPositionWrapper(WritableApplicationService appService,
        ContainerPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(ContainerPosition oldWrappedObject,
        ContainerPosition newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<ContainerPosition> getWrappedClass() {
        return ContainerPosition.class;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setParentContainer(Container parentContainer) {
        Container oldParent = getParentContainer();
        wrappedObject.setParentContainer(parentContainer);
        propertyChangeSupport.firePropertyChange("parentContainer", oldParent,
            parentContainer);
    }

    private Container getParentContainer() {
        return wrappedObject.getParentContainer();
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
        ContainerWrapper parent = new ContainerWrapper(appService,
            getParentContainer());
        RowColPos rowColPos = parent.getPositionFromLabelingScheme(position);
        setRow(rowColPos.row);
        setCol(rowColPos.col);
    }

    private void setCol(Integer col) {
        Integer oldCol = getCol();
        wrappedObject.setCol(col);
        propertyChangeSupport.firePropertyChange("col", oldCol, col);
    }

    private Integer getCol() {
        return wrappedObject.getCol();
    }

    private Integer getRow() {
        return wrappedObject.getRow();
    }

    private void setRow(Integer row) {
        Integer oldRow = getRow();
        wrappedObject.setRow(row);
        propertyChangeSupport.firePropertyChange("row", oldRow, row);
    }

}
