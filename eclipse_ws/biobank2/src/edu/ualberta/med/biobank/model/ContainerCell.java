package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ContainerPositionWrapper;

public class ContainerCell {

    private ContainerPositionWrapper position;

    private ContainerStatus status;

    public ContainerCell() {
    }

    public ContainerCell(ContainerPositionWrapper position) {
        this.position = position;
    }

    public ContainerPositionWrapper getPosition() {
        return position;
    }

    public void setPosition(ContainerPositionWrapper position) {
        this.position = position;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public void setStatus(ContainerStatus status) {
        this.status = status;
    }

}
