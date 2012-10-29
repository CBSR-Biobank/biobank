package edu.ualberta.med.biobank.model.study;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Audited
@Entity
@DiscriminatorValue("CLEV")
@NotUsed.List({
    @NotUsed(by = CollectionEventAnnotation.class, property = "type", groups = PreDelete.class)
})
public class CollectionEventAnnotationType
    extends StudyAnnotationType {
    private static final long serialVersionUID = 1L;
}