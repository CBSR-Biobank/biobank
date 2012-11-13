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
@Table(name = "COLLECTION_EVENT_COMMENT")
public class CollectionEventComment
    extends Comment<CollectionEvent> {
    private static final long serialVersionUID = 1L;

    private CollectionEvent collectionEvent;

    @NotNull(message = "{CollectionEvent.collectionEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false)
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    @Override
    @Transient
    public CollectionEvent getOwner() {
        return getCollectionEvent();
    }
}
