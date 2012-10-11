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
    @Empty(property = "childVessels", groups = PreDelete.class)
})
@ValidContainerType(groups = PrePersist.class)
public class ContainerType
    extends AbstractVersionedModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private Boolean topLevel;
    private Boolean shared;
    private ContainerSchema schema;
    private Set<Vessel> vessels = new HashSet<Vessel>(0);
    private Set<ContainerType> containerTypes = new HashSet<ContainerType>(0);
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

    /**
     * @return true if this {@link ContainerType} should <em>not</em> be able to
     *         be the child of another {@link ContainerType}, otherwise false.
     */
    @NotNull(message = "{ContainerType.topLevel.NotNull}")
    @Column(name = "IS_TOP_LEVEL", nullable = false)
    public Boolean isTopLevel() {
        return this.topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    /**
     * @return true if this {@link ContainerType} can be used by (but not
     *         modified by) other {@link Center}s, otherwise false.
     */
    @NotNull(message = "{ContainerType.shared.NotNull}")
    @Column(name = "IS_SHARED", nullable = false)
    public Boolean isShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    /**
     * @return how {@link Container}s with this {@link ContainerType} are
     *         designed and laid out, with labelled positions for children.
     */
    @NotNull(message = "{ContainerType.schema.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_SCHEMA_ID", nullable = false)
    public ContainerSchema getSchema() {
        return schema;
    }

    public void setSchema(ContainerSchema schema) {
        this.schema = schema;
    }

    /**
     * @return the {@link Vessel}s that this {@link ContainerType} can hold as
     *         children (must be empty if {@link #getChildContainerTypes()} is
     *         not).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_VESSEL",
        joinColumns = { @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_VESSEL_ID", nullable = false, updatable = false) })
    public Set<Vessel> getChildVessels() {
        return vessels;
    }

    public void setChildVessels(Set<Vessel> childVessels) {
        this.vessels = childVessels;
    }

    /**
     * @return the {@link ContainerType}s that this {@link ContainerType} can
     *         hold as children (must be empty if {@link #getVessels()} is not).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_CONTAINER_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", nullable = false, updatable = false) })
    public Set<ContainerType> getChildContainerTypes() {
        return this.containerTypes;
    }

    public void setChildContainerTypes(Set<ContainerType> childContainerTypes) {
        this.containerTypes = childContainerTypes;
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
