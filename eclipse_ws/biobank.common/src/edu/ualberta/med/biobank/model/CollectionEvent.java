package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

public class CollectionEvent extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer visitNumber;
    private Collection<Specimen> allSpecimenCollection =
        new HashSet<Specimen>();
    private Patient patient;
    private ActivityStatus activityStatus;
    private Collection<EventAttr> eventAttrCollection =
        new HashSet<EventAttr>();
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Collection<Specimen> originalSpecimenCollection =
        new HashSet<Specimen>();

    @NotNull
    @Min(value = 1)
    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public Collection<Specimen> getAllSpecimenCollection() {
        return allSpecimenCollection;
    }

    public void setAllSpecimenCollection(
        Collection<Specimen> allSpecimenCollection) {
        this.allSpecimenCollection = allSpecimenCollection;
    }

    @NotNull
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @NotNull
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<EventAttr> getEventAttrCollection() {
        return eventAttrCollection;
    }

    public void setEventAttrCollection(Collection<EventAttr> eventAttrCollection) {
        this.eventAttrCollection = eventAttrCollection;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public Collection<Specimen> getOriginalSpecimenCollection() {
        return originalSpecimenCollection;
    }

    public void setOriginalSpecimenCollection(
        Collection<Specimen> originalSpecimenCollection) {
        this.originalSpecimenCollection = originalSpecimenCollection;
    }
}
