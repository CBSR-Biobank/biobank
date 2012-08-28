package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainerType;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Describes a container configuration which may hold other child containers or
 * specimens. Container types are used to create a representation of a physical
 * container
 * 
 * ET: Describes various containers that can hold specimens, these container
 * types are used to build a container.
 * 
 */
@Audited
@Entity
@Table(name = "CONTAINER_TYPE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CENTER_ID", "NAME" }) })
@Unique.List({
    @Unique(properties = { "center", "name" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = ContainerType.class, property = "childContainerTypes", groups = PreDelete.class),
    @NotUsed(by = Container.class, property = "containerType", groups = PreDelete.class),
    @NotUsed(by = SpecimenPosition.class, property = "container.containerType", groups = PreDelete.class)
})
@Empty.List({
    @Empty(property = "childContainerTypes", groups = PreDelete.class),
    @Empty(property = "specimenTypes", groups = PreDelete.class)
})
@ValidContainerType(groups = PrePersist.class)
public class ContainerType extends AbstractModel
    implements HasName, HasDescription, HasComments {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private Boolean topLevel;
    private Boolean shared;
    private final Set<Vessel> vessels = new HashSet<Vessel>(0);
    private Set<ContainerType> childContainerTypes =
        new HashSet<ContainerType>(0);
    private Capacity capacity = new Capacity();
    private ContainerLabelingScheme childLabelingScheme;
    private Boolean enabled;

    @Override
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @NotEmpty(message = "{ContainerType.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "{ContainerType.topLevel.NotNull}")
    @Column(name = "IS_TOP_LEVEL", nullable = false)
    public Boolean isTopLevel() {
        return this.topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    // TODO: change to decimal
    @Column(name = "DEFAULT_TEMPERATURE")
    public Double getDefaultTemperature() {
        return this.defaultTemperature;
    }

    public void setDefaultTemperature(Double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_SPECIMEN_TYPE",
        joinColumns = { @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false, updatable = false) })
    public Set<SpecimenType> getSpecimenTypes() {
        return this.specimenTypes;
    }

    public void setSpecimenTypes(Set<SpecimenType> specimenTypes) {
        this.specimenTypes = specimenTypes;
    }

    /**
     * The custom @SQLInsert allows a `CENTER_ID` to be inserted into the
     * correlation table so a foreign key can be created to ensure that
     * {@link ContainerType}-s with the same {@link Site} can be related.
     * 
     * @return
     */
    @SQLInsert(sql = "INSERT INTO `CONTAINER_TYPE_CONTAINER_TYPE` (PARENT_CONTAINER_TYPE_ID, CHILD_CONTAINER_TYPE_ID, CENTER_ID) SELECT ?, ID, CENTER_ID FROM `CONTAINER_TYPE` WHERE ID = ?")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_CONTAINER_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", nullable = false, updatable = false) })
    @ForeignKey(name = "FK_ContainerType_childContainerTypes", inverseName = "FK_ContainerType_parentContainerTypes")
    public Set<ContainerType> getChildContainerTypes() {
        return this.childContainerTypes;
    }

    public void setChildContainerTypes(Set<ContainerType> childContainerTypes) {
        this.childContainerTypes = childContainerTypes;
    }

    @NotNull(message = "{ContainerType.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_COMMENT",
        joinColumns = { @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Valid
    @NotNull(message = "{ContainerType.capacity.NotNull}")
    @Embedded
    public Capacity getCapacity() {
        return this.capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    @NotNull(message = "{ContainerType.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @NotAudited
    @NotNull(message = "{ContainerType.childLabelingScheme.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHILD_LABELING_SCHEME_ID", nullable = false)
    public ContainerLabelingScheme getChildLabelingScheme() {
        return this.childLabelingScheme;
    }

    public void setChildLabelingScheme(
        ContainerLabelingScheme childLabelingScheme) {
        this.childLabelingScheme = childLabelingScheme;
    }

    @Transient
    public Integer getRowCapacity() {
        return this.getCapacity().getRowCapacity();
    }

    @Transient
    public Integer getColCapacity() {
        return this.getCapacity().getColCapacity();
    }

    @Transient
    public String getPositionString(RowColPos position) {
        return ContainerLabelingScheme.getPositionString(position,
            getChildLabelingScheme().getId(), getRowCapacity(),
            getColCapacity());

    }

    @Transient
    public RowColPos getRowColFromPositionString(String position)
        throws Exception {
        return getChildLabelingScheme()
            .getRowColFromPositionString(position, getRowCapacity(),
                getColCapacity());
    }

    @Transient
    public boolean isPallet96() {
        return RowColPos.PALLET_96_ROW_MAX.equals(getRowCapacity())
            && RowColPos.PALLET_96_COL_MAX.equals(getColCapacity());
    }
}
