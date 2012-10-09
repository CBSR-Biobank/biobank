package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@DiscriminatorValue("SP")
@NotUsed.List({
    @NotUsed(by = SpecimenAnnotation.class, property = "type", groups = PreDelete.class)
})
public class SpecimenAnnotationType
    extends AbstractAnnotationType {
    private static final long serialVersionUID = 1L;
}