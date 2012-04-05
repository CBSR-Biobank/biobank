package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;

public class UserSaveInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer userId;
    private final String login;
    private final String password; // TODO: get this, use hash?
    private final boolean recvBulkEmails;
    private final String fullName;
    private final String email;
    private final boolean needPwdChange;
    private final Set<Membership> memberships;
    private final Set<Integer> groupIds;
    private final MembershipContext context;

    @SuppressWarnings("nls")
    public UserSaveInput(User user, MembershipContext context, String password) {
        if (user == null)
            throw new IllegalArgumentException("null user");
        if (context == null)
            throw new IllegalArgumentException("null context");

        this.userId = user.getId();

        this.login = user.getLogin();
        this.recvBulkEmails = user.getRecvBulkEmails();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.needPwdChange = user.getNeedPwdChange();

        this.memberships = new HashSet<Membership>(user.getMemberships());
        this.groupIds = new HashSet<Integer>(IdUtil.getIds(user.getGroups()));

        this.context = context;

        this.password = password;
    }

    public UserSaveInput(User user, MembershipContext context) {
        this(user, context, null);
    }

    public Integer getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRecvBulkEmails() {
        return recvBulkEmails;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isNeedPwdChange() {
        return needPwdChange;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public MembershipContext getContext() {
        return context;
    }
}
