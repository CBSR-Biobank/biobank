package edu.ualberta.med.biobank.model.study;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Audited
@Entity
@DiscriminatorValue("PTNT")
@NotUsed.List({
    @NotUsed(by = PatientAnnotation.class, property = "type", groups = PreDelete.class)
})
public class PatientAnnotationType
    extends StudyAnnotationType {
    private static final long serialVersionUID = 1L;
}
