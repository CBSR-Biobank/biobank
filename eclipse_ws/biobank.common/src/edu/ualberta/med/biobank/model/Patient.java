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

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "PATIENT")
public class Patient extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String pnumber;
    private Date createdAt;
    private Set<CollectionEvent> collectionEventCollection =
        new HashSet<CollectionEvent>(0);
    private Study study;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);

    @NotEmpty
    @Column(name = "PNUMBER", unique = true, nullable = false, length = 100)
    public String getPnumber() {
        return this.pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    @NotNull
    @Column(name = "CREATED_AT")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patient")
    public Set<CollectionEvent> getCollectionEventCollection() {
        return this.collectionEventCollection;
    }

    public void setCollectionEventCollection(
        Set<CollectionEvent> collectionEventCollection) {
        this.collectionEventCollection = collectionEventCollection;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "PATIENT_COMMENT",
        joinColumns = { @JoinColumn(name = "PATIENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
