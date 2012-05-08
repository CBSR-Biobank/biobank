package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

// TODO: test activityStatus property
@Audited
@Entity
@Table(name = "STUDY_EVENT_ATTR")
@NotUsed(by = EventAttr.class, property = "studyEventAttr", groups = PreDelete.class)
public class StudyEventAttr extends AbstractBiobankModel
    implements HasActivityStatus {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Study Event Attribute",
        "Study Event Attributes");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString REQUIRED = bundle.trc(
            "model",
            "Required").format();
    }

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

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.StudyEventAttr.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
