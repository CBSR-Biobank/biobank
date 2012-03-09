package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class ManagedUser implements ActionResult {
    private static final long serialVersionUID = 1L;

    public Set<ManagedMembership> managedMemberships =
        new HashSet<ManagedMembership>(0);

    // ManagedGroup

    public ManagedUser(User user, Manager manager) {

        for (Membership m : user.getMemberships()) {
            if (m.isManageable(manager.getUser())) {
                ManagedMembership mm = new ManagedMembership(m, manager);
            }
        }

        for (Group group : user.getGroups()) {
            if (group.isFullyManageable(manager.getUser())) {
                for (Membership m : group.getMemberships()) {
                    ManagedMembership mm = new ManagedMembership(m, manager);
                }
            }
        }
    }
}
