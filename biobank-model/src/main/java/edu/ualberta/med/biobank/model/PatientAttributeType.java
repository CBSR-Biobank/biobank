package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PATIENT_ATTRIBUTE_TYPE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "STUDY_ID", "LABEL" }) })
public class PatientAttributeType
    extends StudyAttributeType<PatientAttributeOption> {
    private static final long serialVersionUID = 1L;
}
