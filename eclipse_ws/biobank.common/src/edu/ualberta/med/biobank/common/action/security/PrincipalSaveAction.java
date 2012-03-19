package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.DiffSet;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Principal;

public abstract class PrincipalSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer principalId;
    private DiffSet<Integer> membershipIds;

    public PrincipalSaveAction(Principal mp) {
        principalId = null;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    public IdResult run(ActionContext context, Principal principal)
        throws ActionException {
        principal.setId(principalId);

        // Map<Integer, Membership> memberships =
        // context.load(Membership.class, membershipIds);
        //
        // SetDifference<Membership> sitesDiff =
        // new SetDifference<Membership>(principal.getMemberships(),
        // memberships.values());
        // principal.setMemberships(sitesDiff.getNewSet());
        //
        // // remove memberships no longer used
        // for (Membership membership : sitesDiff.getRemoveSet()) {
        // context.getSession().delete(membership);
        // }
        //
        // context.getSession().saveOrUpdate(principal);
        // context.getSession().flush();

        return new IdResult(principal.getId());
    }

}
