package edu.ualberta.med.biobank.common.action.security;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.IdUtil;

/**
 * It is easier to modify
 * 
 * @author Jonathan Ferland
 */
public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer id;
    private final Integer principalId;
    private final Integer centerId;
    private final Integer studyId;
    private final Rank rank;
    private final short level;

    private final SetDiff<PermissionEnum> permissions;
    private final SetDiff<Integer> roleIds;

    public static class SetDiff<T> {
        public SetDiff(Set<T> before, Set<T> after) {
        }

        public static <E> SetDiff<E> of(Set<E> before, Set<E> after) {
            return new SetDiff<E>(before, after);
        }

        // public static Diff<E> ofIds(Set<HasId<E>> before, Set<HasId<E>>
        // after) {

        // }
    }

    // TODO: take a MembershipDTO that knows what PermissionEnum-s and Role-s it
    // is meant to be working with?
    public MembershipSaveAction(ManagedMembership m) {
        id = m.getId();
        principalId = IdUtil.getId(m.getPrincipal());
        centerId = IdUtil.getId(m.getCenter());
        studyId = IdUtil.getId(m.getStudy());
        rank = m.getRank();
        level = m.getLevel();

        permissions = SetDiff.of(m.getPermissionOptions(), m.getPermissions());
        roleIds =
            SetDiff.of(IdUtil.getIds(m.getRoleOptions()),
                IdUtil.getIds(m.getRoles()));
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Membership m = context.load(Membership.class, id, new Membership());

        if (m.getId() != null) checkManageability(m, context);

        m.setPrincipal(context.load(Principal.class, principalId));
        m.setCenter(context.load(Center.class, centerId));
        m.setStudy(context.load(Study.class, studyId));
        m.setRank(rank);
        m.setLevel(level);

        // m.getPermissions().removeAll(c)

        // TODO: check user can still manage all permissions before removing
        // TODO: check user can still manage all roles before removing

        // context.load(Role.class, roleIds);

        checkManageability(m, context);

        return new IdResult(m.getId());
    }

    private void checkManageability(Membership m, ActionContext c) {
        if (!m.isManageable(c.getUser())) {
            throw new ActionException(
                "you do not have the permissions to modify this membership");
        }
    }
}
