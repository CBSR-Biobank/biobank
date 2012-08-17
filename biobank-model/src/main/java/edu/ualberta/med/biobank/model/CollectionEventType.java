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

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A uniquely named (with its {@link Study}) classification of visit to a
 * {@link Patient}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "COLLECTION_EVENT_TYPE",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "STUDY_ID", "NAME" }) })
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
public class CollectionEventType
    extends AbstractVersionedModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;
    private Boolean recurring;

    @NotNull(message = "{CollectionEventType.study.NotNull")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    @NotEmpty(message = "{CollectionEventType.name.NotEmpty")
    @Size(max = 50, message = "{CollectionEventType.name.Size}")
    @Column(name = "NAME", length = 50)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotEmpty(message = "{CollectionEventType.description.NotEmpty")
    @Size(max = 10000, message = "{CollectionEventType.description.Size}")
    @Column(name = "DESCRIPTION", length = 10000)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{CollectionEventType.recurring.NotNull")
    @Column(name = "IS_RECURRING", nullable = false)
    public Boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }
}
