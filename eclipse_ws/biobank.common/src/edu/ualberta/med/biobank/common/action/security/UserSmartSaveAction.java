package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.transaction.Synchronization;

import org.hibernate.Transaction;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import edu.ualberta.med.biobank.util.NullHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserSmartSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final UserSaveData data;

    public UserSmartSaveAction(UserSaveData data) {
        this.data = data;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, data.getUserId());

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
            String password = data.getPassword();
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
        if (user.isFullyManageable(data.getContext().getManager())
            // whether the manager (executing user) _still_ can
            && user.isFullyManageable(executingUser)) {

            user.setLogin(data.getLogin());
            user.setEmail(data.getEmail());
            user.setFullName(data.getFullName());
            user.setNeedPwdChange(data.isNeedPwdChange());
            user.setRecvBulkEmails(data.isRecvBulkEmails());

            String newPw = data.getPassword();
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
        User executingUser = context.getUser();

        Set<Integer> contextRoleIds = data.getContext().getRoleIds();
        Map<Integer, Role> rolesMap = context.load(Role.class, contextRoleIds);
        Set<Role> contextRoles = new HashSet<Role>(rolesMap.values());

        // ensure the executing user can do whatever.

        if (user.isNew()) {
            for (Membership membership : data.getMemberships()) {
                user.getMemberships().add(membership);
                membership.setPrincipal(user);
            }
        } else { // merge
            for (Membership membership : data.getMemberships()) {
                // if (membership.get)
            }
        }
    }

    private void setGroups(ActionContext context, User user) {
        User executingUser = context.getUser();

        Set<Integer> groupIds = data.getGroupIds();
        Set<Integer> contextGroupIds = data.getContext().getGroupIds();

        if (!contextGroupIds.containsAll(groupIds)) {
            // TODO: better exception
            throw new ActionException("");
        }

        // add or remove every Group in the context
        Map<Integer, Group> groups = context.load(Group.class, contextGroupIds);
        for (Entry<Integer, Group> entry : groups.entrySet()) {
            Integer groupId = entry.getKey();
            Group group = entry.getValue();

            if (!group.isFullyManageable(executingUser)) {
                // TODO: throw exception
                throw new ActionException("");
            }

            if (groupIds.contains(groupId)) {
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

    private static class DomainUniqueMembership {
        private final Membership membership;
        private final Integer centerId;
        private final Integer studyId;

        private DomainUniqueMembership(Membership membership) {
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
            DomainUniqueMembership that = (DomainUniqueMembership) o;
            if (!NullHelper.safeEquals(centerId, that.centerId)) return false;
            if (!NullHelper.safeEquals(studyId, that.studyId)) return false;
            return true;
        }
    }
}
