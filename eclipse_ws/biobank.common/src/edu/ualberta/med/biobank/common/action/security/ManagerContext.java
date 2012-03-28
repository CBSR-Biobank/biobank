package edu.ualberta.med.biobank.common.action.security;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class ManagerContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private final User manager;
    private final List<Role> roles;
    private final List<Group> groups;
    private final List<User> users;
    private final List<Center> centers;
    private final List<Study> studies;

    private final boolean roleManager;

    public ManagerContext(User manager, List<Role> roles, List<Group> groups,
        List<User> users, List<Center> centers, List<Study> studies,
        boolean roleManager) {
        this.manager = manager;

        this.roles = roles;
        this.groups = groups;
        this.users = users;
        this.centers = centers;
        this.studies = studies;

        this.roleManager = roleManager;
    }

    public User getManager() {
        return manager;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Center> getCenters() {
        return centers;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public boolean isRoleManager() {
        return roleManager;
    }
}
