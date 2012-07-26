package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "COLLECTION_EVENT_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "COLLECTION_EVENT_ID",
            "ATTRIBUTE_TYPE" }) })
@Unique(properties = { "collectionEvent", "attributeType" }, groups = PreDelete.class)
public class CollectionEventAttribute
    extends
    StudyAttribute<CollectionEventAttributeType, CollectionEventAttributeOption> {
    private static final long serialVersionUID = 1L;

    private CollectionEvent collectionEvent;

    @NotNull(message = "{CollectionEventAttribute.collectionEvent.NotNull}")
    @Column(name = "COLLECTION_EVENT_ID")
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }
}
