package edu.ualberta.med.biobank.common.action.info;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final ContainerType containerType;
    private final Long containerCount;

    public ContainerTypeInfo(ContainerType containerType,
        Long containerCount) {
        this.containerType = containerType;
        this.containerCount = containerCount;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public Long getContainerCount() {
        return containerCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((containerType == null) ? 0 : containerType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContainerTypeInfo other = (ContainerTypeInfo) obj;
        if (containerType == null) {
            if (other.containerType != null) return false;
        } else if (!containerType.equals(other.containerType)) return false;
        return true;
    }
}
