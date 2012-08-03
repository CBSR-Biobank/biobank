package edu.ualberta.med.biobank.model;

import java.util.Set;

/**
 * Represents {@link Specimen}s that came <em>directly</em> from a
 * {@link Patient}.
 * 
 * @author Jonathan Ferland
 */
public class SpecimenPatient {
    private Set<Specimen> specimens;
}
