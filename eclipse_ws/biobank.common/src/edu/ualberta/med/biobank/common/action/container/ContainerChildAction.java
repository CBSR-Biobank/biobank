package edu.ualberta.med.biobank.common.action.container;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.util.RowColPos;

public abstract class ContainerChildAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    protected Integer parentContainerId;
    protected Integer childContainerTypeId;
    protected Set<RowColPos> positions;

    public void setParentContainerId(Integer parentContainerId) {
        this.parentContainerId = parentContainerId;
    }

    public void setContainerTypeId(Integer childContainerTypeId) {
        this.childContainerTypeId = childContainerTypeId;
    }

    public void setParentPositions(Set<RowColPos> positions) {
        this.positions = positions;
    }
}
