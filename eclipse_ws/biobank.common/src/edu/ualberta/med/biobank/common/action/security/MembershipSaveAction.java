package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer membershipId;
    private Integer principalId;
    private Set<Integer> roleIds;
    private Set<Integer> permissionIds;
    private Integer centerId;
    private Integer studyId;
    private Session session = null;
    private SessionUtil sessionUtil = null;
    private Membership membership = null;

    public void setId(Integer id) {
        this.membershipId = id;
    }

    public void setPrincipalId(Integer principalId) {
        this.principalId = principalId;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new UserManagementPermission().isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (roleIds == null) {
            throw new NullPropertyException(Membership.class,
                "role ids cannot be null");
        }
        if (permissionIds == null) {
            throw new NullPropertyException(Membership.class,
                "permission ids cannot be null");
        }

        this.session = session;
        sessionUtil = new SessionUtil(session);
        ActionContext actionContext = new ActionContext(user, session);

        membership = sessionUtil.get(
            Membership.class, membershipId, new Membership());
        membership.setPrincipal(actionContext
            .load(Principal.class, principalId));
        membership.setCenter(actionContext.load(Center.class, centerId));
        membership.setStudy(actionContext.load(Study.class, studyId));

        saveRoles();
        savePermissions();

        session.saveOrUpdate(membership);
        session.flush();

        return new IdResult(membership.getId());
    }

    /*
     * Membership to Role association is unidirectional.
     */
    private void saveRoles() {
        Map<Integer, Role> roles = sessionUtil.load(Role.class, roleIds);

        SetDifference<Role> rolesDiff = new SetDifference<Role>(
            membership.getRoleCollection(), roles.values());
        membership.setRoleCollection(rolesDiff.getNewSet());
    }

    /*
     * Membership to Permission association is unidirectional.
     */
    private void savePermissions() {
        Map<Integer, Permission> permissions =
            sessionUtil.load(Permission.class, permissionIds);

        SetDifference<Permission> permissionsDiff =
            new SetDifference<Permission>(
                membership.getPermissionCollection(), permissions.values());
        membership.setPermissionCollection(permissionsDiff.getNewSet());
    }
}
