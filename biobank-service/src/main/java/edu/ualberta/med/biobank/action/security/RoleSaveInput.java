package edu.ualberta.med.biobank.action.security;

import java.util.Collections;
import java.util.Set;

import edu.ualberta.med.biobank.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class RoleSaveInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer roleId;
    private final String name;
    private final Set<PermissionEnum> permissions;

    @SuppressWarnings("nls")
    public RoleSaveInput(Role role) {
        if (role == null)
            throw new IllegalArgumentException("null role");

        this.roleId = role.getId();
        this.name = role.getName();
        this.permissions = Collections.unmodifiableSet(role.getPermissions());
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public Set<PermissionEnum> getPermissions() {
        return permissions;
    }
}
