package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.User;

public class UserDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer userToDeleteId;

    public UserDeleteAction(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        this.userToDeleteId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        User userToDelete = context.load(User.class, userToDeleteId);
        context.getSession().delete(userToDelete);
        return new EmptyResult();
    }

}
