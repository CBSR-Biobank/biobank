package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class UserGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final User user;
    private final MembershipContext context;
    private final boolean isFullyManageable;

    public UserGetOutput(User user, MembershipContext context,
        boolean isFullyManageable) {
        this.user = user;
        this.context = context;
        this.isFullyManageable = isFullyManageable;
    }

    public User getUser() {
        return user;
    }

    public MembershipContext getContext() {
        return context;
    }

    /**
     * Because the {@link Membership}-s and {@link Group}-s of the returned user
     * have been restricted to those this {@link MembershipContext} can see, he
     * will think he's able to fully manage the {@link User} (via
     * {@link User#isFullyManageable(User)}. This indicates whether he
     * <em>truely</em> can.
     * 
     * @return
     */
    public boolean isFullyManageable() {
        return isFullyManageable;
    }
}
