package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "CONTAINER_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PARENT_CONTAINER_ID", "ROW", "COL" }) })
// @SecondaryTables({
// @SecondaryTable(name = "CONTAINER",
// pkJoinColumns = {
// @PrimaryKeyJoinColumn(name = "CONTAINER_ID", referencedColumnName = "ID"),
// @PrimaryKeyJoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName =
// "CONTAINER_TYPE_ID")
// }),
// @SecondaryTable(name = "CONTAINER",
// pkJoinColumns = {
// @PrimaryKeyJoinColumn(name = "PARENT_CONTAINER_ID", referencedColumnName =
// "ID"),
// @PrimaryKeyJoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName
// = "CONTAINER_TYPE_ID")
// }),
// @SecondaryTable(name = "CONTAINER_TYPE_CONTAINER_TYPE",
// pkJoinColumns = {
// @PrimaryKeyJoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName
// = "PARENT_CONTAINER_TYPE_ID"),
// @PrimaryKeyJoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName =
// "CHILD_CONTAINER_TYPE_ID")
// })
// })
@Unique(properties = { "parentContainer", "row", "col" }, groups = PrePersist.class)
public class ContainerPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Container container;

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.parentContainer.NotNull}")
    @ManyToOne
    @ForeignKey(name = "none")
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
        return parentContainer != null ? parentContainer.getContainerType()
            : null;
    }

    void setParentContainerType(ContainerType parentContainerType) {
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.container.NotNull}")
    @OneToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "CONTAINER_ID")
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
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
        return container != null ? container.getContainerType() : null;
    }

    void setContainerType(ContainerType containerType) {
    }
}
