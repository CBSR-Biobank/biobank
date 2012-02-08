package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "STUDY_EVENT_ATTR")
@NotUsed(by = EventAttr.class, property = "studyEventAttr", groups = PreDelete.class)
public class StudyEventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String permissible;
    private boolean required = false;
    private GlobalEventAttr globalEventAttr;
    private Study study;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;

    @Column(name = "PERMISSIBLE")
    public String getPermissible() {
        return this.permissible;
    }

    public void setPermissible(String permissible) {
        this.permissible = permissible;
    }

    @Column(name = "REQUIRED")
    // TODO: rename to isRequired
    public boolean getRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.StudyEventAttr.globalEventAttr.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLOBAL_EVENT_ATTR_ID", nullable = false)
    public GlobalEventAttr getGlobalEventAttr() {
        return this.globalEventAttr;
    }

    public void setGlobalEventAttr(GlobalEventAttr globalEventAttr) {
        this.globalEventAttr = globalEventAttr;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.StudyEventAttr.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.StudyEventAttr.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
