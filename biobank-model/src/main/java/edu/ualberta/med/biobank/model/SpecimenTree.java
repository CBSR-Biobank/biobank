package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * Represents a group of {@link Specimen}-s that all descend from a particular
 * origin {@link Specimen}, which may or may not be known.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_TREE")
public class SpecimenTree extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Specimen originSpecimen;

    /**
     * @return the origin (i.e. root) {@link Specimen} of the tree, or null if
     *         not known. This can be unknown if the origin {@link Specimen} was
     *         thrown away and/or only its children are being imported to the
     *         system.
     */
    @Column(name = "ORIGIN_SPECIMEN_ID", unique = true)
    public Specimen getOriginSpecimen() {
        return originSpecimen;
    }

    public void setOriginSpecimen(Specimen originSpecimen) {
        this.originSpecimen = originSpecimen;
    }

    // TODO: consider this: like each specimenTree has possible parents.
    public Set<Patient> getPatients() {
        return null;
    }
}
