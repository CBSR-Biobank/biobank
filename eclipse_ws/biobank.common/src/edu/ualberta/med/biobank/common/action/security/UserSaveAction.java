package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Synchronization;

import org.hibernate.Transaction;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import edu.ualberta.med.biobank.util.NullHelper;
import edu.ualberta.med.biobank.util.SetDiff;
import edu.ualberta.med.biobank.util.SetDiff.Pair;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final UserSaveInput input;

    public UserSaveAction(UserSaveInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, input.getUserId());

        createCsmUser(context, user);

        setProperties(context, user);
        setMemberships(context, user);
        setGroups(context, user);

        context.getSession().saveOrUpdate(user);

        return new IdResult(user.getId());
    }

    private void createCsmUser(ActionContext context, User user) {
        if (user.isNew()) return;
        if (user.getCsmUserId() != null) return;
        try {
            String password = input.getPassword();
            Long csmUserId = BiobankCSMSecurityUtil.persistUser(user, password);
            user.setCsmUserId(csmUserId);

            Transaction tx = context.getSession().getTransaction();
            tx.registerSynchronization(new DeleteCsmUserOnRollback(user));
        } catch (ApplicationException e) {
            throw new ActionException(e);
        }
    }

    private void setProperties(ActionContext context, User user) {
        User executingUser = context.getUser();
        // whether the manager _could_ have modified the user properties
        if (user.isFullyManageable(input.getContext().getManager())
            // whether the manager (executing user) _still_ can
            && user.isFullyManageable(executingUser)) {

            user.setLogin(input.getLogin());
            user.setEmail(input.getEmail());
            user.setFullName(input.getFullName());
            user.setNeedPwdChange(input.isNeedPwdChange());
            user.setRecvBulkEmails(input.isRecvBulkEmails());

            String newPw = input.getPassword();
            if (!user.isNew() && newPw != null) {
                try {
                    String login = user.getLogin();
                    String pw = BiobankCSMSecurityUtil.getUserPassword(login);
                    if (!pw.equals(newPw)) {
                        Long csmUserId = user.getCsmUserId();
                        BiobankCSMSecurityUtil
                            .modifyPassword(csmUserId, pw, newPw);

                        Transaction tx = context.getSession().getTransaction();
                        tx.registerSynchronization(
                            new RevertPasswordOnRollback(csmUserId, pw, newPw));
                    }
                } catch (ApplicationException e) {
                    throw new ActionException(e);
                }
            }
        }
    }

    private void setMemberships(ActionContext context, User user) {
        SetDiff<MembershipDomain> diff = diffMemberships(user);

        for (MembershipDomain domain : diff.getAdditions()) {
            Membership membership = domain.getMembership();
            checkFullyManageable(context, membership);

            user.getMemberships().add(membership);
            membership.setPrincipal(user);
        }

        for (MembershipDomain domain : diff.getRemovals()) {
            Membership membership = domain.getMembership();
            checkFullyManageable(context, membership);

            user.getMemberships().remove(membership);
            membership.setPrincipal(null);
        }

        mergeMemberships(context, diff.getIntersection());

        for (Membership m : user.getMemberships()) {
            m.reducePermissions();
        }
    }

    private void mergeMemberships(ActionContext context,
        Set<Pair<MembershipDomain>> conflicts) {
        User executingUser = context.getUser();
        User manager = input.getContext().getManager();

        // TODO: reload these
        Set<Role> contextRoles = input.getContext().getRoles();

        Set<PermissionEnum> perms, oldPermissionScope, newPermissionScope;
        Set<Role> roles, oldRoleScope, newRoleScope;
        for (Pair<MembershipDomain> conflict : conflicts) {
            Membership oldM = conflict.getOld().getMembership();
            Membership newM = conflict.getNew().getMembership();

            // the permissions and roles the user would have intended to modify
            oldPermissionScope = oldM.getManageablePermissions(manager);
            oldRoleScope = oldM.getManageableRoles(manager, contextRoles);

            // the permissions and roles the executing user can currently modify
            newPermissionScope = newM.getManageablePermissions(executingUser);
            newRoleScope = newM.getManageableRoles(executingUser, contextRoles);

            // ensure the old (client?) scope can still be modified by the new
            // (server?) scope
            if (!newPermissionScope.containsAll(oldPermissionScope)
                || newRoleScope.containsAll(oldRoleScope)) {
                // TODO: better exception
                throw new ActionException("reduced scope");
            }

            // looks okay, clear out the old scope and assign intended values
            oldM.getPermissions().removeAll(oldPermissionScope);
            oldM.getRoles().removeAll(oldRoleScope);

            perms = new HashSet<PermissionEnum>();
            perms.addAll(newM.getPermissions());
            perms.retainAll(oldPermissionScope);
            oldM.getPermissions().addAll(perms);

            roles = new HashSet<Role>();
            roles.addAll(newM.getRoles());
            roles.retainAll(newM.getRoles());
            oldM.getRoles().addAll(roles);
        }
    }

    private void checkFullyManageable(ActionContext context, Membership m) {
        User executingUser = context.getUser();
        if (!m.isFullyManageable(executingUser)) {
            // TODO: better exception
            throw new ActionException("");
        }
    }

    private SetDiff<MembershipDomain> diffMemberships(User user) {
        final Set<MembershipDomain> oldDomains, newDomains;
        oldDomains = MembershipDomain.from(input.getMemberships());
        newDomains = MembershipDomain.from(user.getMemberships());
        return new SetDiff<MembershipDomain>(oldDomains, newDomains);
    }

    private void setGroups(ActionContext context, User user) {
        User executingUser = context.getUser();

        // TODO: reload these
        Set<Group> contextGroups = input.getContext().getGroups();
        Set<Integer> contextGroupIds = IdUtil.getIds(contextGroups);

        if (!contextGroups.containsAll(input.getGroupIds())) {
            // TODO: better exception
            throw new ActionException("found groups out of context");
        }

        // add or remove every Group in the context
        Set<Group> groups = context.load(Group.class, contextGroupIds);
        for (Group group : groups) {
            if (!group.isFullyManageable(executingUser)) {
                // TODO: throw exception
                throw new ActionException("modifying unmanageable group");
            }

            if (input.getGroupIds().contains(group.getId())) {
                user.getGroups().add(group);
            } else {
                user.getGroups().remove(group);
            }
        }
    }

    private static class DeleteCsmUserOnRollback extends EmptySynchronization {
        private final User user;

        private DeleteCsmUserOnRollback(User user) {
            this.user = user;
        }

        @Override
        public void afterCompletion(int status) {
            if (status == javax.transaction.Status.STATUS_ROLLEDBACK) {
                try {
                    BiobankCSMSecurityUtil.deleteUser(user);
                } catch (ApplicationException e) {
                    // TODO: what to do?
                }
            }
        }
    }

    private static class RevertPasswordOnRollback extends EmptySynchronization {
        private final Long csmUserId;
        private final String oldPassword;
        private final String newPassword;

        private RevertPasswordOnRollback(Long csmUserId,
            String oldPassword, String newPassword) {
            this.csmUserId = csmUserId;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        @Override
        public void afterCompletion(int status) {
            if (status == javax.transaction.Status.STATUS_ROLLEDBACK) {
                try {
                    BiobankCSMSecurityUtil.modifyPassword(csmUserId,
                        newPassword, oldPassword);
                } catch (ApplicationException e) {
                    // TODO: what to do?
                }
            }
        }
    }

    private static class EmptySynchronization implements Synchronization {
        @Override
        public void afterCompletion(int status) {
        }

        @Override
        public void beforeCompletion() {
        }
    }

    /**
     * Helps merge {@link Membership}-s by comparing them by domain (their
     * {@link Center} and {@link Study}).
     * 
     * @author Jonathan Ferland
     */
    private static class MembershipDomain {
        private final Membership membership;
        private final Integer centerId;
        private final Integer studyId;

        private MembershipDomain(Membership membership) {
            this.membership = membership;
            this.centerId = membership.getCenter().getId();
            this.studyId = membership.getStudy().getId();
        }

        public Membership getMembership() {
            return membership;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = 31 * hash + ((centerId == null) ? 0 : centerId.hashCode());
            hash = 31 * hash + ((studyId == null) ? 0 : studyId.hashCode());
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (getClass() != o.getClass()) return false;
            MembershipDomain that = (MembershipDomain) o;
            if (!NullHelper.safeEquals(centerId, that.centerId)) return false;
            if (!NullHelper.safeEquals(studyId, that.studyId)) return false;
            return true;
        }

        @Override
        public String toString() {
            return "MembershipDomain [membership=" + membership + ", centerId="
                + centerId + ", studyId=" + studyId + "]";
        }

        /**
         * 
         * @param memberships
         * @return
         * @throws IllegalArgumentException if any two resulting
         *             {@link MembershipDomain}-s are equal.
         */
        public static Set<MembershipDomain> from(Set<Membership> memberships)
            throws IllegalArgumentException {
            Set<MembershipDomain> domains = new HashSet<MembershipDomain>();
            for (Membership m : memberships) {
                MembershipDomain d = new MembershipDomain(m);
                if (domains.add(d)) {
                    throw new IllegalArgumentException("duplicate domain " + d);
                }
            }
            return domains;
        }
    }
}
