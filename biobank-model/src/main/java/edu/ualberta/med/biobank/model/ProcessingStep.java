package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a regularly performed procedure carried out on one or more
 * expected types of {@link Specimen}s. There is also one or more expected
 * resulting types of {@link Specimen}s.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "PROCESSING_STEP",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "STUDY_ID", "NAME" }) })
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessingStep.class, property = "processingStep", groups = PreDelete.class)
public class ProcessingStep
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;

    /**
     * @return the {@link Study} this {@link ProcessingStep} belongs to.
     */
    @NotNull(message = "{ProcessingStep.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    /**
     * @return a short name that uniquely identifies a {@link ProcessingStep}
     *         within a {@link Study}.
     */
    @NotEmpty(message = "{ProcessingStep.name.NotEmpty")
    @Size(max = 50, message = "{ProcessingStep.name.Size}")
    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an optional detailed description of the {@link ProcessingStep},
     *         or null.
     */
    @Size(max = 10000, message = "{ProcessingStep.description.Size}")
    @Column(name = "DESCRIPTION", length = 10000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
