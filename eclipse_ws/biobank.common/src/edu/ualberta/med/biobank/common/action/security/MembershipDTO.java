package edu.ualberta.med.biobank.common.action.security;

import java.util.Collections;
import java.util.Set;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * It is necessary to transfer which {@link PermissionEnum}-s are manageable
 * since this could change when the data is resubmitted. Note that these values
 * should still be checked when re-submitted. For example, if a {@link User} has
 * a form to remove a {@link PermissionEnum} open, then gains power before
 * submitting, the new {@link PermissionEnum} the manager has access to manage
 * (modify) will now be blanked.
 * <p>
 * But what if the center or study are modified??? The important thing is that
 * 
 * @author Jonathan Ferland
 */
public class MembershipDTO {
    private final Set<Role> roleOptions = null;
    private final Set<PermissionEnum> permissionOptions;

    private Center center;
    private Study study;

    public MembershipDTO(Membership m, User requestingUser) {
        this.permissionOptions = Collections.unmodifiableSet(m
            .getManageablePermissions(requestingUser));
    }

    public Set<PermissionEnum> getManageablePermissions() {
        return permissionOptions;
    }

    public Set<Role> getManageableRoles() {
        return roleOptions;
    }
}
