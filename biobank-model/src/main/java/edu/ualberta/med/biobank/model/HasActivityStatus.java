package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.model.type.ActivityStatus;

public interface HasActivityStatus {
    public ActivityStatus getActivityStatus();

    public void setActivityStatus(ActivityStatus activityStatus);
}
