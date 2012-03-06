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

    public MembershipSaveAction(Membership m) {
        this.id = m.getId();
        this.centerId = m.getCenter().getId();
        this.studyId = m.getStudy().getId();
        this.rank = m.getRank();
        this.level = m.getLevel();

        this.permissions = m.getPermissions();
        // this.manageablePermissions = m.getManageablePermissions(user);

        this.roleIds = ModelUtil.getIds(m.getRoles());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Membership m = context.load(Membership.class, id, new Membership());

        m.setCenter(context.load(Center.class, centerId));
        m.setStudy(context.load(Study.class, studyId));
        m.setRank(rank);
        m.setLevel(level);

        // m.getPermissions().removeAll(c)

        return new IdResult(m.getId());
    }
}
