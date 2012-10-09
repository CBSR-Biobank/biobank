package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "SPECIMEN_ANNOTATION")
public class SpecimenAnnotation
    extends AbstractAnnotation<SpecimenAnnotationType> {
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
