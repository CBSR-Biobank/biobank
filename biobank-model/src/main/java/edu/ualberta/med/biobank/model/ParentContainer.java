package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Immutable
@Entity
@Table(name = "PARENT_CONTAINER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "CONTAINER_ID",
            "CONTAINER_SCHEMA_POSITION_ID" })
    })
@Unique(properties = { "container", "position" }, groups = PrePersist.class)
public class ParentContainer
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Container container;
    private ContainerSchemaPosition position;

    @NotNull(message = "{ParentContainer.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CONTAINER_ID", nullable = false, updatable = false)
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "{ParentContainer.position.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_SCHEMA_POSITION_ID", nullable = false, updatable = false)
    public ContainerSchemaPosition getPosition() {
        return position;
    }

    public void setPosition(ContainerSchemaPosition position) {
        this.position = position;
    }
}
