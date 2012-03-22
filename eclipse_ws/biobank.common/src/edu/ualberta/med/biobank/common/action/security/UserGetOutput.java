package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.model.User;

public class UserGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;
    
    public final User user;
    public final ManagerContext context;

    public UserGetOutput(User user, ManagerContext context) {
        this.user = user;
        this.context = context;
    }

    public User getUser() {
        return user;
    }

    public ManagerContext getContext() {
        return context;
    }
}
