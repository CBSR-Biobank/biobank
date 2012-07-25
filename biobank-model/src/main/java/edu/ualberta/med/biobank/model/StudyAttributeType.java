package edu.ualberta.med.biobank.model;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@MappedSuperclass
@Unique(properties = { "study", "label" }, groups = PreDelete.class)
public abstract class StudyAttributeType<T extends AttributeOption>
    extends AttributeType<T> {
    private static final long serialVersionUID = 1L;

    private Study study;

    @NotNull(message = "{edu.ualberta.med.biobank.model.StudyAttributeType.study.NotNull}")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
