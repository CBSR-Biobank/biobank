package edu.ualberta.med.biobank.model;

public class StudyEventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String permissible;
    private Boolean required = false;
    private GlobalEventAttr globalEventAttr;
    private Study study;
    private ActivityStatus activityStatus;

    public String getPermissible() {
        return permissible;
    }

    public void setPermissible(String permissible) {
        this.permissible = permissible;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public GlobalEventAttr getGlobalEventAttr() {
        return globalEventAttr;
    }

    public void setGlobalEventAttr(GlobalEventAttr globalEventAttr) {
        this.globalEventAttr = globalEventAttr;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
