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
 * A {@link Patient} may act as an alias for at most one other {@link Patient}
 * per {@link Study}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "PATIENT_ALIAS")
public class PatientAlias {
    private PatientAliasId id;
    private Patient patient;
    private Patient alias;

    @EmbeddedId
    public PatientAliasId getId() {
        return id;
    }

    public void setId(PatientAliasId id) {
        this.id = id;
    }

    @MapsId("patientId")
    @NotNull(message = "{PatientAlias.patient.NotNull}")
    @ManyToOne
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @MapsId("aliasId")
    @NotNull(message = "{PatientAlias.alias.NotNull}")
    @ManyToOne
    @JoinColumn(name = "ALIAS_PATIENT_ID", nullable = false)
    public Patient getAlias() {
        return alias;
    }

    public void setAlias(Patient alias) {
        this.alias = alias;
    }

    @Embeddable
    public static class PatientAliasId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer patientId;
        private Integer aliasId;

        public Integer getPatientId() {
            return patientId;
        }

        public void setPatientId(Integer patientId) {
            this.patientId = patientId;
        }

        public Integer getAliasId() {
            return aliasId;
        }

        public void setAliasId(Integer aliasId) {
            this.aliasId = aliasId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((aliasId == null) ? 0 : aliasId.hashCode());
            result = prime * result
                + ((patientId == null) ? 0 : patientId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PatientAliasId other = (PatientAliasId) obj;
            if (aliasId == null) {
                if (other.aliasId != null) return false;
            } else if (!aliasId.equals(other.aliasId)) return false;
            if (patientId == null) {
                if (other.patientId != null) return false;
            } else if (!patientId.equals(other.patientId)) return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((patient == null) ? 0 : patient.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PatientAlias other = (PatientAlias) obj;
        if (alias == null) {
            if (other.alias != null) return false;
        } else if (!alias.equals(other.alias)) return false;
        if (patient == null) {
            if (other.patient != null) return false;
        } else if (!patient.equals(other.patient)) return false;
        return true;
    }
}
