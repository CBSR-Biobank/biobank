package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;

public class UserSaveData {
    private final Integer userId;
    private final String login;
    private final String password = null; // TODO: get this, use hash?
    private final boolean recvBulkEmails;
    private final String fullName;
    private final String email;
    private final boolean needPwdChange;
    private final Set<Membership> memberships;
    private final Set<Integer> groupIds;
    private final Context context;

    public UserSaveData(User user, Context context) {
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

    public Context getContext() {
        return context;
    }

    public static class Context {
        private final User manager;
        private final Set<Integer> roleIds;
        private final Set<Integer> groupIds;

        /**
         * A snapshot of the managing {@link User} needs to be included because
         * it defines the context of the information <em>intended</em> to be
         * saved. In case there were changes to the manager's permissions in the
         * mean time, they might otherwise save things they don't intend to
         * (e.g. if they became more powerful).
         * <p>
         * Similarly, the modifiable {@link Role}-s and {@link Group}-s need to
         * be sent in case some where added or removed since this action was
         * generated. So, these are the sets the manager is aware of at this
         * point.
         * 
         * @param manager the {@link User} that is executing the save
         * @param roles every {@link Role} that <em>can</em> be modified, that
         *            the manager is aware of at this point
         * @param groups every manageable {@link Group} that <em>can</em> be
         *            modified, that the manager is aware of at this point
         */
        public Context(User manager, Set<Role> roles, Set<Group> groups) {
            this.manager = manager;
            this.roleIds = IdUtil.getIds(roles);
            this.groupIds = IdUtil.getIds(groups);
        }

        public User getManager() {
            return manager;
        }

        public Set<Integer> getRoleIds() {
            return roleIds;
        }

        public Set<Integer> getGroupIds() {
            return groupIds;
        }
    }
}
