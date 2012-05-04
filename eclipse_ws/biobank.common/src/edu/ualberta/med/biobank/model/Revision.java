package edu.ualberta.med.biobank.model;

import java.util.Date;
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

import edu.ualberta.med.biobank.model.util.RevisionListenerImpl;

@RevisionEntity(RevisionListenerImpl.class)
@Entity
@Table(name = "REVISION")
public class Revision implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Date timestamp;
    private User user;
    private Set<RevisedEntity> revisedEntities = new HashSet<RevisedEntity>(0);

    @Override
    @RevisionNumber
    @GenericGenerator(name = "generator", strategy = "increment")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @RevisionTimestamp
    @Column(name = "TIMESTAMP")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", unique = true, nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL)
    public Set<RevisedEntity> getRevisedEntities() {
        return revisedEntities;
    }

    public void setRevisedEntities(Set<RevisedEntity> revisedEntities) {
        this.revisedEntities = revisedEntities;
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