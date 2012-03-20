package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

/**
 * Return a {@link User} only with the {@link Membership}-s, {@link Group}-s,
 * {@link Role}-s, and {@link PermissionEnum}-s that the executing {@link User}
 * is able to manage.
 * 
 * @author Jonathan Ferland
 */
public class UserGetAction implements Action<UserGetResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagementPermission();

    private Integer userId;

    public UserGetAction(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public UserGetResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId);

        return new UserGetResult(user);
    }

    @SuppressWarnings("unused")
    public static class UserDTO {
        private String login;
        private boolean recvBulkEmails = true;
        private String fullName;
        private String email;
        private boolean needPwdChange = true;

        private WorkingSet<Group> groups;
        private WorkingSet<Membership> memberships;

        public UserDTO(User user) {
            // go through Membership-s, find if any PermissionEnum-s in it, then
            // add it to the collection if so.
            // for (Membership membership : user.getMemberships()) {
            //
            // }
        }

        public static class MembershipDTO {
            private WorkingSet<Role> roles;
            private WorkingSet<PermissionEnum> permissions;
        }
    }
}
