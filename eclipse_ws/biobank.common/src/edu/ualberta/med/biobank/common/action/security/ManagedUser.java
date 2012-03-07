package edu.ualberta.med.biobank.common.action.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class ManagedUser implements Serializable {
    private static final long serialVersionUID = 1L;

    public Set<ManagedMembership> managedMemberships =
        new HashSet<ManagedMembership>(0);

    // ManagedGroup

    public ManagedUser(User user, User manager, Set<Role> allRoles) {

        for (Membership m : user.getMemberships()) {
            if (m.isManageable(manager)) {
                ManagedMembership managedMembership =
                    new ManagedMembership(m, manager, allRoles);
            }
        }
    }
}
