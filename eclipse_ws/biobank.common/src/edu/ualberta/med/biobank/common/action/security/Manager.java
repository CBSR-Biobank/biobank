package edu.ualberta.med.biobank.common.action.security;

import java.util.Collections;
import java.util.Set;

import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class Manager {
    private final User user;
    private final Set<Role> allRoles;

    private Manager(User user, Set<Role> allRoles) {
        this.user = user;
        this.allRoles = Collections.unmodifiableSet(allRoles);
    }

    public User getUser() {
        return user;
    }

    public Set<Role> getAllRoles() {
        return allRoles;
    }

    // static Manager allRoles(User user) {
    //
    //
    // // return new Manager(user, allRoles);
    // }
}
