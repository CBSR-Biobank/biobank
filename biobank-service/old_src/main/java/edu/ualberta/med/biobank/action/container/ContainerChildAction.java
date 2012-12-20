package edu.ualberta.med.biobank.action.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.model.util.RowColPos;

public abstract class ContainerChildAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    protected Integer parentContainerId;
    protected Integer childContainerTypeId;
    protected List<RowColPos> positions;

    public void setParentContainerId(Integer parentContainerId) {
        this.parentContainerId = parentContainerId;
    }

    public void setContainerTypeId(Integer childContainerTypeId) {
        this.childContainerTypeId = childContainerTypeId;
    }

    public void setParentPositions(Set<RowColPos> positions) {
        this.positions = new ArrayList<RowColPos>(positions);
    }
}
