package edu.ualberta.med.biobank.model.study;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.AnnotationType;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@DiscriminatorValue("STDY")
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
public abstract class StudyAnnotationType
    extends AnnotationType {
    private static final long serialVersionUID = 1L;

    private Study study;

    /**
     * @return the {@link Study} that this {@link StudyAnnotationType} belongs
     *         to.
     */
    @NotNull(message = "{AnnotationType.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
