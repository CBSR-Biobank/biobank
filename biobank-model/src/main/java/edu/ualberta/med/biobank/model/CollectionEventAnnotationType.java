package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@DiscriminatorValue("CE")
@NotUsed.List({
    @NotUsed(by = CollectionEventAnnotation.class, property = "type", groups = PreDelete.class)
})
public class CollectionEventAnnotationType
    extends AbstractAnnotationType {
    private static final long serialVersionUID = 1L;
}