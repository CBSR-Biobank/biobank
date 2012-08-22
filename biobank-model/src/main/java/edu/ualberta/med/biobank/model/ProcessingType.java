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
 * Describes a regularly performed procedure with a unique name (within its
 * {@link Study}). There should be one or more associated
 * {@link SpecimenProcesingType}s and {@link SpecimenProcesingLinkType}s that
 * (1) further define legal procedures and (2) allow logging of procedures
 * performed on different types of {@link Specimen}s.
 * 
 * @author Jonathan Ferland
 * @see SpecimenProcessingType
 */
@Audited
@Entity
@Table(name = "PROCESSING_TYPE",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "STUDY_ID", "NAME" }) })
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessingType.class, property = "type", groups = PreDelete.class)
public class ProcessingType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;
    private Boolean enabled;

    /**
     * @return the {@link Study} this {@link ProcessingType} belongs to.
     */
    @NotNull(message = "{ProcessingType.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    /**
     * @return a short name that uniquely identifies a {@link ProcessingType}
     *         within a {@link Study}.
     */
    @NotEmpty(message = "{ProcessingType.name.NotEmpty")
    @Size(max = 50, message = "{ProcessingType.name.Size}")
    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an optional detailed description of the {@link ProcessingType},
     *         or null.
     */
    @Size(max = 10000, message = "{ProcessingType.description.Size}")
    @Column(name = "DESCRIPTION", length = 10000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return true if this {@link ProcessingType} is still being performed, or
     *         false if this {@link ProcessingType} is no longer being carried
     *         out, but exists for historical and record-keeping purposes.
     */
    @NotNull(message = "{ProcessingType.enabled.NotNull}")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
