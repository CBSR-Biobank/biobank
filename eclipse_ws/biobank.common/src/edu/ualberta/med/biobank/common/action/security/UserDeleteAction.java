package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.User;

public class UserDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer userId;

    public UserDeleteAction(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("null user id");
        }

        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId);
        
        if (!user.isFullyManageable(context.getUser())) {
            throw new ActionException("insufficient power");
        }
        
        context.getSession().delete(user);
        return new EmptyResult();
    }
}
