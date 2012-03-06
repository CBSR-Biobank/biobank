package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Principal;

public abstract class PrincipalSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    protected Integer principalId = null;

    private Set<Integer> membershipIds;

    public void setId(Integer id) {
        this.principalId = id;
    }

    public void setMembershipIds(Set<Integer> membershipIds) {
        this.membershipIds = membershipIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    public IdResult run(ActionContext context, Principal principal)
        throws ActionException {
        principal.setId(principalId);

        Map<Integer, Membership> memberships =
            context.load(Membership.class, membershipIds);

        SetDifference<Membership> sitesDiff =
            new SetDifference<Membership>(principal.getMemberships(),
                memberships.values());
        principal.setMemberships(sitesDiff.getNewSet());

        // remove memberships no longer used
        for (Membership membership : sitesDiff.getRemoveSet()) {
            context.getSession().delete(membership);
        }

        context.getSession().saveOrUpdate(principal);
        context.getSession().flush();

        return new IdResult(principal.getId());
    }

}
