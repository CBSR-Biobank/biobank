package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.DiffSet;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.BundleI18dMessage;
import edu.ualberta.med.biobank.i18n.I18dMessage;
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
    public static final I18dMessage ILLEGAL_PERMS_MODIFIED =
        new BundleI18dMessage("messages", "illegalPermissionsModified");
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
        if (!membership.isManageable(manager)) {
            // throw new I18dActionException(new OgnlI18dMessage(
            // ActionI18dMessage.ONE, membership));
        }
    }

    private void setPermissions(Membership memb, User manager) {
        Set<PermissionEnum> disallowed = new HashSet<PermissionEnum>(permsDiff);
        disallowed.removeAll(memb.getManageablePermissions(manager));
        if (!disallowed.isEmpty()) {
            // I18dMessage msg =
            // new OgnlI18dMessage(ILLEGAL_PERMS_MODIFIED, disallowed);
            // throw new I18dActionException(msg);
        }
        permsDiff.apply(memb.getPermissions());
    }

    private void setRoles(Membership m, User manager, ActionContext context) {
        DiffSet<Role> rolesDiff = context.load(Role.class, roleIdsDiff);
        Set<Role> disallowed = new HashSet<Role>(rolesDiff);
        disallowed.removeAll(m.getManageableRoles(manager, rolesDiff));
        if (!disallowed.isEmpty()) {
            // throw new I18dActionException(
            // "MembershipSaveAciton.disallowedPermissions",
            // disallowed);
        }
        rolesDiff.apply(m.getRoles());
    }
}
