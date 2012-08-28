package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;

@Embeddable
public class ContainerPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Integer position;
    private Integer left;
    private Integer right;

    @NotNull(message = "{ContainerPosition.parentContainer.NotNull}")
    @ManyToOne
    @ForeignKey(name = "FK_ContainerPosition_parentContainer")
    @JoinColumn(name = "PARENT_CONTAINER_ID")
    public Container getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    /**
     * Read-only property (the corresponding setter does nothing) to get data
     * for a foreign key constraint to the parent container, ensuring that as
     * long as this {@link ContainerPosition} exists, the parent
     * {@link Container} has the same {@link ContainerType}.
     * 
     * @return
     */
    @ManyToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false)
    ContainerType getParentContainerType() {
        return getParentContainer() != null
            ? getParentContainer().getContainerType()
            : null;
    }

    @SuppressWarnings("unused")
    void setParentContainerType(ContainerType parentContainerType) {
    }

    @NotNull(message = "{ContainerPosition.container.NotNull}")
    @OneToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "CONTAINER_ID")
    public Container getContainer() {
        return container;
    }

    void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Read-only property (the corresponding setter does nothing) to get data
     * for a foreign key constraint to the container, ensuring that as long as
     * this {@link ContainerPosition} exists, parent {@link Container} has the
     * same {@link ContainerType}.
     * 
     * @return
     */
    @ManyToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false)
    ContainerType getContainerType() {
        return getContainer() != null
            ? getContainer().getContainerType()
            : null;
    }

    @SuppressWarnings("unused")
    void setContainerType(ContainerType containerType) {
    }

    @Override
    @Transient
    public Container getHoldingContainer() {
        return getParentContainer();
    }
}
