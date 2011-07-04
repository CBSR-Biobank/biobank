package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class UpdateContainerChildrenAction extends WrapperAction<Container> {
    private static final long serialVersionUID = 1L;
    private static final Property<String, Container> POSITION_STRING = ContainerPeer.POSITION
        .to(ContainerPositionPeer.POSITION_STRING);
    private static final Property<Container, Container> PARENT_CONTAINER = ContainerPeer.POSITION
        .to(ContainerPositionPeer.PARENT_CONTAINER);
    private static final Property<Integer, Container> PARENT_CONTAINER_ID = PARENT_CONTAINER
        .to(ContainerPeer.ID);
    private static final Property<String, Container> PARENT_CONTAINER_PATH = PARENT_CONTAINER
        .to(ContainerPeer.PATH);
    private static final Property<String, Container> PARENT_CONTAINER_LABEL = PARENT_CONTAINER
        .to(ContainerPeer.LABEL);
    // @formatter:off
    private static final String UPDATE_PATH_HQL = 
        "CONCAT(IF(LENGTH(" + PARENT_CONTAINER_PATH.getName() + ") > 0, " + PARENT_CONTAINER_PATH.getName() + ", ''), " + PARENT_CONTAINER_ID.getName() + ")";
    private static final String UPDATE_CHILDREN_HQL =
        "\\nUPDATE " + Container.class.getName() + " o" +
        "\\n SET o." + ContainerPeer.TOP_CONTAINER.getName() + " = ? " +
        "\\n    ,o." + ContainerPeer.LABEL.getName() + " = CONCAT(o." + PARENT_CONTAINER_LABEL.getName() + ", o." + POSITION_STRING.getName() + ")" +
        "\\n    ,o." + ContainerPeer.PATH.getName() + " = " + UPDATE_PATH_HQL +
        "\\n WHERE o." + PARENT_CONTAINER_ID.getName() + " IN ({0})";
    private static final String SELECT_CHILDREN_HQL = 
        "\\nSELECT o." + ContainerPeer.ID.getName() +
        "\\n FROM " + Container.class.getName() + " o" +
        "\\n WHERE o." + PARENT_CONTAINER_ID.getName() + " IN ({0})";
    // @formatter:on

    private final Container topContainer;

    public UpdateContainerChildrenAction(ContainerWrapper wrapper) {
        super(wrapper);
        this.topContainer = wrapper.getTopContainer().getWrappedObject();
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        List<Integer> ids = Arrays.asList(getModel().getId());

        updateChildren(session, ids);

        return null;
    }

    private void updateChildren(Session session, List<Integer> ids)
        throws BiobankSessionException {
        if (ids.isEmpty()) {
            return;
        }

        String paramString = getParamString(ids.size());
        String hql = MessageFormat.format(UPDATE_CHILDREN_HQL, paramString);
        Query query = session.createQuery(hql);
        query.setParameter(0, topContainer);

        int position = 1;
        for (Integer id : ids) {
            query.setParameter(position++, id);
        }

        query.executeUpdate();

        List<Integer> childIds = selectChildren(session, ids);

        updateChildren(session, childIds);
    }

    private List<Integer> selectChildren(Session session, List<Integer> ids) {
        String paramString = getParamString(ids.size());
        String hql = MessageFormat.format(SELECT_CHILDREN_HQL, paramString);
        Query query = session.createQuery(hql);

        int position = 0;
        for (Integer id : ids) {
            query.setParameter(position++, id);
        }

        @SuppressWarnings("unchecked")
        List<Integer> childIds = query.list();

        return childIds;
    }

    private static String getParamString(int numParams) {
        StringBuilder buffer = new StringBuilder(numParams * 2);

        for (int i = 0; i < numParams; i++) {
            buffer.append("?");
            if (i < numParams - 1) {
                buffer.append(", ");
            }
        }

        return buffer.toString();
    }
}