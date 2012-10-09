package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "COLLECTION_EVENT_ANNOTATION")
public class CollectionEventAnnotation
    extends AbstractAnnotation<CollectionEventAnnotationType> {
    private static final long serialVersionUID = 1L;

    private CollectionEvent collectionEvent;

    @NotNull(message = "{CollectionEventAnnotation.collectionEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ID")
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }
}
