package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

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
    @NotUsed(by = Container.class, property = "containerType", groups = PreDelete.class)
})
@Empty.List({
    @Empty(property = "childContainerTypes", groups = PreDelete.class),
    @Empty(property = "specimenTypes", groups = PreDelete.class)
})
@ValidContainerType(groups = PrePersist.class)
public class ContainerType extends AbstractModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private Boolean topLevel;
    private Boolean shared;
    private final Set<Vessel> vessels = new HashSet<Vessel>(0);
    private Set<ContainerType> childContainerTypes =
        new HashSet<ContainerType>(0);
    private ContainerSchema childSchema;
    private Boolean enabled;

    /**
     * @return the {@link Center} that owns and is allowed to modify this
     *         {@link ContainerType}.
     */
    @NotNull(message = "{ContainerType.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Override
    @NotEmpty(message = "{ContainerType.name.NotEmpty}")
    @Size(max = 50, message = "{ContainerType.name.Size}")
    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Size(max = 5000, message = "{ContainerType.description.Size}")
    @Column(name = "DESCRIPTION", length = 5000)
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{ContainerType.topLevel.NotNull}")
    @Column(name = "IS_TOP_LEVEL", nullable = false)
    public Boolean isTopLevel() {
        return this.topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    /**
     * @return the {@link ContainerType}s that this {@link ContainerType} can
     *         hold as children.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_CONTAINER_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", nullable = false, updatable = false) })
    public Set<ContainerType> getChildContainerTypes() {
        return this.childContainerTypes;
    }

    public void setChildContainerTypes(Set<ContainerType> childContainerTypes) {
        this.childContainerTypes = childContainerTypes;
    }

    /**
     * @return true if this {@link ContainerType} can be used to create new
     *         {@link Container}s, or false if this {@link ContainerType} is to
     *         be used only for existing {@link Container}s.
     */
    @NotNull(message = "{ContainerType.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
