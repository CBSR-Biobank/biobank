package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Note: since this application will be used for inventory control of non human
 * participants, this class should be renamed to Participant.
 * 
 * caTissue Term - Participant: An individual from which a biospecimen is
 * collected.
 * 
 * NCI Term - Patient: A person who receives medical attention, care, or
 * treatment, or who is registered with medical professional or institution with
 * the purpose to receive medical care when necessary.
 * 
 */
@Audited
@Entity
@Table(name = "PATIENT")
@Unique(properties = "pnumber", groups = PrePersist.class)
@NotUsed(by = Specimen.class, property = "collectionEvent.patient", groups = PreDelete.class)
@Empty(property = "collectionEvents", groups = PreDelete.class)
public class Patient extends AbstractVersionedModel
    implements HasCreatedAt, HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Patient",
        "Patients");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
        public static final LString PNUMBER = bundle.trc(
            "model",
            "Patient Number").format();
    }

    private String pnumber;
    private Date createdAt;
    private Set<CollectionEvent> collectionEvents =
        new HashSet<CollectionEvent>(0);
    private Study study;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Patient.pnumber.NotEmpty}")
    @Column(name = "PNUMBER", unique = true, nullable = false, length = 100)
    public String getPnumber() {
        return this.pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Patient.createdAt.NotNull}")
    @Column(name = "CREATED_AT")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patient")
    public Set<CollectionEvent> getCollectionEvents() {
        return this.collectionEvents;
    }

    public void setCollectionEvents(Set<CollectionEvent> collectionEvents) {
        this.collectionEvents = collectionEvents;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Patient.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "PATIENT_COMMENT",
        joinColumns = { @JoinColumn(name = "PATIENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
