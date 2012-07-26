package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "SPECIMEN_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "STUDY_ID", "LABEL" }) })
@Unique(properties = { "specimen", "attributeType" }, groups = PreDelete.class)
public class SpecimenAttribute
    extends StudyAttribute<SpecimenAttributeType, SpecimenAttributeOption> {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;

    @NotNull(message = "{SpecimenAttribute.specimen.NotNull}")
    @Column(name = "SPECIMEN_ID")
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }
}
