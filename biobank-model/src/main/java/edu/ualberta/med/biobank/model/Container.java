package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainer;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A specifically built physical unit that can hold child containers, or can be
 * contained in a parent container.
 * 
 */
@Audited
@Entity
@Table(name = "CONTAINER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PRODUCT_BARCODE" }),
        @UniqueConstraint(columnNames = { "CONTAINER_TREE_ID", "LABEL" })
    })
// TODO: consider pulling @UniqueConstraint into this @Unique annotation,
// because this is a total repeating of constraints. Would then need to figure
// out how to add DDL constraints from our annotations and how to get a bean's
// value of a specific column.
@Unique.List({
    @Unique(properties = { "productBarcode" }, groups = PrePersist.class),
    @Unique(properties = { "node.tree", "label" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = ParentContainer.class, property = "container", groups = PreDelete.class)
})
@ValidContainer(groups = PrePersist.class)
public class Container
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private String productBarcode;
    private ContainerType containerType;
    private ContainerNode node;
    private ContainerConstraints constraints;
    private Boolean enabled;

    /**
     * Optional, but globally unique (if specified) barcode. Global uniqueness
     * is required so that {@link Container}s, like {@link Specimen}s, can be
     * shipped between {@link Center}s.
     * 
     * @return a globally unique identifying barcode, or null if not specified.
     */
    @Column(name = "PRODUCT_BARCODE", unique = true)
    public String getProductBarcode() {
        return this.productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NotNull(message = "{Container.containerType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_TYPE_ID")
    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @NotNull(message = "{Container.node.NotNull}")
    @Valid
    @Embedded
    public ContainerNode getNode() {
        return node;
    }

    public void setNode(ContainerNode node) {
        this.node = node;
    }

    /**
     * @return optional information about what types of {@link Specimen}s this
     *         {@link Container} and its children can legally contain, or null
     *         if none specified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_CONSTRAINTS_ID")
    public ContainerConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(ContainerConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * @return true if this {@link Container} can have new {@link Specimen}s
     *         added to it, otherwise false.
     */
    @NotNull(message = "{Container.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Holds information about a {@link Container}'s place in a
     * {@link Container} hierarchy.
     * <p>
     * Uses Nested Sets (AKA Modified Preorder Tree Traversal) for more
     * efficient querying of children.
     * 
     * @author Jonathan Ferland
     */
    @Embeddable
    public static class ContainerNode implements Serializable {
        private static final long serialVersionUID = 1L;

        private ParentContainer parent;
        private ContainerTree tree;
        private String label;
        private Integer left;
        private Integer right;
        private Integer depth;

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "PARENT_CONTAINER_ID", unique = true)
        public ParentContainer getParent() {
            return parent;
        }

        public void setParent(ParentContainer parent) {
            this.parent = parent;
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
         * 
         * @return if this has a {@link #getParent()}, then return this
         *         {@link ContainerNode}'s position (i.e.
         *         {@link ParentContainer#getPosition()}) delimited and
         *         prepended with this parent's label, recursively, back to a
         *         root of a {@link ContainerTree}. Otherwise, a user-defined
         *         label.
         */
        // TODO: ask cbsr if we can just not store labels?
        // TODO: but a label is needed for easy location display and look-up?
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((parent == null) ? 0 : parent.hashCode());
            result = prime * result
                + ((tree == null) ? 0 : tree.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ContainerNode other = (ContainerNode) obj;
            if (parent == null) {
                if (other.parent != null) return false;
            } else if (!parent.equals(other.parent)) return false;
            if (tree == null) {
                if (other.tree != null) return false;
            } else if (!tree.equals(other.tree)) return false;
            return true;
        }
    }
}
