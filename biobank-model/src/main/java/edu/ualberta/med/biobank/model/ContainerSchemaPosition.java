package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a labelled position that a child (e.g. a {@link Container} or a
 * {@link Specimen}) has in a parent {@link Container}. Labels are associated
 * with a single {@link ContainerSchema}.
 * <p>
 * This is its own class instead of an {@link javax.persistence.Embeddable}
 * (i.e. a value type) because it must be referenced and the {@link #label}
 * could be quite long.
 * 
 * @author Jonathan Ferland
 * @see ContainerSchema
 */
@Audited
@Immutable
@Entity
@Table(name = "CONTAINER_SCHEMA_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CONTAINER_SCHEMA_ID", "LABEL" })
    })
@Unique(properties = { "schema", "label" }, groups = PrePersist.class)
@NotUsed(by = ParentContainer.class, property = "position", groups = PreDelete.class)
public class ContainerSchemaPosition
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private ContainerSchema schema;
    private String label;

    @NotNull(message = "{ContainerSchemaPosition.schema.NotNull}")
    @ManyToOne
    @JoinColumn(name = "CONTAINER_SCHEMA_ID", nullable = false, updatable = false)
    public ContainerSchema getSchema() {
        return schema;
    }

    public void setSchema(ContainerSchema schema) {
        this.schema = schema;
    }

    @NotNull(message = "{ContainerSchemaPosition.label.NotNull}")
    @Size(max = 4, message = "{ContainerSchemaPosition.label.Size}")
    @Column(name = "LABEL", length = 4, nullable = false, updatable = false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
