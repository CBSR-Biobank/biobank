package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ContainerPositionInBoundsCheck<E extends AbstractPosition> extends
    WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String HQL = "SELECT o.{0}, o.{1} FROM {2} o WHERE o = ?"; //$NON-NLS-1$
    private static final String OUT_OF_BOUNDS_POSITION_MSG = Messages.getString("ContainerPositionInBoundsCheck.out.of.bounds.position.msg"); //$NON-NLS-1$
    private static final Property<Capacity, Container> CONTAINER_CAPACITY = ContainerPeer.CONTAINER_TYPE
        .to(ContainerTypePeer.CAPACITY);
    private static final Property<Integer, Container> MAX_ROW = CONTAINER_CAPACITY
        .to(CapacityPeer.ROW_CAPACITY);
    private static final Property<Integer, Container> MAX_COL = CONTAINER_CAPACITY
        .to(CapacityPeer.COL_CAPACITY);

    protected final Property<Integer, ? super E> maxRowProperty;
    protected final Property<Integer, ? super E> maxColProperty;

    public ContainerPositionInBoundsCheck(ModelWrapper<E> wrapper,
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
            && (row < 0 || row >= maxRow || col < 0 || col >= maxCol)) {
            String msg = MessageFormat.format(OUT_OF_BOUNDS_POSITION_MSG, row,
                col, maxRow, maxCol);
            throw new BiobankSessionException(msg);
        }

        return null;
    }
}