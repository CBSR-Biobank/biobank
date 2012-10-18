package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Determines which {@link CollectionEventAnnotationType}s either can or must
 * (determined by {@link #isRequired()}) be recorded on which
 * {@link CollectionEventType}s.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "COLLECTION_EVENT_TYPE_ANNOTATION_TYPE")
@Unique(properties = { "collectionEventType", "annotationType" }, groups = PrePersist.class)
public class CollectionEventTypeAnnotationType
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private CollectionEventType collectionEventType;
    private CollectionEventAnnotationType annotationType;
    private Boolean required;

    @NaturalId
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "{CollectionEventTypeAnnotationType.collectionEventType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_TYPE_ID", nullable = false)
    public CollectionEventType getCollectionEventType() {
        return collectionEventType;
    }

    public void setCollectionEventType(CollectionEventType collectionEventType) {
        this.collectionEventType = collectionEventType;
    }

    @NaturalId
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "{CollectionEventTypeAnnotationType.collectionEventType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ANNOTATION_TYPE", nullable = false)
    public CollectionEventAnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(CollectionEventAnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    /**
     * @return true if a value for {@link #getAnnotationType()} <em>must</em> be
     *         recorded whenever a {@link SpecimenLink} of type
     *         {@link #getLinkType()} is created, otherwise false when a value
     *         is optional.
     */
    @NotNull(message = "{CollectionEventTypeAnnotationType.required.NotNull}")
    @Column(name = "IS_REQUIRED", nullable = false)
    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
