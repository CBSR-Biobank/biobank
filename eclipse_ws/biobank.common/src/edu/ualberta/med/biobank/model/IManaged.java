package edu.ualberta.med.biobank.model;

/**
 * Interface for user security model objects that can be managed by {@link User}
 * -s.
 * 
 * @author Jonathan Ferland
 */
public interface IManaged {
    /**
     * Determines whether the given {@link User} can manage the object that
     * implements this interface. That is,
     * 
     * @param user
     * @return
     */
    public boolean isManageable(User user);
}
