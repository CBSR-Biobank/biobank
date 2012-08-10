package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A shared named unit (e.g. mL, g, m) for qualifying an amount of something.
 * <p>
 * Note that no two units should be able to be converted to each other. That is,
 * there should only be one unit defined for each measurable property, such as,
 * length, area, volume, mass, etc.. For example, there should <em>not</em> be a
 * 'mL' unit and a 'L' unit since that would require searching for an amount of
 * 1000 in mL or an amount of 1 in L.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "UNIT")
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "amount.unit", groups = PreDelete.class),
})
public class Unit extends AbstractUniquelyNamedModel {
    private static final long serialVersionUID = 1L;
}
