package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.User;

public class UserDeleteInput implements ActionInput {
    private static final long serialVersionUID = 1L;

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
