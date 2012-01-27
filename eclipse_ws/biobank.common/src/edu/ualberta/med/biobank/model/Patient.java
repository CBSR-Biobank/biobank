package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Collection;
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

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "PATIENT")
public class Patient extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String pnumber;
    private Date createdAt;
    private Collection<CollectionEvent> collectionEventCollection =
        new HashSet<CollectionEvent>(0);
    private Study study;
    private Collection<Comment> commentCollection = new HashSet<Comment>(0);

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
    public Collection<CollectionEvent> getCollectionEventCollection() {
        return this.collectionEventCollection;
    }

    public void setCollectionEventCollection(
        Collection<CollectionEvent> collectionEventCollection) {
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
    public Collection<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
