package edu.ualberta.med.biobank.common.wrappers.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.TaskList;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage.LazyArg;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
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

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().notNull(AbstractPositionPeer.ROW));
        tasks.add(check().notNull(AbstractPositionPeer.COL));

        tasks.add(super.getPersistTasks());

        return tasks;
    }

    protected static class PostCheckContainerPositionAvailable<E extends AbstractPosition>
        extends WrapperAction<E> {
        private static final long serialVersionUID = 1L;
        // @formatter:off
        private static final String HQL = "SELECT pos.{1}." + ContainerPeer.ID.getName() +
            " FROM {0} pos" +
            " WHERE pos." + AbstractPositionPeer.ROW.getName() + " = ?" +
            " AND pos." + AbstractPositionPeer.COL.getName() + " = ?" +
            " AND pos.{1} = (SELECT this.{1} FROM {0} this WHERE this = ?)" +
            " AND pos <> ?";
        // @formatter:on
        private static final String UNAVAILABLE_POSITION_MSG = "Position {0}:{1} in container {2} is not available.";

        private final Property<Container, ? super E> containerProperty;

        protected PostCheckContainerPositionAvailable(ModelWrapper<E> wrapper,
            Property<Container, ? super E> containerProperty) {
            super(wrapper);
            this.containerProperty = containerProperty;
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            E model = getModel();
            Integer row = AbstractPositionPeer.ROW.get(model);
            Integer col = AbstractPositionPeer.COL.get(model);

            String hql = MessageFormat.format(HQL, getModelClass().getName(),
                containerProperty.getName());
            Query query = session.createQuery(hql);
            query.setParameter(0, row);
            query.setParameter(1, col);
            query.setParameter(2, model);
            query.setParameter(3, model);

            @SuppressWarnings("unchecked")
            List<Integer> containerIds = query.list();

            for (Integer containerId : containerIds) {
                String containerString = getContainerString(session,
                    containerId);
                String msg = MessageFormat.format(UNAVAILABLE_POSITION_MSG,
                    row, col, containerString);
                throw new BiobankSessionException(msg);
            }

            return null;
        }

        private static String getContainerString(Session session,
            Integer containerId) {
            LazyArg label = LazyMessage.newArg(Container.class,
                ContainerPeer.ID, containerId, ContainerPeer.LABEL);
            LazyArg barcode = LazyMessage.newArg(Container.class,
                ContainerPeer.ID, containerId, ContainerPeer.PRODUCT_BARCODE);

            LazyMessage containerLazyMessage = new LazyMessage("{0} ({1})",
                label, barcode);

            String containerString = containerLazyMessage.format(session);
            return containerString;
        }
    }

    protected static class PostCheckContainerPositionInBounds<E extends AbstractPosition>
        extends WrapperAction<E> {
        private static final long serialVersionUID = 1L;
        private static final String HQL = "SELECT o.{0}, o.{1} FROM {2} o WHERE o = ?";
        private static final String OUT_OF_BOUNDS_POSITION_MSG = "Position {0}:{1} is invalid. Row should be between 0 and {2} (exclusive) and Col should be between 0 and {3} (exclusive).";
        private static final Property<Capacity, Container> CONTAINER_CAPACITY = ContainerPeer.CONTAINER_TYPE
            .to(ContainerTypePeer.CAPACITY);
        private static final Property<Integer, Container> MAX_ROW = CONTAINER_CAPACITY
            .to(CapacityPeer.ROW_CAPACITY);
        private static final Property<Integer, Container> MAX_COL = CONTAINER_CAPACITY
            .to(CapacityPeer.COL_CAPACITY);

        protected final Property<Integer, ? super E> maxRowProperty;
        protected final Property<Integer, ? super E> maxColProperty;

        protected PostCheckContainerPositionInBounds(ModelWrapper<E> wrapper,
            Property<Container, ? super E> containerProperty) {
            super(wrapper);
            this.maxRowProperty = containerProperty.to(MAX_ROW);
            this.maxColProperty = containerProperty.to(MAX_COL);
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            String hql = MessageFormat.format(HQL, maxRowProperty.getName(),
                maxColProperty.getName(), getModelClass().getName());

            Query query = session.createQuery(hql);
            query.setParameter(0, getModel());

            List<?> list = query.list();

            Integer maxRow = null, maxCol = null;
            for (Object result : list) {
                if (result instanceof Object[]) {
                    Object[] array = (Object[]) result;
                    maxRow = (Integer) array[0];
                    maxCol = (Integer) array[1];
                }
                break;
            }

            E model = getModel();
            Integer row = AbstractPositionPeer.ROW.get(model);
            Integer col = AbstractPositionPeer.COL.get(model);

            if (maxRow != null && maxCol != null
                && (row >= maxRow || col >= maxCol)) {
                String msg = MessageFormat.format(OUT_OF_BOUNDS_POSITION_MSG,
                    row, col, maxRow, maxCol);
                throw new BiobankSessionException(msg);
            }

            return null;
        }
    }
}
