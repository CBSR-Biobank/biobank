package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "CONTAINER_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PARENT_CONTAINER_ID", "ROW", "COL" }) })
@Unique(properties = { "parentContainer", "row", "col" }, groups = PrePersist.class)
public class ContainerPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Container container;

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.parentContainer.NotNull}")
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

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.container.NotNull}")
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
