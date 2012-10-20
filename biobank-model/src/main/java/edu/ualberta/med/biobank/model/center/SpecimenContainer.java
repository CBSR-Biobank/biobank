package edu.ualberta.med.biobank.model.center;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.study.Specimen;

/**
 * Represents a single physical {@link Container} that directly holds one or
 * more {@link Specimen}s.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@DiscriminatorValue("SP")
public class SpecimenContainer
    extends Container<SpecimenContainerType> {
    private static final long serialVersionUID = 1L;
}
