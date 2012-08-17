package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A standardised set of classifications that describe <em>what</em> a
 * {@link Specimen} is. Potential examples include: urine, whole blood, plasma,
 * nail, protein, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_TYPE")
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "specimenType", groups = PreDelete.class)
})
public class SpecimenType extends AbstractUniquelyNamedModel {
    private static final long serialVersionUID = 1L;
}
