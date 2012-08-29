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
 * 
 * @author Jonathan Ferland
 * @see ContainerSchema
 */

@Audited
@Immutable
@Entity
@Table(name = "CONTAINER_CHILD_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CONTAINER_CHILD_SCHEMA_ID", "LABEL" })
    })
@Unique(properties = { "schema", "label" }, groups = PrePersist.class)
@NotUsed(by = ParentContainer.class, property = "position", groups = PreDelete.class)
public class ContainerChildPosition
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private ContainerSchema schema;
    private String label;

    @NotNull(message = "{ContainerChildPosition.schema.NotNull}")
    @ManyToOne
    @JoinColumn(name = "CONTAINER_CHILD_SCHEMA_ID", nullable = false, updatable = false)
    public ContainerSchema getSchema() {
        return schema;
    }

    public void setSchema(ContainerSchema schema) {
        this.schema = schema;
    }

    @NotNull(message = "{ContainerChildPosition.label.NotNull}")
    @Size(max = 4, message = "{ContainerChildPosition.label.Size}")
    @Column(name = "LABEL", length = 4, nullable = false, updatable = false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        ContainerChildPosition other = (ContainerChildPosition) obj;
        if (label == null) {
            if (other.label != null) return false;
        } else if (!label.equals(other.label)) return false;
        if (schema == null) {
            if (other.schema != null) return false;
        } else if (!schema.equals(other.schema)) return false;
        return true;
    }
}
