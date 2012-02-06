package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;

@Entity
@Table(name = "SPECIMEN_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CONTAINER_ID", "ROW", "COL" }) })
@Unique(properties = { "container", "row", "col" },
    groups = { PreInsert.class, PreUpdate.class },
    message = "{edu.ualberta.med.biobank.model.SpecimenPosition.position.Unique}")
public class SpecimenPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container container;
    private Specimen specimen;

    @NotNull(message = "edu.ualberta.med.biobank.model.SpecimenPosition.container.NotNull")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "edu.ualberta.med.biobank.model.SpecimenPosition.specimen.NotNull")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false, unique = true)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }
}
