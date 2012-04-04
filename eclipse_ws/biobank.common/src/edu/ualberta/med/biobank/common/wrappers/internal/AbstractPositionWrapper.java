package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.base.AbstractPositionBaseWrapper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractPositionWrapper<E extends AbstractPosition>
    extends AbstractPositionBaseWrapper<E> {
    public AbstractPositionWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    public AbstractPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    private RowColPos position = null;

    @Override
    @Deprecated
    public void setRow(Integer row) {
        throw new UnsupportedOperationException(
            "Use setPosition() instead of setRow()."); //$NON-NLS-1$
    }

    @Override
    @Deprecated
    public void setCol(Integer col) {
        throw new UnsupportedOperationException(
            "Use setPosition() instead of setCol()."); //$NON-NLS-1$
    }

    public RowColPos getPosition() {
        if (position == null && getRow() != null && getCol() != null) {
            position = new RowColPos(getRow(), getCol());
        }
        return position;
    }

    protected void setPosition(RowColPos newPosition) {
        if (newPosition == null) {
            throw new IllegalArgumentException(
                "Position cannot be set to null."); //$NON-NLS-1$
        }

        super.setRow(newPosition.getRow());
        super.setCol(newPosition.getCol());

        position = newPosition;
    }

    public abstract ContainerWrapper getParent();

    public void setParent(ContainerWrapper parent, RowColPos position) {
        if (parent == null) {
            throw new IllegalArgumentException(
                "Parent container cannot be set null."); //$NON-NLS-1$
        }
        setParent(parent);
        setPosition(position);
    }

    protected abstract void setParent(ContainerWrapper parent);

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        return Collections
            .unmodifiableList(new ArrayList<Property<?, ? super E>>(
                AbstractPositionPeer.PROPERTIES));
    }
}
