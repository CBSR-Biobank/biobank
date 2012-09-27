package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.util.RowColPos;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractPositionHolder<E, T extends AbstractPosition>
    extends ModelWrapper<E> {

    private RowColPos rowColPosition;
    private AbstractPositionWrapper<T> positionWrapper;

    private ContainerWrapper parent;

    public AbstractPositionHolder(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    public AbstractPositionHolder(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public void persist() throws Exception {
        boolean positionSet = (rowColPosition != null);
        AbstractPositionWrapper<T> posWrapper = getPositionWrapper(positionSet);
        if (posWrapper != null && positionSet) {
            posWrapper.setRow(rowColPosition.row);
            posWrapper.setCol(rowColPosition.col);
        }
        super.persist();
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        boolean positionSet = rowColPosition != null;
        AbstractPositionWrapper<T> posWrapper = getPositionWrapper(positionSet);
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

    @Override
    protected void resetInternalField() {
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
        if (parent == null) {
            if (getPositionWrapper() != null)
                parent = getPositionWrapper().getParent();
        }
        return parent;
    }

    public void setParent(ContainerWrapper container) {
        this.parent = container;
        ContainerWrapper oldValue = getParent();
        AbstractPositionWrapper<T> pos = getPositionWrapper(true);
        pos.setParent(container);
        propertyChangeSupport.firePropertyChange("parent", oldValue, container);
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

    @Override
    public void reload() throws Exception {
        parent = null;
        super.reload();
    }

}