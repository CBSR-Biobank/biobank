package edu.ualberta.med.biobank.model;

public class ContainerPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Container container;

    public Container getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}