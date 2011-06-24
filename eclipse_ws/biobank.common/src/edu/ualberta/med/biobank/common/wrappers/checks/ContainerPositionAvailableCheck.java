package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage.LazyArg;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ContainerPositionAvailableCheck<E extends AbstractPosition>
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

    public ContainerPositionAvailableCheck(ModelWrapper<E> wrapper,
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
            String containerString = getContainerString(session, containerId);
            String msg = MessageFormat.format(UNAVAILABLE_POSITION_MSG, row,
                col, containerString);
            throw new BiobankSessionException(msg);
        }

        return null;
    }

    private static String getContainerString(Session session,
        Integer containerId) {
        LazyArg label = LazyMessage.newArg(Container.class, ContainerPeer.ID,
            containerId, ContainerPeer.LABEL);
        LazyArg barcode = LazyMessage.newArg(Container.class, ContainerPeer.ID,
            containerId, ContainerPeer.PRODUCT_BARCODE);

        LazyMessage containerLazyMessage = new LazyMessage("{0} ({1})", label,
            barcode);

        String containerString = containerLazyMessage.format(session);
        return containerString;
    }
}