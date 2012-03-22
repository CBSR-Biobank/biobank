package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.model.User;

public class UserDeleteInput {
    private final Integer userId;

    public UserDeleteInput(User user) {
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
