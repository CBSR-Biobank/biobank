package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.util.HashCodeBuilderProvider;
import edu.ualberta.med.biobank.model.util.ProxyUtil;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Joins {@link Specimen}s to {@link CollectionEvent}s, which provides
 * generalised parentage information about where {@link Specimen}s came from (
 * {@link SpecimenProcessingLink} provides much more specific information). The
 * {@link #isOriginalSpecimen()} value is used to determine whether the
 * {@link #getSpecimen()} truly was directly collected from the {@link Patient}
 * (e.g. blood directly drawn or urine directly collected).
 * <p>
 * It is possible for {@link Specimen}s to have at most one
 * {@link CollectionEvent} per {@link Study}.
 * 
 * @author Jonathan Ferland
 * @see SpecimenProcessingLink
 */
@Audited
@Entity
@Table(name = "STUDY_SPECIMEN")
@Unique(properties = { "study", "specimen" }, groups = PrePersist.class)
public class StudySpecimen
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(StudySpecimen.class, 31, 37);

    private Study study;
    private Specimen specimen;
    private CollectionEvent collectionEvent;
    private Boolean originalSpecimen;

    @NaturalId
    @NotNull(message = "{StudySpecimen.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NaturalId
    @NotNull(message = "{StudySpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull(message = "{StudySpecimen.collectionEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false)
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    /**
     * @return true if the {@link #specimen} was collected <em>directly</em>
     *         from the {@link #collectionEvent}'s
     *         {@link CollectionEvent#getPatient()}, otherwise return false.
     */
    @NotNull(message = "{StudySpecimen.originalSpecimen.NotNull}")
    @Column(name = "IS_ORIGINAL_SPECIMEN", nullable = false)
    public Boolean isOriginalSpecimen() {
        return originalSpecimen;
    }

    public void setOriginalSpecimen(Boolean originalSpecimen) {
        this.originalSpecimen = originalSpecimen;
    }

    @Override
    public int hashCode() {
        return hashCodeBuilderProvider.get()
            .append(getStudy())
            .append(getSpecimen())
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!ProxyUtil.sameClass(this, obj)) return false;
        StudySpecimen rhs = (StudySpecimen) obj;
        return new EqualsBuilder()
            .append(getStudy(), rhs.getStudy())
            .append(getSpecimen(), rhs.getSpecimen())
            .isEquals();
    }
}
