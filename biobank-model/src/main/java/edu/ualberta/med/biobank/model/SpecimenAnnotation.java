package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@DiscriminatorValue("SP")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "SPECIMEN_ID", "ANNOTATION_TYPE_ID" }) })
@Unique(properties = { "specimen", "type" }, groups = PrePersist.class)
public class SpecimenAnnotation
    extends AbstractAnnotation {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;

    @NotNull(message = "{SpecimenAnnotation.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID")
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }
}
