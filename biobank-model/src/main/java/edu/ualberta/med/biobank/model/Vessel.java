package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A standardised set of the types of containers that {@link Specimen}s are
 * <em>directly</em> stored in, such as, specific types of tubes, vials, slides,
 * etc..
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "VESSEL")
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "vessel", groups = PreDelete.class)
})
public class Vessel extends AbstractUniquelyNamedModel {
    private static final long serialVersionUID = 1L;
}
