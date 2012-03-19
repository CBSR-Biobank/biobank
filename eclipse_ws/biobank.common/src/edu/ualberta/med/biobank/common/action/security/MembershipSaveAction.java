package edu.ualberta.med.biobank.common.action.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.L10nedActionException;
import edu.ualberta.med.biobank.common.action.util.DiffSet;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.CommonMessages;
import edu.ualberta.med.biobank.i18n.I18nUtil;
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

    private final DiffSet<PermissionEnum> permsDiff;
    private final DiffSet<Integer> roleIdsDiff;

    public MembershipSaveAction(ManagedMembership m) {
        id = m.getId();
        principalId = IdUtil.getId(m.getPrincipal());
        centerId = IdUtil.getId(m.getCenter());
        studyId = IdUtil.getId(m.getStudy());
        rank = m.getRank();
        level = m.getLevel();

        permsDiff = DiffSet.of(m.getPermissionOptions(), m.getPermissions());
        roleIdsDiff = DiffSet.ofIds(m.getRoleOptions(), m.getRoles());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Membership memb = context.load(Membership.class, id, new Membership());
        User manager = context.getUser();

        if (memb.getId() != null) checkManageability(memb, manager);

        memb.setPrincipal(context.load(Principal.class, principalId));
        memb.setCenter(context.load(Center.class, centerId));
        memb.setStudy(context.load(Study.class, studyId));
        memb.setRank(rank);
        memb.setLevel(level);

        setPermissions(memb, manager);
        setRoles(memb, manager, context);

        checkManageability(memb, manager);

        return new IdResult(memb.getId());
    }

    private void checkManageability(Membership membership, User manager) {
        if (!membership.isPartiallyManageable(manager)) {
            throw new L10nedActionException(
                CommonMessages.MEMBERSHIP_SAVE_NOT_ALLOWED);
        }
    }

    private void setPermissions(Membership memb, User manager) {
        Set<PermissionEnum> illegal = new HashSet<PermissionEnum>(permsDiff);
        illegal.removeAll(memb.getManageablePermissions(manager));
        if (!illegal.isEmpty()) {
            List<String> permNames = new ArrayList<String>();
            for (PermissionEnum permission : illegal) {
                permNames.add(permission.getName());
            }

            throw L10nedActionException.ognl(
                CommonMessages.MEMBERSHIP_SAVE_ILLEGAL_PERMS_MODIFIED,
                I18nUtil.join(permNames));
        }
        permsDiff.apply(memb.getPermissions());
    }

    private void setRoles(Membership m, User manager, ActionContext context) {
        DiffSet<Role> rolesDiff = context.load(Role.class, roleIdsDiff);
        Set<Role> illegal = new HashSet<Role>(rolesDiff);
        illegal.removeAll(m.getManageableRoles(manager, rolesDiff));
        if (!illegal.isEmpty()) {
            List<String> roleNames = new ArrayList<String>();
            for (Role role : illegal) {
                roleNames.add(role.getName());
            }

            throw L10nedActionException.ognl(
                CommonMessages.MEMBERSHIP_SAVE_ILLEGAL_ROLES_MODIFIED,
                I18nUtil.join(roleNames));
        }
        rolesDiff.apply(m.getRoles());
    }
}
