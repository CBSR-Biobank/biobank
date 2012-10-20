package edu.ualberta.med.biobank.model.center;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.study.Specimen;

/**
 * Represents a type or classification of specimen containers that
 * <em>directly</em> hold {@link Specimen}s, such as, specific types of tubes
 * (e.g. NUNC 2ml, NUNC 5ml, etc.), vials, slides, well plates, etc..
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@DiscriminatorValue("SP")
public class SpecimenContainerType
    extends ContainerType {
    private static final long serialVersionUID = 1L;
}
