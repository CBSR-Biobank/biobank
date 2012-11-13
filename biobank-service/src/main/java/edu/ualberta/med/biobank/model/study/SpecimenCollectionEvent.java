package edu.ualberta.med.biobank.model.study;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Joins {@link Specimen}s to {@link CollectionEvent}s, which provides
 * generalised parentage information about where {@link Specimen}s came from (
 * {@link SpecimenLink} provides much more specific information). It is possible
 * for {@link Specimen}s to have multiple {@link CollectionEvent}s per
 * {@link Study}, but not recommended.
 * <p>
 * Having multiple {@link CollectionEvent}s allows each {@link Study} to have
 * their own metadata on a {@link Specimen}. This allows for cases where one
 * {@link Study} collects and transfers {@link Specimens} to another
 * {@link Study}.
 * 
 * @author Jonathan Ferland
 * @see SpecimenLink
 */
@Audited
@Entity
@Table(name = "SPECIMEN_COLLECTION_EVENT",
    uniqueConstraints = @UniqueConstraint(columnNames = {
        "SPECIMEN_ID",
        "COLLECTION_EVENT_ID"
    }))
@Unique(properties = { "specimen", "collectionEvent" }, groups = PrePersist.class)
public class SpecimenCollectionEvent
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private CollectionEvent collectionEvent;

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
}
