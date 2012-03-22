package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.model.User;

public class UserGetInput {
    private final Integer userId;

    public UserGetInput(User user, ManagerContext context) {
        if (user == null)
            throw new IllegalArgumentException("null user");
        if (user.getId() == null)
            throw new IllegalArgumentException("null user id");
        
        this.userId = user.getId();
    }

    public Integer getUserId() {
        return userId;
    }
}
