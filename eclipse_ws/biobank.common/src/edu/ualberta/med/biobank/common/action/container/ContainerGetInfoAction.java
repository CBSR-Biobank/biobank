package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Container;

public class ContainerGetInfoAction implements Action<ContainerTypeInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CONTAINER_INFO_HQL =
        "SELECT DISTINCT container"
            + " FROM " + Container.class.getName() + " container"
            + " INNER JOIN FETCH container.activityStatus"
            + " INNER JOIN FETCH container.capacity"
            + " LEFT JOIN FETCH container.childPositionCollection"
            + " LEFT JOIN FETCH container.specimenTypeCollection"
            + " LEFT JOIN FETCH container.commentCollection comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE container.id = ?";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ContainerTypeInfo run(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }

}
