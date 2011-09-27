package edu.ualberta.med.biobank.common.wrappers.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.util.ProxyUtil;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class UpdateContainerChildrenAction extends WrapperAction<Container> {
    private static final long serialVersionUID = 1L;
    private static final String PATH_DELIMITER = ContainerWrapper.PATH_DELIMITER;
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
    @SuppressWarnings("nls")
    private static final String UPDATE_HQL =
        "\nUPDATE " + Container.class.getName() + " o" +
        "\n SET o." + ContainerPeer.TOP_CONTAINER.getName() + " = ? " +
        "\n    ,o." + ContainerPeer.LABEL.getName() + " = ?" +
        "\n    ,o." + ContainerPeer.PATH.getName() + " = ?" +
        "\n WHERE o." + ContainerPeer.ID.getName() + " = ?";
    @SuppressWarnings("nls")
    private static final String SELECT_CHILDREN_HQL = 
        "\nSELECT o." + ContainerPeer.ID.getName() +
        "\n      ,o." + PARENT_CONTAINER_LABEL.getName() +
        "\n      ,o." + POSITION_STRING.getName() +
        "\n      ,o." + PARENT_CONTAINER_PATH.getName() +
        "\n FROM " + Container.class.getName() + " o" +
        "\n WHERE o." + PARENT_CONTAINER_ID.getName() + " = ?";
    // @formatter:on

    private final Container topContainer;

    public UpdateContainerChildrenAction(ContainerWrapper wrapper) {
        super(wrapper);
        this.topContainer = ProxyUtil.convertProxyToObject(wrapper
            .getTopContainer().getWrappedObject());
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        Integer id = getModel().getId();
        updateChildren(session, id);

        return null;
    }

    private void updateChildren(Session session, Integer parentId)
        throws BiobankSessionException {

        List<ContainerInfo> children = getChildren(session, parentId);

        for (ContainerInfo child : children) {
            Integer id = child.getId();

            Query query = session.createQuery(UPDATE_HQL);
            query.setParameter(0, topContainer);
            query.setParameter(1, child.getLabel());
            query.setParameter(2, child.getPath());
            query.setParameter(3, id);

            query.executeUpdate();

            updateChildren(session, id);
        }
    }

    private List<ContainerInfo> getChildren(Session session, Integer parentId) {
        Query query = session.createQuery(SELECT_CHILDREN_HQL);
        query.setParameter(0, parentId);

        List<ContainerInfo> children = new ArrayList<ContainerInfo>();

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();

        for (Object[] result : results) {
            Integer id = (Integer) result[0];
            String parentLabel = (String) result[1];
            String positionString = (String) result[2];
            String parentPath = (String) result[3];

            ContainerInfo containerInfo = new ContainerInfo(id, parentLabel,
                positionString, parentPath, parentId);
            children.add(containerInfo);
        }

        return children;
    }

    private static final class ContainerInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Integer id;
        private final String label, path;

        public ContainerInfo(Integer id, String parentLabel,
            String positionString, String parentPath, Integer parentId) {
            this.id = id;
            this.label = parentLabel + positionString;
            this.path = getPath(parentPath, parentId);
        }

        public Integer getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getPath() {
            return path;
        }

        private static String getPath(String parentPath, Integer parentId) {
            StringBuilder path = new StringBuilder();

            if (parentPath != null && !parentPath.isEmpty()) {
                path.append(parentPath);
                path.append(PATH_DELIMITER);
            }

            path.append(parentId);

            return path.toString();
        }
    }
}