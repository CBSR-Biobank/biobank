package edu.ualberta.med.biobank.validator.group;

/**
 * A validation group. {@link PrePersist} is defined as a super-interface of
 * {@link PreInsert} and {@link PreUpdate} so that validation on a
 * {@link PrePersist} is performed in any of its derived groups.
 * 
 * @author Jonathan Ferland
 * 
 */
public interface PrePersist {
}
