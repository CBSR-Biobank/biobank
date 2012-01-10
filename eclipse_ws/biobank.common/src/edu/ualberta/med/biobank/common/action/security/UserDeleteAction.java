package edu.ualberta.med.biobank.common.action.security;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.User;

public class UserDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer userToDeleteId;

    public UserDeleteAction(Integer id) {
        this.userToDeleteId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new UserManagementPermission().isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        User userToDelete =
            new ActionContext(user, session).load(User.class, userToDeleteId);
        session.delete(userToDelete);
        return new EmptyResult();
    }

}
