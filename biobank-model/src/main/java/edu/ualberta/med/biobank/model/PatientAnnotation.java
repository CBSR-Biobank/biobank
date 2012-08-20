package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "PATIENT_ANNOTATION")
public class PatientAnnotation
    extends Annotation {
    private static final long serialVersionUID = 1L;

    private Patient patient;

    @NotNull(message = "{PatientAnnotation.patient.NotNull}")
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
