package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Ownership, summary, storage, and classification information that applies to
 * an entire group or collection of {@link Specimen}s.
 * <p>
 * The owning {@link Study} of a {@link Specimen} is determined by
 * {@link Specimen#getSpecimenGroup()}.{@link SpecimenGroup#getStudy()}.
 * Therefore, if and when {@link Specimen}s move from one {@link Study} to
 * another, they must actually be moved specifically and carefully from one
 * {@link SpecimenGroup} to another.
 * <p>
 * It would not make sense for multiple {@link Study}s to each have their own
 * {@link Preservation} and {@link SpecimenType} data on a {@link Specimen} as
 * this could quickly go out-of-date when the {@link Specimen}s are further
 * processed. It only makes sense for their to be one {@link SpecimenGroup} per
 * {@link Specimen}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_GROUP",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "STUDY_ID", "NAME" }) })
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "group", groups = PreDelete.class),
})
public class SpecimenGroup
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;
    private Preservation preservation;
    private AnatomicalSource anatomicalSource;
    private SpecimenType specimenType;
    private Vessel vessel;
    private String unit;

    @NotNull(message = "{SpecimenGroup.study.NotNull}")
    @Column(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotEmpty(message = "{SpecimenGroup.name.NotEmpty}")
    @Size(max = 50, message = "{SpecimenGroup.name.Size}")
    @Column(name = "NAME", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Valid
    @NotNull(message = "{SpecimenGroup.preservation.NotEmpty}")
    @Embedded
    public Preservation getPreservation() {
        return preservation;
    }

    public void setPreservation(Preservation preservation) {
        this.preservation = preservation;
    }

    @NotEmpty(message = "{SpecimenGroup.anatomicalSource.NotEmpty}")
    @Column(name = "ANATOMICAL_SOURCE_ID", nullable = false)
    public AnatomicalSource getAnatomicalSource() {
        return anatomicalSource;
    }

    public void setAnatomicalSource(AnatomicalSource anatomicalSource) {
        this.anatomicalSource = anatomicalSource;
    }

    @NotEmpty(message = "{SpecimenGroup.specimenType.NotEmpty}")
    @Column(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @NotNull(message = "{SpecimenGroup.vessel.NotNull}")
    @Column(name = "VESSEL_ID", nullable = false)
    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    /**
     * @return how the {@link Specimen#amount} of each {@link Specimen} is
     *         measured (e.g. in mL, m, g, mg, etc.).
     */
    @NotEmpty(message = "{SpecimenGroup.unit.NotEmpty}")
    @Size(max = 20, message = "{SpecimenGroup.unit.Size}")
    @Column(name = "UNIT", nullable = false, length = 20)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
