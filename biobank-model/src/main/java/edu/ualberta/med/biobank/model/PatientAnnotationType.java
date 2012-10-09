package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@DiscriminatorValue("PT")
@NotUsed.List({
    @NotUsed(by = PatientAnnotation.class, property = "type", groups = PreDelete.class)
})
public class PatientAnnotationType
    extends AbstractAnnotationType {
    private static final long serialVersionUID = 1L;
}
