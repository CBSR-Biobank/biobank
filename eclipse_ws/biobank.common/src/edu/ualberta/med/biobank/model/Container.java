package edu.ualberta.med.biobank.model;

import java.text.MessageFormat;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.common.util.RowColPos;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

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
    @Unique(properties = { "site", "containerType", "label" }, groups = PrePersist.class),
    @Unique(properties = { "site", "productBarcode" }, groups = PrePersist.class)
})
@Empty.List({
    @Empty(property = "specimenPositionCollection", groups = PreDelete.class),
    @Empty(property = "childPositionCollection", groups = PreDelete.class)
})
public class Container extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String productBarcode;
    private String label;
    private Double temperature;
    private String path;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);
    private Set<ContainerPosition> childPositionCollection =
        new HashSet<ContainerPosition>(0);
    private Container topContainer;
    private Set<SpecimenPosition> specimenPositionCollection =
        new HashSet<SpecimenPosition>(0);
    private ContainerType containerType;
    private ContainerPosition position;
    private Site site;
    private ActivityStatus activityStatus;

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

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_COMMENT",
        joinColumns = { @JoinColumn(name = "CONTAINER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentContainer")
    public Set<ContainerPosition> getChildPositionCollection() {
        return this.childPositionCollection;
    }

    public void setChildPositionCollection(
        Set<ContainerPosition> childPositionCollection) {
        this.childPositionCollection = childPositionCollection;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_CONTAINER_ID")
    public Container getTopContainer() {
        return this.topContainer;
    }

    public void setTopContainer(Container topContainer) {
        this.topContainer = topContainer;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "container")
    public Set<SpecimenPosition> getSpecimenPositionCollection() {
        return this.specimenPositionCollection;
    }

    public void setSpecimenPositionCollection(
        Set<SpecimenPosition> specimenPositionCollection) {
        this.specimenPositionCollection = specimenPositionCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.containerType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false)
    public ContainerType getContainerType() {
        return this.containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID", unique = true)
    public ContainerPosition getPosition() {
        return this.position;
    }

    public void setPosition(ContainerPosition position) {
        this.position = position;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.site.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false)
    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.activityStatus.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Transient
    public RowColPos getPositionAsRowCol() {
        return this.position == null ? null : this.position.getPosition();
    }

    @Transient
    public Container getParentContainer() {
        return this.position == null ? null : this.position
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

    public boolean isPositionFree(RowColPos requestedPosition) {
        if (childPositionCollection.size() > 0) {
            for (ContainerPosition pos : childPositionCollection) {
                RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
                if (requestedPosition.equals(rcp)) {
                    return false;
                }
            }
        }

        // else assume this container has specimens
        for (SpecimenPosition pos : specimenPositionCollection) {
            RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
            if (requestedPosition.equals(rcp)) {
                return false;
            }
        }
        return true;
    }

    @Transient
    public Container getChild(RowColPos requestedPosition) throws Exception {
        if (childPositionCollection.size() == 0) {
            throw new Exception("container does not have children");
        }

        for (ContainerPosition pos : childPositionCollection) {
            RowColPos rcp = new RowColPos(pos.getRow(), pos.getCol());
            if (requestedPosition.equals(rcp)) {
                return pos.getContainer();
            }
        }
        return null;
    }

    /**
     * Label can start with parent's label as prefix or without.
     * 
     * @param label
     * @return
     * @throws Exception
     */
    @Transient
    public Container getChildByLabel(String childLabel) throws Exception {
        if (containerType == null) {
            throw new Exception("container type is null");
        }

        // remove parent label from child label
        if (childLabel.startsWith(label)) {
            childLabel = childLabel.substring(label.length());
        }
        RowColPos pos = getPositionFromLabelingScheme(label);
        return getChild(pos);
    }

    /**
     * position is 2 letters, or 2 number or 1 letter and 1 number... this
     * position string is used to get the correct row and column index the given
     * position String.
     * 
     * @throws Exception
     */
    @Transient
    public RowColPos getPositionFromLabelingScheme(String position)
        throws Exception {
        RowColPos rcp = containerType.getRowColFromPositionString(position);
        if (rcp != null) {
            if (rcp.getRow() >= containerType.getRowCapacity()
                || rcp.getCol() >= containerType.getColCapacity()) {
                throw new Exception(
                    MessageFormat
                        .format(
                            "Can''t use position {0} in container {1}. Reason: capacity = {2}*{3}",
                            position, getFullInfoLabel(),
                            containerType.getRowCapacity(),
                            containerType.getColCapacity()));
            }
            if (rcp.getRow() < 0 || rcp.getCol() < 0) {
                throw new Exception(
                    MessageFormat.format(
                        "Position ''{0}'' is invalid for this container {1}",
                        position, getFullInfoLabel()));
            }
        }
        return rcp;
    }

    /**
     * @return a string with the label of this container + the short name of its
     *         type
     * 
     */
    @Transient
    public String getFullInfoLabel() {
        if ((containerType == null) || (containerType.getNameShort() == null)) {
            return getLabel();
        }
        return getLabel() + " (" + containerType.getNameShort() + ")";
    }

    public boolean hasSpecimens() {
        return (specimenPositionCollection.size() > 0);
    }

}
