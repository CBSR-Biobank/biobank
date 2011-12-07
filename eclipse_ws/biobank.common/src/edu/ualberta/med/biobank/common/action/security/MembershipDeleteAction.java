package edu.ualberta.med.biobank.common.action.security;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class MembershipDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer membershipId;

    public MembershipDeleteAction(Integer id) {
        this.membershipId = id;

    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new UserManagementPermission().isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Membership membership =
            ActionUtil.sessionGet(session, Membership.class, membershipId);
        session.delete(membership);
        return new EmptyResult();
    }

}
