package edu.ualberta.med.biobank.common.action.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class ContainerGetInfoAction implements Action<ContainerInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CONTAINER_INFO_HQL =
        "SELECT DISTINCT container"
            + " FROM " + Container.class.getName() + " container"
            + " INNER JOIN FETCH container.activityStatus"
            + " INNER JOIN FETCH container.site"
            + " INNER JOIN FETCH container.capacity"
            + " LEFT JOIN FETCH container.childPositionCollection childPos"
            + " LEFT JOIN FETCH childPos.container"
            + " LEFT JOIN FETCH container.specimenTypeCollection"
            + " LEFT JOIN FETCH container.commentCollection comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE container.id = ?";

    public static class ContainerInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Container container;

        private Map<RowColPos, Specimen> specimens =
            new HashMap<RowColPos, Specimen>();
        private Map<RowColPos, Container> children =
            new HashMap<RowColPos, Container>();

        public Container getChildContainer(int row, int col) {
            return children.get(new RowColPos(row, col));
        }
    }

    private final Integer containerId;

    public ContainerGetInfoAction(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerReadPermission(containerId).isAllowed(context);
    }

    @Override
    public ContainerInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(CONTAINER_INFO_HQL);
        query.setParameter(0, containerId);

        @SuppressWarnings("unchecked")
        List<Container> containers = query.list();

        if (containers.size() != 1) {
            throw new ModelNotFoundException(Container.class, containerId);
        }

        ContainerInfo containerInfo = new ContainerInfo();
        containerInfo.container = containers.get(0);

        for (SpecimenPosition pos : containerInfo.container
            .getSpecimenPositionCollection()) {
            containerInfo.specimens.put(
                new RowColPos(pos.getRow(), pos.getCol()),
                pos.getSpecimen());
        }

        for (ContainerPosition pos : containerInfo.container
            .getChildPositionCollection()) {
            containerInfo.children.put(
                new RowColPos(pos.getRow(), pos.getCol()),
                pos.getContainer());
        }

        return containerInfo;
    }

}
