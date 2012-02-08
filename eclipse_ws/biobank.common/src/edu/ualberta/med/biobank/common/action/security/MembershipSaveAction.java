package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;

public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer membershipId;
    private Integer principalId;
    private Set<Integer> roleIds;
    private Set<PermissionEnum> permissions;
    private Integer centerId;
    private Integer studyId;
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

    public void setPermissionIds(Set<PermissionEnum> permissions) {
        this.permissions = permissions;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        membership =
            context.get(Membership.class, membershipId, new Membership());
        membership.setPrincipal(context.load(Principal.class, principalId));
        membership.setCenter(context.load(Center.class, centerId));
        membership.setStudy(context.load(Study.class, studyId));

        saveRoles(context);
        savePermissions(context);

        context.getSession().saveOrUpdate(membership);
        context.getSession().flush();

        return new IdResult(membership.getId());
    }

    /*
     * Membership to Role association is unidirectional.
     */
    private void saveRoles(ActionContext context) {
        Map<Integer, Role> roles = context.load(Role.class, roleIds);

        SetDifference<Role> rolesDiff = new SetDifference<Role>(
            membership.getRoleCollection(), roles.values());
        membership.setRoleCollection(rolesDiff.getNewSet());
    }

    /*
     * Membership to Permission association is unidirectional.
     */
    private void savePermissions(ActionContext context) {
        SetDifference<PermissionEnum> permissionsDiff =
            new SetDifference<PermissionEnum>(
                membership.getPermissionCollection(), permissions);
        membership.setPermissionCollection(permissionsDiff.getNewSet());
    }
}
