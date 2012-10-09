package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@DiscriminatorValue("SPPE")
@NotUsed.List({
    @NotUsed(by = SpecimenProcessingEventAnnotation.class, property = "type", groups = PreDelete.class)
})
public class SpecimenProcessingEventAnnotationType
    extends AbstractAnnotationType {
    private static final long serialVersionUID = 1L;
}
