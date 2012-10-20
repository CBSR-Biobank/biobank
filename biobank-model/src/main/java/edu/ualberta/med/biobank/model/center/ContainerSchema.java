package edu.ualberta.med.biobank.model.center;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A plan for how the children in a {@link Container} are positioned and
 * labelled.
 * 
 * @author Jonathan Ferland
 * @see ContainerSchemaPosition
 */
@Audited
@Entity
@Table(name = "CONTAINER_SCHEMA",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CENTER_ID", "NAME" })
    })
@Unique(properties = { "center", "name" }, groups = PrePersist.class)
@NotUsed(by = ContainerSchemaPosition.class, property = "schema", groups = PreDelete.class)
public class ContainerSchema
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private Boolean shared;

    @NotNull(message = "{ContainerSchema.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @NotEmpty(message = "{ContainerSchema.name.NotEmpty}")
    @Size(max = 30, message = "{ContainerSchema.name.Size}")
    @Column(name = "NAME", length = 30, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 5000, message = "{ContainerSchema.name.Size}")
    @Column(name = "NAME", length = 5000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{ContainerSchema.shared.NotNull}")
    @Column(name = "IS_SHARED", nullable = false)
    public Boolean isShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }
}
