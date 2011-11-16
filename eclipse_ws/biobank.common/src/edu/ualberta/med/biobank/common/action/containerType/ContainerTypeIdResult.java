package edu.ualberta.med.biobank.common.action.containerType;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class ContainerTypeIdResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final Integer containerTypeId;

    public ContainerTypeIdResult(Integer containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public Integer getContainerTypeId() {
        return containerTypeId;
    }
}
