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
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.ModelUtil;

/**
 * It is easier to modify
 * 
 * @author Jonathan Ferland
 */
public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer id;
    private final Integer centerId;
    private final Integer studyId;
    private final Rank rank;
    private final short level;
    private final Set<PermissionEnum> permissions;
    private final Set<Integer> roleIds;

    // TODO: take a MembershipDTO that knows what PermissionEnum-s and Role-s it
    // is meant to be working with?
    public MembershipSaveAction(Membership m, User manager) {
        this.id = m.getId();
        this.centerId = ModelUtil.getId(m.getCenter());
        this.studyId = ModelUtil.getId(m.getStudy());
        this.rank = m.getRank();
        this.level = m.getLevel();

        // I HAVE TO SEND THE OPTIONS, SO WHAT I THINK IS GOING TO BE DONE IS
        // WHAT IS ACTUALLY DONE.
        // Note that this can be done by either:
        // (1) sending the entire set you think you're working with or
        // (2) sending what you think the differences are, (i.e. and added set
        // and a removed set)
        // -- don't bother sending the Membership PE and Role options back, we
        // can figure this out on the client.
        this.permissions = m.getPermissions();
        // this.manageablePermissions =
        m.getManageablePermissions(manager);
        m.getManageableRoles(manager);

        // EnumSet.

        this.roleIds = ModelUtil.getIds(m.getRoles());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Membership m = context.load(Membership.class, id, new Membership());

        if (m.getId() != null) checkManageability(m, context);

        m.setCenter(context.load(Center.class, centerId));
        m.setStudy(context.load(Study.class, studyId));
        m.setRank(rank);
        m.setLevel(level);

        // m.getPermissions().removeAll(c)

        // TODO: check user can still manage all permissions before removing
        // TODO: check user can still manage all roles before removing

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
