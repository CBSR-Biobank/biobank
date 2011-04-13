package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
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

    public Integer getRow() {
        return getProperty(AbstractPositionPeer.ROW);
    }

    public void setRow(Integer row) {
        setProperty(AbstractPositionPeer.ROW, row);
    }

    public Integer getCol() {
        return getProperty(AbstractPositionPeer.COL);
    }

    public void setCol(Integer col) {
        setProperty(AbstractPositionPeer.COL, col);
    }

    public abstract ContainerWrapper getParent();

    public abstract void setParent(ContainerWrapper parent);

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        return Collections
            .unmodifiableList(new ArrayList<Property<?, ? super E>>(
                AbstractPositionPeer.PROPERTIES));
    }

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
