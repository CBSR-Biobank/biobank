package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.TaskList;
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

    @Override
    public void setRow(Integer row) {
        Integer oldRow = getRow();
        super.setRow(row);
        if (row == null || !row.equals(oldRow)) {
            updatePositionString();
        }
    }

    @Override
    public void setCol(Integer col) {
        Integer oldCol = getCol();
        super.setCol(col);
        if (col == null || !col.equals(oldCol)) {
            updatePositionString();
        }
    }

    public RowColPos getPosition() {
        return new RowColPos(getRow(), getCol());
    }

    public void setPosition(RowColPos rcp) {
        setRow(rcp.getRow());
        setCol(rcp.getCol());
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
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().notNull(AbstractPositionPeer.ROW));
        tasks.add(check().notNull(AbstractPositionPeer.COL));

        tasks.add(super.getPersistTasks());

        return tasks;
    }

    private void updatePositionString() {
        ContainerWrapper container = getParent();
        if (container != null && getRow() != null && getCol() != null) {
            ContainerTypeWrapper containerType = container.getContainerType();
            if (containerType != null) {
                String positionString = containerType
                    .getPositionString(new RowColPos(getRow(), getCol()));
                setPositionString(positionString);
            }
        }
    }
}
