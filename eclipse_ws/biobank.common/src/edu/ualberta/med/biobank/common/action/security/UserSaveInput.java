package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;

public class UserSaveInput {
    private final Integer userId;
    private final String login;
    private final String password = null; // TODO: get this, use hash?
    private final boolean recvBulkEmails;
    private final String fullName;
    private final String email;
    private final boolean needPwdChange;
    private final Set<Membership> memberships;
    private final Set<Integer> groupIds;
    private final ManagerContext context;

    public UserSaveInput(User user, ManagerContext context) {
        this.userId = user.getId();

        this.login = user.getLogin();
        this.recvBulkEmails = user.getRecvBulkEmails();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.needPwdChange = user.getNeedPwdChange();

        this.memberships = new HashSet<Membership>(user.getMemberships());
        this.groupIds = new HashSet<Integer>(IdUtil.getIds(user.getGroups()));

        this.context = context;
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

    public ManagerContext getContext() {
        return context;
    }
}
