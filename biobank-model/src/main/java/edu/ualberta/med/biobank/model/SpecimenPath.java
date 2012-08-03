package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Represents the transitive closure of the {@link SpecimenLink} objects.
 * 
 * @author Jonathan Ferland
 * @see http://www.slideshare.net/billkarwin/models-for-hierarchical-data
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PATH")
public class SpecimenPath implements Serializable {
    private static final long serialVersionUID = 1L;

    private PathId id;
    private Specimen ancestor;
    private Specimen descendant;
    private Integer length;

    @EmbeddedId
    public PathId getId() {
        return id;
    }

    public void setId(PathId id) {
        this.id = id;
    }

    @MapsId("ancestorId")
    @NotNull(message = "{SpecimenPath.ancestor.NotNull}")
    @ManyToOne
    @JoinColumn(name = "ANCESTOR_ID", nullable = false)
    public Specimen getAncestor() {
        return ancestor;
    }

    public void setAncestor(Specimen ancestor) {
        this.ancestor = ancestor;
    }

    @MapsId("descendantId")
    @NotNull(message = "{SpecimenPath.descendant.NotNull}")
    @ManyToOne
    @JoinColumn(name = "DESCENDANT_ID", nullable = false)
    public Specimen getDescendant() {
        return descendant;
    }

    public void setDescendant(Specimen descendant) {
        this.descendant = descendant;
    }

    @NotNull(message = "{SpecimenPath.length.NotNull}")
    @Min(value = 0, message = "{SpecimenPath.length.Min}")
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Embeddable
    public static class PathId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer ancestorId;
        private Integer descendantId;

        public Integer getAncestorId() {
            return ancestorId;
        }

        public void setAncestorId(Integer ancestorId) {
            this.ancestorId = ancestorId;
        }

        public Integer getDescendantId() {
            return descendantId;
        }

        public void setDescendantId(Integer descendantId) {
            this.descendantId = descendantId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((ancestorId == null) ? 0 : ancestorId.hashCode());
            result = prime * result
                + ((descendantId == null) ? 0 : descendantId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PathId other = (PathId) obj;
            if (ancestorId == null) {
                if (other.ancestorId != null) return false;
            } else if (!ancestorId.equals(other.ancestorId)) return false;
            if (descendantId == null) {
                if (other.descendantId != null) return false;
            } else if (!descendantId.equals(other.descendantId)) return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((ancestor == null) ? 0 : ancestor.hashCode());
        result =
            prime * result + ((descendant == null) ? 0 : descendant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SpecimenPath other = (SpecimenPath) obj;
        if (ancestor == null) {
            if (other.ancestor != null) return false;
        } else if (!ancestor.equals(other.ancestor)) return false;
        if (descendant == null) {
            if (other.descendant != null) return false;
        } else if (!descendant.equals(other.descendant)) return false;
        return true;
    }
}
