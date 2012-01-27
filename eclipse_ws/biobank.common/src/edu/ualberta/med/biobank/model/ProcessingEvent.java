package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class ProcessingEvent extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String worksheet;
    private Date createdAt;
    private Center center;
    private Collection<Specimen> specimenCollection = new HashSet<Specimen>();
    private ActivityStatus activityStatus;
    private Collection<Comment> commentCollection = new HashSet<Comment>();

    public String getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(String worksheet) {
        this.worksheet = worksheet;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Collection<Specimen> getSpecimenCollection() {
        return specimenCollection;
    }

    public void setSpecimenCollection(Collection<Specimen> specimenCollection) {
        this.specimenCollection = specimenCollection;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
