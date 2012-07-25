package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "STUDY_CENTER")
public class StudyCenter extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Center center;

    @NotNull(message = "{StudyCenter.study.NotNull}")
    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{StudyCenter.center.NotNull}")
    @Column(name = "CENTER_ID")
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
