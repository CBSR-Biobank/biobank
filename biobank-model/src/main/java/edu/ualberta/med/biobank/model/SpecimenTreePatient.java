package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * This is a correlation entity that represents an optional relationship between
 * a {@link SpecimenTree} and a {@link Patient}. A {@link SpecimenTree} may also
 * be part of a {@link CollectionEvent}, but because a {@link CollectionEvent}
 * <em>must</em> have a {@link Patient}, the link comes through a
 * {@link SpecimenTreePatient} object with the same {@link Patient}.
 * 
 * <pre>
 * {@link SpecimenTree}
 *   ^ 1
 *   |
 *   |  0..*
 *   +------{@link SpecimenTreePatient}
 *             |    | 0..*
 *        0..* |    |
 *             |    |    1
 *             |    +-----> {@link Patient}
 *             |                ^ 1
 *             |                |
 *             +--------------> |
 *                        0..1  |
 *                              | 0..*
 *                          {@link CollectionEvent}
 * </pre>
 * 
 * This enforces the following possibilities:
 * <ul>
 * <li>A {@link CollectionEvent} can exist without a {@link SpecimenTree}.</li>
 * <li>A {@link SpecimenTree} can exist without a {@link CollectionEvent}.</li>
 * <li>A {@link SpecimenTree} can exist without a {@link Patient}.</li>
 * <li>A {@link Patient} can exist without a {@link SpecimenTree}.</li>
 * <li>A {@link Patient} can exist without a {@link CollectionEvent}.</li>
 * <li>A {@link CollectionEvent} can <em>not</em> exist without a
 * {@link Patient}.</li>
 * <li>A {@link SpecimenTree} can have multiple {@link Patient}-s, one for each
 * {@link Study} it has been involved with.</li>
 * </ul>
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_TREE__PATIENT")
public class SpecimenTreePatient implements Serializable {
    private static final long serialVersionUID = 1L;

    private SpecimenTreePatientId id;
    private SpecimenTree specimenTree;
    private Patient patient;
    private CollectionEvent collectionEvent;

    @EmbeddedId
    public SpecimenTreePatientId getId() {
        return id;
    }

    public void setId(SpecimenTreePatientId id) {
        this.id = id;
    }

    /**
     * @return a {@link SpecimenTree} of {@link #getPatient()}.
     */
    @MapsId("specimenTreeId")
    @NotNull(message = "{SpecimenTreePatient.specimenTree.NotNull}")
    @ManyToOne
    @JoinColumn(name = "SPECIMEN_TREE_ID", nullable = false)
    public SpecimenTree getSpecimenTree() {
        return specimenTree;
    }

    public void setSpecimenTree(SpecimenTree specimenTree) {
        this.specimenTree = specimenTree;
    }

    /**
     * @return a {@link Patient} of {@link #getSpecimenTree()}.
     */
    @MapsId("patientId")
    @NotNull(message = "{SpecimenTreePatient.patient.NotNull}")
    @ManyToOne
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    // TODO: put a two-column foreign key on {@link CollectionEvent} to ensure
    // that {@link SpecimenTreePatient#getPatient()} matches
    // {@link SpecimenTreePatient#getCollectionEvent()}.
    // {@link CollectionEvent#getPatient()}?
    @ManyToOne
    @JoinColumn(name = "COLLECTION_EVENT_ID")
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    @Embeddable
    public static class SpecimenTreePatientId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer specimenTreeId;
        private Integer patientId;

        public Integer getSpecimenTreeId() {
            return specimenTreeId;
        }

        public void setSpecimenTreeId(Integer specimenTreeId) {
            this.specimenTreeId = specimenTreeId;
        }

        public Integer getPatientId() {
            return patientId;
        }

        public void setPatientId(Integer patientId) {
            this.patientId = patientId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((patientId == null) ? 0 : patientId.hashCode());
            result = prime * result
                + ((specimenTreeId == null) ? 0 : specimenTreeId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SpecimenTreePatientId other = (SpecimenTreePatientId) obj;
            if (patientId == null) {
                if (other.patientId != null) return false;
            } else if (!patientId.equals(other.patientId)) return false;
            if (specimenTreeId == null) {
                if (other.specimenTreeId != null) return false;
            } else if (!specimenTreeId.equals(other.specimenTreeId))
                return false;
            return true;
        }
    }
}
