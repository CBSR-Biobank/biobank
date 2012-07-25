package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.model.util.RowColPos;
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
        @UniqueConstraint(columnNames = { "SITE_ID", "CONTAINER_TYPE_ID",
            "LABEL" }),
        @UniqueConstraint(columnNames = { "SITE_ID", "PRODUCT_BARCODE" }) })
// TODO: consider pulling @UniqueConstraint into this @Unique annotation,
// because this is a total repeating of constraints. Would then need to figure
// out how to add DDL constraints from our annotations and how to get a bean's
// value of a specific column.
@Unique.List({
    @Unique(properties = { "center", "containerType", "label" }, groups = PrePersist.class),
    @Unique(properties = { "center", "productBarcode" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = SpecimenPosition.class, property = "container", groups = PreDelete.class),
    @NotUsed(by = ContainerPosition.class, property = "parentContainer", groups = PreDelete.class)
})
@ValidContainer(groups = PrePersist.class)
public class Container extends AbstractModel
    implements HasComments, HasActivityStatus {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Container",
        "Containers");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString LABEL = bundle.trc(
            "model",
            "Label").format();
        public static final LString PRODUCT_BARCODE = bundle.trc(
            "model",
            "Product Barcode").format();
        public static final LString TEMPERATURE = bundle.trc(
            "model",
            "Temperature").format();
    }

    private String productBarcode;
    private String label;
    private Double temperature;
    private String path;
    private Container topContainer;
    private ContainerPosition position;
    private Center center;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private ContainerType containerType;
    private Set<Comment> comments = new HashSet<Comment>(0);

    /**
     * Optional.
     * 
     * @return
     */
    @Column(name = "PRODUCT_BARCODE")
    public String getProductBarcode() {
        return this.productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Container.label.NotEmpty}")
    @Column(name = "LABEL", nullable = false)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // TODO: should be decimal?
    @Column(name = "TEMPERATURE")
    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Column(name = "PATH")
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_COMMENT",
        joinColumns = { @JoinColumn(name = "CONTAINER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_CONTAINER_ID")
    public Container getTopContainer() {
        return this.topContainer;
    }

    public void setTopContainer(Container topContainer) {
        this.topContainer = topContainer;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.containerType.NotNull}")
    @ManyToOne
    @JoinColumn(name = "CONTAINER_TYPE_ID")
    @ForeignKey(name = "FK_Container_containerType")
    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "container", orphanRemoval = true)
    public ContainerPosition getPosition() {
        return this.position;
    }

    public void setPosition(ContainerPosition position) {
        if (this.position != null) {
            this.position.setContainer(null);
        }
        this.position = position;
        if (position != null) {
            position.setContainer(this);
        }
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Transient
    public RowColPos getPositionAsRowCol() {
        return getPosition() == null ? null : getPosition().getPosition();
    }

    @Transient
    public Container getParentContainer() {
        return getPosition() == null ? null : getPosition()
            .getParentContainer();
    }

    @Transient
    public String getPositionString() {
        Container parent = getParentContainer();
        if (parent != null) {
            RowColPos pos = getPositionAsRowCol();
            if (pos != null) {
                return parent.getContainerType().getPositionString(pos);
            }
        }
        return null;
    }

    // public boolean isPositionFree(RowColPos requestedPosition) {
    // if (getChildPositions().size() > 0) {
    // for (ContainerPosition pos : getChildPositions()) {
    // RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
    // if (requestedPosition.equals(rcp)) {
    // return false;
    // }
    // }
    // }
    //
    // // else assume this container has specimens
    // for (SpecimenPosition pos : getSpecimenPositions()) {
    // RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
    // if (requestedPosition.equals(rcp)) {
    // return false;
    // }
    // }
    // return true;
    // }

    // @Transient
    // public Container getChild(RowColPos requestedPosition) throws Exception {
    // for (ContainerPosition pos : getChildPositions()) {
    // RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
    // if (requestedPosition.equals(rcp)) {
    // return pos.getContainer();
    // }
    // }
    // return null;
    // }

    /**
     * Label can start with parent's label as prefix or without.
     * 
     * @param label
     * @return
     * @throws Exception
     */
    // @Transient
    // public Container getChildByLabel(String childLabel) throws Exception {
    // // remove parent label from child label
    // if (childLabel.startsWith(getLabel())) {
    // childLabel = childLabel.substring(getLabel().length());
    // }
    // RowColPos pos = getPositionFromLabelingScheme(getLabel());
    // return getChild(pos);
    // }

    /**
     * position is 2 letters, or 2 number or 1 letter and 1 number... this
     * position string is used to get the correct row and column index the given
     * position String.
     * 
     * @throws Exception
     */
    @SuppressWarnings("nls")
    @Transient
    public RowColPos getPositionFromLabelingScheme(String position)
        throws Exception {
        ContainerType containerType = getContainerType();

        if (containerType == null)
            throw new IllegalStateException("container type cannot be null");

        RowColPos rcp = containerType.getRowColFromPositionString(position);
        if (rcp != null) {
            if (rcp.getRow() >= containerType.getRowCapacity()
                || rcp.getCol() >= containerType.getColCapacity()) {
                throw new IllegalArgumentException("position " + position
                    + " (" + rcp + ") is out of bounds of "
                    + containerType.getCapacity());
            }
        }
        return rcp;
    }

    // public boolean hasSpecimens() {
    // return (getSpecimenPositions().size() > 0);
    // }
}
