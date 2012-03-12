package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Hibernate;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.util.WorkingSet;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class ManagedUser implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final Set<ManagedMembership> memberships;
    private final WorkingSet<Group> groups;

    public ManagedUser(User user, User manager, Set<Role> allRoles) {
        Set<ManagedMembership> mms = new HashSet<ManagedMembership>();
        ManagedMembership mm;
        for (Membership m : user.getMemberships()) {
            if (m.isManageable(manager)) {
                mm = new ManagedMembership(m, manager, allRoles);
                mms.add(mm);
            }
        }
        memberships = new HashSet<ManagedMembership>(mms);

        Set<Group> gs = new HashSet<Group>();
        for (Group group : user.getGroups()) {
            if (group.isFullyManageable(manager)) {
                Hibernate.initialize(group);
                gs.add(group);
            }
        }
        groups = new WorkingSet<Group>(gs);
    }

    public Set<ManagedMembership> getMemberships() {
        return memberships;
    }

    public WorkingSet<Group> getGroups() {
        return groups;
    }

    public class Properties {
        private final boolean isManageable = false;

        public Properties() {

        }
    }
}
