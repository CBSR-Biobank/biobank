package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "REVISION_ENTITY_TYPE")
public class RevisionEntityType implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String type;
    private Revision revision;

    @Override
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

    @Column(name = "TYPE", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVISION_ID", nullable = false)
    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RevisionEntityType other = (RevisionEntityType) obj;
        if (revision == null) {
            if (other.revision != null) return false;
        } else if (!revision.equals(other.revision)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }
}
