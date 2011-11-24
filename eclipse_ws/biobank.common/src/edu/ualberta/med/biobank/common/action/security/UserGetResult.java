package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.User;

public class UserGetResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final User user;

    public UserGetResult(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
