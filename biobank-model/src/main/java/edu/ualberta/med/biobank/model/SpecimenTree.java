package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * Represents a group of {@link Specimen}-s that all descend from a set of one
 * or more origin sister {@link Specimen}-s ({@link Specimen}-s with this
 * {@link SpecimenTree} that have a null {@link Specimen#getParentSpecimen()}).
 * This allows properties to be shared by all the descendants of one or more
 * {@link Specimen}, such as, {@link Patient}, {@link CollectionEvent}, source
 * tissue, etc..
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_TREE")
public class SpecimenTree extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;
}
