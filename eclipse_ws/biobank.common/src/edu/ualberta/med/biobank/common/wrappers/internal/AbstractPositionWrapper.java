package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.TaskList;
import edu.ualberta.med.biobank.model.AbstractPosition;
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

    public RowColPos getPosition() {
        return new RowColPos(getRow(), getCol());
    }

    public void setPosition(RowColPos rcp) {
        setRow(rcp.row);
        setCol(rcp.col);
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
}
