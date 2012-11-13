package edu.ualberta.med.biobank.model.study;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Comment;

@Entity
@Table(name = "SPECIMEN_COMMENT")
public class SpecimenComment
    extends Comment<Specimen> {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;

    @NotNull(message = "{SpecimenComment.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @Override
    @Transient
    public Specimen getOwner() {
        return getSpecimen();
    }
}