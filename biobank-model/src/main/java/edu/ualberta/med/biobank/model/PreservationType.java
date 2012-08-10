package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A standardised set of methods for preserving and storing {@link Specimen}s.
 * Potential examples include: frozen specimen, RNA later, fresh specimen,
 * slide, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "PRESERVATION_TYPE")
@NotUsed.List({
    @NotUsed(by = Preservation.class, property = "type", groups = PreDelete.class)
})
public class PreservationType extends AbstractUniquelyNamedModel {
    private static final long serialVersionUID = 1L;
}
