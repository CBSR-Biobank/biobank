package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;

import edu.ualberta.med.biobank.model.Study;

public class StudyEvent extends Event {
    private static final long serialVersionUID = 1L;

    private Study study;

    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
