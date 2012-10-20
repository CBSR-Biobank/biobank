package edu.ualberta.med.biobank.model.center;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainer;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A specifically built physical unit that can hold child containers, or can be
 * contained in a parent container.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CONTAINER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "INVENTORY_ID" }),
        @UniqueConstraint(columnNames = { "CONTAINER_TREE_ID", "LABEL" }),
        @UniqueConstraint(columnNames = {
            "PARENT_CONTAINER_ID",
            "CONTAINER_SCHEMA_POSITION_ID"
        })
    })
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// TODO: consider pulling @UniqueConstraint into this @Unique annotation,
// because this is a total repeating of constraints. Would then need to figure
// out how to add DDL constraints from our annotations and how to get a bean's
// value of a specific column.
@Unique.List({
    @Unique(properties = { "inventoryId" }, groups = PrePersist.class),
    @Unique(properties = { "tree", "label" }, groups = PrePersist.class),
    @Unique(properties = { "parentContainer", "position" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = Container.class, property = "parent", groups = PreDelete.class),
    @NotUsed(by = Specimen.class, property = "container", groups = PreDelete.class)
})
@ValidContainer(groups = PrePersist.class)
public abstract class Container<T extends ContainerType>
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private String inventoryId;
    private T containerType;
    private StorageContainer parent;
    private ContainerSchemaPosition position;
    private ContainerTree tree;
    private String label;
    private Integer left;
    private Integer right;
    private Integer depth;

    /**
     * Required inventory identifier, such as a barcode. Global uniqueness is
     * required so that {@link Container}s, like {@link Specimen}s, can be
     * shipped between {@link Center}s.
     * 
     * @return a globally unique identifier, or null if not specified.
     */
    @NotNull(message = "{Container.inventoryId.NotNull}")
    @Column(name = "INVENTORY_ID", unique = true, nullable = false)
    public String getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    /**
     * @return the classification of this {@link Container}.
     */
    @NotNull(message = "{Container.containerType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ContainerType.class)
    @JoinColumn(name = "CONTAINER_TYPE_ID")
    public T getContainerType() {
        return containerType;
    }

    public void setContainerType(T containerType) {
        this.containerType = containerType;
    }

    /**
     * @return the {@link StorageContainer} that this {@link Container} is
     *         inside, or null if it isn't inside one.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CONTAINER_ID")
    public StorageContainer getParent() {
        return parent;
    }

    public void setParent(StorageContainer parent) {
        this.parent = parent;
    }

    /**
     * This value should always be null if {@link #getParent()} is null.
     * 
     * @return the position this {@link Container} has in its
     *         {@link #getParent()}, or null if there is no specific position.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_SCHEMA_POSITION_ID")
    public ContainerSchemaPosition getPosition() {
        return position;
    }

    public void setPosition(ContainerSchemaPosition position) {
        this.position = position;
    }

    @NotNull(message = "{ContainerNode.tree.NotNull}")
    @ManyToOne
    @JoinColumn(name = "CONTAINER_TREE_ID", nullable = false)
    public ContainerTree getTree() {
        return tree;
    }

    public void setTree(ContainerTree tree) {
        this.tree = tree;
    }

    /**
     * The label must be delimited to avoid confusion, but perhaps users may
     * enter non-delimited versions (but that is 2^(n-1) different possible
     * delimited labels to search).
     * <p>
     * Exists as a "materialized" path, essentially, this value will never be
     * entered, only manually calculated and looked up or searched for.
     * 
     * @return if this has a {@link #getParent()}, then return this
     *         {@link Container}'s position (i.e.
     *         {@link ParentContainer#getPosition()}) delimited and prepended
     *         with this parent's label, recursively, back to a root of a
     *         {@link ContainerTree}. Otherwise, a user-defined label.
     */
    @NotEmpty(message = "{Container.label.NotEmpty}")
    @Column(name = "LABEL", nullable = false)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @NotNull(message = "{Container.left.NotNull}")
    @Column(name = "`LEFT`", nullable = false)
    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    @NotNull(message = "{Container.right.NotNull}")
    @Column(name = "`RIGHT`", nullable = false)
    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    @NotNull(message = "{Container.depth.NotNull}")
    @Column(name = "`DEPTH`", nullable = false)
    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
