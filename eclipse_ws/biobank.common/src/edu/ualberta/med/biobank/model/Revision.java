package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@RevisionEntity(RevisionListenerImpl.class)
@Entity
@Table(name = "REVISION")
public class Revision
    implements Serializable, HasId<Long> {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long timestamp;
    private Long generatedAt;
    private Long committedAt;
    private User user;
    private Set<RevisionEntityType> entityTypes =
        new HashSet<RevisionEntityType>(0);

    @Override
    @RevisionNumber
    @Id
    @GeneratedValue(generator = "revision-number-generator")
    @GenericGenerator(name = "revision-number-generator",
        strategy = "edu.ualberta.med.biobank.model.id.RevisionNumberGenerator")
    @Column(name = "ID", nullable = false)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Milliseconds since epoch when this object is created by Envers.
     * 
     * @return
     */
    @RevisionTimestamp
    @Column(name = "REVISION_TIMESTAMP")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Milliseconds since epoch right after the {@link @RevisionNumber} was
     * generated.
     * 
     * @return
     */
    @Column(name = "GENERATED_AT")
    public Long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Long generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Milliseconds since epoch just before the transaction was committed.
     * 
     * @return
     */
    @Column(name = "COMMITTED_AT")
    public Long getCommittedAt() {
        return committedAt;
    }

    public void setCommittedAt(Long committedAt) {
        this.committedAt = committedAt;
    }

    /**
     * The {@link User} responsible for this {@link Revision}. Note that this
     * could be null, in cases such as, periodic server or maintenance actions.
     * 
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL)
    public Set<RevisionEntityType> getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(Set<RevisionEntityType> entityTypes) {
        this.entityTypes = entityTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Revision other = (Revision) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}