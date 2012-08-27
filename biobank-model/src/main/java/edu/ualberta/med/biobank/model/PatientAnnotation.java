package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@DiscriminatorValue("PA")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "PATIENT_ID",
        "ANNOTATION_TYPE_ID" })
})
@Unique(properties = { "patient", "type" }, groups = PrePersist.class)
public class PatientAnnotation
    extends Annotation {
    private static final long serialVersionUID = 1L;

    private Patient patient;

    @NotNull(message = "{PatientAnnotation.patient.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
