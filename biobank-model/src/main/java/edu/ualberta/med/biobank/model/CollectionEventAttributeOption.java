package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "COLLECTION_EVENT_ATTRIBUTE_OPTION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ATTRIBUTE_TYPE", "VALUE" }) })
@NotUsed(by = CollectionEventAttribute.class, property = "options", groups = PreDelete.class)
public class CollectionEventAttributeOption extends AttributeOption {
    private static final long serialVersionUID = 1L;
}
