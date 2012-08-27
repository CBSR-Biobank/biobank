package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "SPECIMEN_ANNOTATION")
public class SpecimenAnnotation
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private Annotation annotation;

    @NotNull(message = "{SpecimenAnnotation.specimen.NotNull}")
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}
