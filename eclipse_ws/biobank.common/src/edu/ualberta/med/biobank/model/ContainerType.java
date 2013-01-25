package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

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
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
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
@Entity
@Table(name = "CONTAINER_TYPE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SITE_ID", "NAME" }),
        @UniqueConstraint(columnNames = { "SITE_ID", "NAME_SHORT" }) })
@Unique.List({
    @Unique(properties = { "site", "name" }, groups = PrePersist.class),
    @Unique(properties = { "site", "nameShort" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = Container.class, property = "containerType", groups = PreDelete.class),
    @NotUsed(by = SpecimenPosition.class, property = "container.containerType", groups = PreDelete.class)
})
@Empty.List({
    @Empty(property = "childContainerTypes", groups = PreDelete.class),
    @Empty(property = "parentContainerTypes", groups = PreDelete.class),
    @Empty(property = "specimenTypes", groups = PreDelete.class)
})
@ValidContainerType(groups = PrePersist.class)
public class ContainerType extends AbstractBiobankModel
    implements HasName, HasNameShort, HasActivityStatus, HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Container Type",
        "Container Types");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString CHILD_LABELING_SCHEME = bundle.trc(
            "model",
            "Child Labeling Scheme").format();
        public static final LString DEFAULT_TEMPERATURE = bundle.trc(
            "model",
            "Default Temperature").format();
        public static final LString TOP_LEVEL = bundle.trc(
            "model",
            "Top Level").format();
    }

    private String name;
    private String nameShort;
    private boolean topLevel = false;
    private Double defaultTemperature;
    private Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>(0);
    private Set<ContainerType> childContainerTypes =
        new HashSet<ContainerType>(0);
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Capacity capacity = new Capacity();
    private Site site;
    private ContainerLabelingScheme childLabelingScheme;
    private Set<ContainerType> parentContainerTypes =
        new HashSet<ContainerType>(0);

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.ContainerType.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.ContainerType.name.NotEmpty}")
    @Column(name = "NAME_SHORT")
    public String getNameShort() {
        return this.nameShort;
    }

    @Override
    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @Column(name = "TOP_LEVEL")
    // TODO: rename to isTopLevel
    public boolean getTopLevel() {
        return this.topLevel;
    }

    public void setTopLevel(boolean topLevel) {
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
     * The custom @SQLInsert allows a `SITE_ID` to be inserted into the
     * correlation table so a foreign key can be created to ensure that
     * {@link ContainerType}-s with the same {@link Site} can be related.
     * 
     * @return
     */
    @SQLInsert(sql = "INSERT INTO `CONTAINER_TYPE_CONTAINER_TYPE` (PARENT_CONTAINER_TYPE_ID, CHILD_CONTAINER_TYPE_ID, SITE_ID) SELECT ?, ID, SITE_ID FROM `CONTAINER_TYPE` WHERE ID = ?")
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

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerType.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Override
    @ManyToMany(fetch = FetchType.LAZY)
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
    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerType.capacity.NotNull}")
    @Embedded
    public Capacity getCapacity() {
        return this.capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerType.site.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false)
    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerType.childLabelingScheme.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHILD_LABELING_SCHEME_ID", nullable = false)
    public ContainerLabelingScheme getChildLabelingScheme() {
        return this.childLabelingScheme;
    }

    public void setChildLabelingScheme(
        ContainerLabelingScheme childLabelingScheme) {
        this.childLabelingScheme = childLabelingScheme;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "childContainerTypes")
    public Set<ContainerType> getParentContainerTypes() {
        return this.parentContainerTypes;
    }

    public void setParentContainerTypes(Set<ContainerType> parentContainerTypes) {
        this.parentContainerTypes = parentContainerTypes;
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
}
