package edu.ualberta.med.biobank.model.context;

import edu.ualberta.med.biobank.model.security.User;

public class ExecutingUserImpl
    implements ExecutingUser {
    // TODO: consider replacing this with a Spring "Request" scoped component
    private static final ThreadLocal<User> user = new ThreadLocal<User>();

    @Override
    public User get() {
        return user.get();
    }

    @Override
    public void set(User user) {
        ExecutingUserImpl.user.set(user);
    }
}
