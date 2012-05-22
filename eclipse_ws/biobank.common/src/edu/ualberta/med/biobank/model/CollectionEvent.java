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
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "COLLECTION_EVENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PATIENT_ID", "VISIT_NUMBER" }) })
@Unique(properties = { "patient", "visitNumber" }, groups = PrePersist.class)
@Empty(property = "allSpecimens", groups = PreDelete.class)
public class CollectionEvent extends AbstractBiobankModel
    implements HasActivityStatus, HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Collection Event",
        "Collection Events");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
        public static final LString VISIT_NUMBER = bundle.trc(
            "model",
            "Visit Number").format();
        public static final LString WORKSHEET = bundle.trc(
            "model",
            "Worksheet").format();
    }

    private Integer visitNumber;
    private Set<Specimen> allSpecimens = new HashSet<Specimen>(0);
    private Patient patient;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<EventAttr> eventAttrs = new HashSet<EventAttr>(0);
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<Specimen> originalSpecimens = new HashSet<Specimen>(0);

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
    public Set<Specimen> getAllSpecimens() {
        return this.allSpecimens;
    }

    public void setAllSpecimens(Set<Specimen> allSpecimens) {
        this.allSpecimens = allSpecimens;
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

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.CollectionEvent.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @OneToMany(cascade = javax.persistence.CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "collectionEvent")
    @Cascade({ CascadeType.SAVE_UPDATE })
    public Set<EventAttr> getEventAttrs() {
        return this.eventAttrs;
    }

    public void setEventAttrs(Set<EventAttr> eventAttrs) {
        this.eventAttrs = eventAttrs;
    }

    @Override
    @ManyToMany(cascade = javax.persistence.CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "COLLECTION_EVENT_COMMENT",
        joinColumns = { @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalCollectionEvent")
    public Set<Specimen> getOriginalSpecimens() {
        return this.originalSpecimens;
    }

    public void setOriginalSpecimens(Set<Specimen> originalSpecimens) {
        this.originalSpecimens = originalSpecimens;
    }
}
