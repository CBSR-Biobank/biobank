package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "COLLECTION_EVENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PATIENT_ID", "VISIT_NUMBER" }) })
@Unique(properties = { "patient", "visitNumber" }, groups = PrePersist.class)
@Empty(property = "allSpecimenCollection", groups = PreDelete.class)
public class CollectionEvent extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer visitNumber;
    private Set<Specimen> allSpecimenCollection = new HashSet<Specimen>(
        0);
    private Patient patient;
    private ActivityStatus activityStatus;
    private Set<EventAttr> eventAttrCollection = new HashSet<EventAttr>(
        0);
    private Set<Comment> commentCollection = new HashSet<Comment>(0);
    private Set<Specimen> originalSpecimenCollection =
        new HashSet<Specimen>(0);

    @Min(value = 1, message = "{edu.ualberta.med.biobank.model.CollectionEvent.visitNumber.Min}")
    @NotNull(message = "{edu.ualberta.med.biobank.model.CollectionEvent.visitNumber.NotNull}")
    @Column(name = "VISIT_NUMBER", nullable = false)
    public Integer getVisitNumber() {
        return this.visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "collectionEvent")
    @Cascade({ CascadeType.SAVE_UPDATE })
    public Set<Specimen> getAllSpecimenCollection() {
        return this.allSpecimenCollection;
    }

    public void setAllSpecimenCollection(
        Set<Specimen> allSpecimenCollection) {
        this.allSpecimenCollection = allSpecimenCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.CollectionEvent.patient.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.CollectionEvent.activityStatus.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @OneToMany(cascade = javax.persistence.CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "collectionEvent")
    @Cascade({ CascadeType.SAVE_UPDATE })
    public Set<EventAttr> getEventAttrCollection() {
        return this.eventAttrCollection;
    }

    public void setEventAttrCollection(Set<EventAttr> eventAttrCollection) {
        this.eventAttrCollection = eventAttrCollection;
    }

    @ManyToMany(cascade = javax.persistence.CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "COLLECTION_EVENT_COMMENT",
        joinColumns = { @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalCollectionEvent")
    public Set<Specimen> getOriginalSpecimenCollection() {
        return this.originalSpecimenCollection;
    }

    public void setOriginalSpecimenCollection(
        Set<Specimen> originalSpecimenCollection) {
        this.originalSpecimenCollection = originalSpecimenCollection;
    }
}
