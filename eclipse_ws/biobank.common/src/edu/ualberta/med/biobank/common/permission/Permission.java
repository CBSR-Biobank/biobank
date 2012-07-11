package edu.ualberta.med.biobank.common.permission;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.ActionContext;

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
    public boolean isAllowed(ActionContext context);
}
