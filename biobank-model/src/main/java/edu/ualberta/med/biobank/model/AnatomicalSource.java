package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A standardised set of regions in a {@link Patient} <em>where</em> a
 * {@link Specimen} was collected from. Potential examples include: colon, ear,
 * leg, kidney, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "ANOTOMICAL_SOURCE")
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "anotomicalSource", groups = PreDelete.class)
})
public class AnatomicalSource extends AbstractUniquelyNamedModel {
    private static final long serialVersionUID = 1L;
}
