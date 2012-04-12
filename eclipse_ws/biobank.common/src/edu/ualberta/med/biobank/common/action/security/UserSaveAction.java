package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Synchronization;

import org.hibernate.Transaction;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.LocalizedString;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import edu.ualberta.med.biobank.util.SetDiff;
import edu.ualberta.med.biobank.util.SetDiff.Pair;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserSaveAction implements Action<UserSaveOutput> {
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
    public UserSaveOutput run(ActionContext context) throws ActionException {
        User user = context.load(User.class, input.getUserId(), new User());

        setProperties(context, user);
        setMemberships(context, user);
        setGroups(context, user);

        context.getSession().saveOrUpdate(user);

        return new UserSaveOutput(user.getId(), user.getCsmUserId());
    }

    private void setProperties(ActionContext context, User user) {
        User executingUser = context.getUser();
        // whether the manager _could_ have modified the user properties
        if (user.isFullyManageable(input.getContext().getManager())
            // whether the manager (executing user) _still_ can
            && user.isFullyManageable(executingUser)) {

            User oldUserData = new User();
            oldUserData.setId(user.getId());
            oldUserData.setCsmUserId(user.getCsmUserId());
            oldUserData.setLogin(user.getLogin());
            oldUserData.setEmail(user.getEmail());
            oldUserData.setFullName(user.getFullName());
            oldUserData.setNeedPwdChange(user.getNeedPwdChange());
            oldUserData.setRecvBulkEmails(user.getRecvBulkEmails());

            user.setLogin(input.getLogin());
            user.setEmail(input.getEmail());
            user.setFullName(input.getFullName());
            user.setNeedPwdChange(input.isNeedPwdChange());
            user.setRecvBulkEmails(input.isRecvBulkEmails());

            setCsmUserProperties(context, user, oldUserData);
        }
    }

    @SuppressWarnings("nls")
    private void setCsmUserProperties(ActionContext context, User user,
        User oldUserData) {
        try {
            boolean newCsmUser = user.getCsmUserId() == null;
            String pw = input.getPassword();

            if (newCsmUser) {
                Long csmUserId = BiobankCSMSecurityUtil.persistUser(user, pw);
                user.setCsmUserId(csmUserId);

                final int MIN_PW_LENGTH = 5;

                if (pw == null || pw.length() < MIN_PW_LENGTH) {
                    throw new ActionException(
                        LocalizedString.tr(
                            "Passwords must be at least {0} characters long",
                            MIN_PW_LENGTH));
                }

                Transaction tx = context.getSession().getTransaction();
                tx.registerSynchronization(new DeleteCsmUserOnRollback(user));
            } else {
                String login = oldUserData.getLogin();
                String oldPw = BiobankCSMSecurityUtil.getUserPassword(login);

                BiobankCSMSecurityUtil.persistUser(user, pw);

                Transaction tx = context.getSession().getTransaction();
                tx.registerSynchronization(new PersistCsmUserOnRollback(
                    oldUserData, oldPw));
            }
        } catch (ApplicationException e) {
            throw new ActionException(
                LocalizedString.tr("Problem modify associated CSM user properties"), e);
        }
    }

    @SuppressWarnings("nls")
    private Set<Membership> getManageableMemberships(ActionContext context,
        User user) {
        User manager = input.getContext().getManager();
        User executor = context.getUser();
        Set<Membership> managerMembs = user.getManageableMemberships(manager);
        Set<Membership> executorMembs = user.getManageableMemberships(executor);

        if (!managerMembs.containsAll(executorMembs)) {
            throw new ActionException(
                LocalizedString.tr("Your manageable memberships have changed since you began modifying this user. Please start over and try again."));
        }

        return managerMembs;
    }

    private void setMemberships(ActionContext context, User user) {
        Set<Membership> manageable = getManageableMemberships(context, user);
        SetDiff<Membership> diff = new SetDiff<Membership>(
            manageable, input.getMemberships());

        handleMembershipDeletes(context, diff.getRemovals());
        handleMembershipInserts(context, user, diff.getAdditions());
        handleMembershipUpdates(context, diff.getIntersection());

        mergeMembershipsOnDomain(context, user);

        for (Membership m : user.getMemberships()) {
            m.reducePermissions();
        }
    }

    private void handleMembershipDeletes(ActionContext context,
        Set<Membership> removals) {
        for (Membership membership : removals) {
            checkFullyManageable(context, membership);

            membership.getPrincipal().getMemberships().remove(membership);
            context.getSession().delete(membership);
        }
    }

    private void handleMembershipInserts(ActionContext context, User user,
        Set<Membership> additions) {
        for (Membership membership : additions) {
            checkFullyManageable(context, membership);

            user.getMemberships().add(membership);
            membership.setPrincipal(user);
        }
    }

    private void handleMembershipUpdates(ActionContext context,
        Set<Pair<Membership>> conflicts) {
        User executingUser = context.getUser();
        User manager = input.getContext().getManager();

        // TODO: reload these
        Set<Role> contextRoles = input.getContext().getRoles();

        Set<PermissionEnum> perms, oldPermissionScope, newPermissionScope;
        Set<Role> roles, oldRoleScope, newRoleScope;
        for (Pair<Membership> conflict : conflicts) {
            Membership oldM = conflict.getOld();
            Membership newM = conflict.getNew();

            // the permissions and roles the user would have intended to modify
            oldPermissionScope = oldM.getManageablePermissions(manager);
            oldRoleScope = oldM.getManageableRoles(manager, contextRoles);

            // the permissions and roles the executing user can currently modify
            newPermissionScope = newM.getManageablePermissions(executingUser);
            newRoleScope = newM.getManageableRoles(executingUser, contextRoles);

            // ensure the old (client?) scope can still be modified by the new
            // (server?) scope
            if (!newPermissionScope.containsAll(oldPermissionScope)
                || !newRoleScope.containsAll(oldRoleScope)) {
                throw new ActionException(
                    LocalizedString.tr("Your roles or permissions have changed since you began modifying this user. Please start over and try again."));
            }

            Domain newD = newM.getDomain();
            Domain oldD = oldM.getDomain();

            oldD.getCenters().clear();
            oldD.getCenters().addAll(newD.getCenters());
            oldD.setAllCenters(newD.isAllCenters());

            oldD.getStudies().clear();
            oldD.getStudies().addAll(newD.getStudies());
            oldD.setAllStudies(newD.isAllStudies());

            // clear out the old scope and assign intended values
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

            oldM.setUserManager(newM.isUserManager());
            oldM.setEveryPermission(newM.isEveryPermission());

            checkFullyManageable(context, oldM);
        }
    }

    private void mergeMembershipsOnDomain(ActionContext context, User user) {

    }

    private void checkFullyManageable(ActionContext context, Membership m) {
        User executingUser = context.getUser();
        if (!m.isFullyManageable(executingUser)) {
            throw new ActionException(
                LocalizedString.tr("You do not have adequate permissions to modify this user."));
        }
    }

    @SuppressWarnings("nls")
    private void setGroups(ActionContext context, User user) {
        User executingUser = context.getUser();

        // TODO: reload these
        Set<Group> contextGroups = input.getContext().getGroups();
        Set<Integer> contextGroupIds = IdUtil.getIds(contextGroups);

        if (!contextGroupIds.containsAll(input.getGroupIds())) {
            throw new ActionException(
                LocalizedString.tr("It appears you are trying to add groups you were not aware of. Please start over and try again."));
        }

        // add or remove every Group in the context
        Set<Group> groups = context.load(Group.class, contextGroupIds);
        for (Group group : groups) {
            if (!group.isFullyManageable(executingUser)) {
                throw new ActionException(
                    LocalizedString.tr("You cannot add this user to groups you cannot manage."));
            }

            if (input.getGroupIds().contains(group.getId())) {
                user.getGroups().add(group);
                group.getUsers().add(user);
            } else {
                user.getGroups().remove(group);
                group.getUsers().remove(user);
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

    private static class PersistCsmUserOnRollback extends EmptySynchronization {
        private final User user;
        private final String oldPassword;

        private PersistCsmUserOnRollback(User user, String oldPassword) {
            this.user = user;
            this.oldPassword = oldPassword;
        }

        @Override
        public void afterCompletion(int status) {
            if (status == javax.transaction.Status.STATUS_ROLLEDBACK) {
                try {
                    BiobankCSMSecurityUtil.persistUser(user, oldPassword);
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
}
