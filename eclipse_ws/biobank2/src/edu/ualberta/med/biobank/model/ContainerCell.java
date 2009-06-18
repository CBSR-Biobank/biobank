package edu.ualberta.med.biobank.model;

public class ContainerCell {

	private ContainerPosition position;

	private ContainerStatus status;

	public ContainerCell() {
	}

	public ContainerCell(ContainerPosition position) {
		this.position = position;
	}

	public ContainerPosition getPosition() {
		return position;
	}

	public void setPosition(ContainerPosition position) {
		this.position = position;
	}

	public ContainerStatus getStatus() {
		return status;
	}

	public void setStatus(ContainerStatus status) {
		this.status = status;
	}

}
