package edu.ualberta.med.biobank.action.security;

import java.util.SortedSet;

import edu.ualberta.med.biobank.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.model.Role;

public class RoleGetAllOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;
    private final SortedSet<Role> allRoles;

    public RoleGetAllOutput(SortedSet<Role> allRoles) {
        this.allRoles = allRoles;
    }

    public SortedSet<Role> getAllRoles() {
        return allRoles;
    }
}
