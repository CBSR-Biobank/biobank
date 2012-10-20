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
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;
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
        @UniqueConstraint(columnNames = { "CENTER_ID", "NAME" })
    })
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Unique.List({
    @Unique(properties = { "center", "name" }, groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = ContainerType.class, property = "childContainerTypes", groups = PreDelete.class),
    @NotUsed(by = Container.class, property = "containerType", groups = PreDelete.class)
})
@Empty.List({
    @Empty(property = "childContainerTypes", groups = PreDelete.class)
})
@ValidContainerType(groups = PrePersist.class)
public abstract class ContainerType
    extends VersionedLongIdModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private ContainerSchema schema;
    private Boolean shared;
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
