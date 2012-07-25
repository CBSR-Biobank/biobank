package edu.ualberta.med.biobank.model;

import javax.persistence.MappedSuperclass;

import edu.ualberta.med.biobank.validator.constraint.Unique;

@MappedSuperclass
@Unique(properties = { "study", "attributeType" })
public abstract class StudyAttribute<T extends StudyAttributeType<U>, U extends AttributeOption>
    extends Attribute<T, U> {
    private static final long serialVersionUID = 1L;
}
