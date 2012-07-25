package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "EVENT_ATTR")
public class EventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String value;
    private StudyEventAttr studyEventAttr;

    @Column(name = "VALUE")
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.EventAttr.studyEventAttr.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_EVENT_ATTR_ID", nullable = false)
    public StudyEventAttr getStudyEventAttr() {
        return this.studyEventAttr;
    }

    public void setStudyEventAttr(StudyEventAttr studyEventAttr) {
        this.studyEventAttr = studyEventAttr;
    }
}
