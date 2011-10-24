package edu.ualberta.med.biobank.common.permission;

import java.io.Serializable;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.User;

/**
 * Implementations of this interface should follow the template
 * "{noun}{verb}{noun}..Permission," for example, CreateSpecimenPermission NOT
 * SpecimenCreatePermission.
 * 
 * @author jferland
 * 
 * @param <T>
 */
public interface Permission extends Serializable {
    public boolean isAllowed(User user, Session session);
}
