package edu.ualberta.med.biobank.model.context;

import edu.ualberta.med.biobank.model.User;

/**
 * Holds the {@link User} that is considered logged in or as the executor of the
 * current commands.
 * 
 * @author Jonathan Ferland
 */
public interface ExecutingUser {
    public User get();

    public void set(User user);
}
