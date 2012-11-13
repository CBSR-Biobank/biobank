package edu.ualberta.med.biobank.model.study;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Comment;

@Entity
@Table(name = "PATIENT_COMMENT")
public class PatientComment
    extends Comment<Patient> {
    private static final long serialVersionUID = 1L;

    private Patient patient;

    @NotNull(message = "{PatientComment.patient.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    @Transient
    public Patient getOwner() {
        return getPatient();
    }
}
