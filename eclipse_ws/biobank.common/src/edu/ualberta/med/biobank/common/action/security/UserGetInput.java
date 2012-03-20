package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.model.User;

public class UserGetInput {
    private final Integer userId;
    private final ManagerContext context;

    public UserGetInput(User user, ManagerContext context) {
        this.userId = user.getId();
        this.context = context;
    }

    public Integer getUserId() {
        return userId;
    }

    public ManagerContext getContext() {
        return context;
    }
}
