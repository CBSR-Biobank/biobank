package edu.ualberta.med.biobank.common.action.container;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerCreateChildrenAction implements Action<EmptyResult> {

    /**
     * Initialise children at given position with the given type. If the
     * positions list is null, initialise all the children. <strong>If a
     * position is already filled then it is skipped and no changes are made to
     * it</strong>.
     * 
     * @return true if at least one children has been initialised
     * @throws BiobankCheckException
     * @throws WrapperException
     * @throws ApplicationException
     */
    public void initChildrenWithType(ContainerType type,
        Set<RowColPos> positions) throws Exception {
        if (positions == null) {
            Capacity capacity =
                containerInfo.container.getContainerType().getCapacity();
            for (int i = 0, n = capacity.getRowCapacity().intValue(); i < n; i++) {
                for (int j = 0, m = capacity.getColCapacity().intValue(); j < m; j++) {
                    initPositionIfEmpty(type, i, j);
                }
            }
        } else {
            for (RowColPos rcp : positions) {
                initPositionIfEmpty(type, rcp.getRow(), rcp.getCol());
            }
        }
    }

    private void initPositionIfEmpty(ContainerType type, int i, int j)
        throws Exception {
        if (type == null) {
            throw new Exception(
                "Error initializing container. That is not a valid container type."); //$NON-NLS-1$
        }
        Boolean filled = (containerInfo.getChildContainer(i, j) != null);
        if (!filled) {
            ContainerSaveAction containerSaveAction = new ContainerSaveAction();
            containerSaveAction.setTypeId(type.getId());
            containerSaveAction.setSiteId(type.getSite().getId());
            containerSaveAction.setParentId(containerInfo.container.getId());
            containerSaveAction.setPosition(new RowColPos(i, j));
            containerSaveAction.setActivityStatusId(
                ActivityStatusEnum.ACTIVE.getId());
            SessionManager.getAppService().doAction(containerSaveAction);
        }
    }

    public void setParentContainer(Container container) {
        // TODO Auto-generated method stub

    }

    public void setContainerTypeId(Integer id) {
        // TODO Auto-generated method stub

    }

    public void setParentPositions(Set<RowColPos> positions) {
        // TODO Auto-generated method stub

    }

}
