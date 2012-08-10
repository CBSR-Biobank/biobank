package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Allows more than one {@link Study} to have information on the same
 * {@link Specimen}, in case ownership changes. {@link Patient} and
 * {@link CollectionEvent} relationships are defined through this entity.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "STUDY_SPECIMEN",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "STUDY_ID", "SPECIMEN_ID" })
    })
@Unique(properties = { "study", "specimen" }, groups = PrePersist.class)
public class StudySpecimen extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Specimen specimen;
    private Patient patient;
    private CollectionEvent collectionEvent;

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public Patient getPatient() {
        return (collectionEvent != null)
            ? collectionEvent.getPatient()
            : patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }
}
