package edu.ualberta.med.biobank.common.wrappers.actions;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class UpdateContainerPathAction extends WrapperAction<Container> {
    private static final long serialVersionUID = 1L;

    private static final String PATH_DELIMITER = ContainerWrapper.PATH_DELIMITER;
    private static final Property<Container, Container> PARENT_PROPERTY = ContainerPeer.POSITION
        .to(ContainerPositionPeer.PARENT_CONTAINER);
    private static final Property<String, Container> PARENT_PATH_PROPERTY = PARENT_PROPERTY
        .to(ContainerPeer.PATH);
    private static final Property<Integer, Container> PARENT_ID_PROPERTY = PARENT_PROPERTY
        .to(ContainerPeer.ID);
    // @formatter:off
    private static final String SELECT_PARENT_INFO_HQL = 
        "SELECT o." + PARENT_PATH_PROPERTY.getName() + 
        "      ,o." + PARENT_ID_PROPERTY.getName() +
        " FROM " + Container.class.getName() + " o" +
        " WHERE o = ?";
    // @formatter:on

    public UpdateContainerPathAction(ContainerWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        String path = getPath(session);
        Container container = getModel();

        ContainerPeer.PATH.set(container, path);
        session.saveOrUpdate(container);

        return null;
    }

    private String getPath(Session session) {
        Query query = session.createQuery(SELECT_PARENT_INFO_HQL);
        query.setParameter(0, getModel());

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();

        StringBuilder path = new StringBuilder();

        for (Object[] result : results) {
            String parentPath = (String) result[0];
            Integer parentId = (Integer) result[1];

            if (parentPath != null && !parentPath.isEmpty()) {
                path.append(parentPath);
                path.append(PATH_DELIMITER);
            }

            if (parentId != null) {
                path.append(parentId);
            }

            break;
        }

        return path.toString();
    }
}
