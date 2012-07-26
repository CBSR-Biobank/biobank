package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "PATIENT_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PATIENT_ID", "LABEL" }) })
@Unique(properties = { "patient", "attributeType" }, groups = PreDelete.class)
public class PatientAttribute
    extends StudyAttribute<PatientAttributeType, PatientAttributeOption> {
    private static final long serialVersionUID = 1L;

    private Patient patient;

    @NotNull(message = "{PatientAttribute.patient.NotNull}")
    @Column(name = "PATIENT_ID")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
