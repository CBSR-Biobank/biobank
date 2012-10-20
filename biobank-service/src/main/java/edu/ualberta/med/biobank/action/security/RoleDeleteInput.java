package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.security.Role;

public class RoleDeleteInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer roleId;

    @SuppressWarnings("nls")
    public RoleDeleteInput(Role role) {
        if (role == null)
            throw new IllegalArgumentException("null role");
        if (role.getId() == null)
            throw new IllegalArgumentException("null role id");

        this.roleId = role.getId();
    }

    public Integer getRoleId() {
        return roleId;
    }
}
