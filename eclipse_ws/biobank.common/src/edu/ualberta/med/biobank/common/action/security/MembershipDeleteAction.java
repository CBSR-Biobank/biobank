package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Membership;

public class MembershipDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer membershipId;

    public MembershipDeleteAction(Integer id) {
        this.membershipId = id;

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(null);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Membership membership = context.load(Membership.class,
            membershipId);
        context.getSession().delete(membership);
        return new EmptyResult();
    }

}
