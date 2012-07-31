package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "SPECIMEN_TREE__PATIENT")
public class SpecimenTreePatient implements Serializable {
    private static final long serialVersionUID = 1L;

    // TODO: map ids and implement equals and hashCode()
    // TODO: or just have SpecimenTree.getPatients() ?? because small number,
    // probably
    private SpecimenTree specimenTree;
    private Patient patient;

    @Column(name = "SPECIMEN_TREE_ID")
    public SpecimenTree getSpecimenTree() {
        return specimenTree;
    }

    public void setSpecimenTree(SpecimenTree specimenTree) {
        this.specimenTree = specimenTree;
    }

    @Column(name = "PATIENT_ID")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
