package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Patient extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String pnumber;
    private Date createdAt;
    private Collection<CollectionEvent> collectionEventCollection =
        new HashSet<CollectionEvent>();
    private Study study;
    private Collection<Comment> commentCollection = new HashSet<Comment>();

    @NotEmpty
    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Collection<CollectionEvent> getCollectionEventCollection() {
        return collectionEventCollection;
    }

    public void setCollectionEventCollection(
        Collection<CollectionEvent> collectionEventCollection) {
        this.collectionEventCollection = collectionEventCollection;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
