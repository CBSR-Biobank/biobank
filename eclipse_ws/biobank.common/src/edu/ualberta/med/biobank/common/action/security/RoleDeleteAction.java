package edu.ualberta.med.biobank.common.action.security;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class RoleDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer roleId;

    public RoleDeleteAction(Integer id) {
        this.roleId = id;

    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new UserManagementPermission().isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Role role = new ActionContext(user, session).load(Role.class, roleId);
        session.delete(role);
        return new EmptyResult();
    }

}
