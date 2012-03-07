package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SetDiff;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.IdUtil;

public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer id;
    private final Integer principalId;
    private final Integer centerId;
    private final Integer studyId;
    private final Rank rank;
    private final short level;

    private final SetDiff<PermissionEnum> permissionsDiff;
    private final SetDiff<Integer> roleIdsDiff;

    public MembershipSaveAction(ManagedMembership m) {
        id = m.getId();
        principalId = IdUtil.getId(m.getPrincipal());
        centerId = IdUtil.getId(m.getCenter());
        studyId = IdUtil.getId(m.getStudy());
        rank = m.getRank();
        level = m.getLevel();

        permissionsDiff =
            SetDiff.of(m.getPermissionOptions(), m.getPermissions());
        roleIdsDiff = SetDiff.ofIds(m.getRoleOptions(), m.getRoles());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Membership m = context.load(Membership.class, id, new Membership());
        User user = context.getUser();

        if (m.getId() != null) checkManageability(m, user);

        m.setPrincipal(context.load(Principal.class, principalId));
        m.setCenter(context.load(Center.class, centerId));
        m.setStudy(context.load(Study.class, studyId));
        m.setRank(rank);
        m.setLevel(level);

        if (!m.getManageablePermissions(user).containsAll(
            permissionsDiff.getDifference())) {
            throw new ActionException("Not allowed");
        }

        permissionsDiff.apply(m.getPermissions());

        Map<Integer, Role> roleAdditions =
            context.load(Role.class, roleIdsDiff.getAdditions());

        Map<Integer, Role> roleRemovals =
            context.load(Role.class, roleIdsDiff.getRemovals());

        m.getRoles().removeAll(roleRemovals.values());
        m.getRoles().addAll(roleAdditions.values());

        checkManageability(m, user);

        return new IdResult(m.getId());
    }

    private void checkManageability(Membership membership, User user) {
        if (!membership.isManageable(user)) {
            throw new ActionException(
                "you do not have the permissions to modify this membership");
        }
    }
}
