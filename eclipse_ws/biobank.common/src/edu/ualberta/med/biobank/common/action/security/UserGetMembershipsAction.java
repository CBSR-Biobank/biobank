package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class UserGetMembershipsAction
    implements Action<ListResult<ManagedMembership>> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer userId;

    public UserGetMembershipsAction(User user) {
        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public ListResult<ManagedMembership> run(ActionContext context)
        throws ActionException {
        Criteria criteria = context.getSession().createCriteria(User.class)
            .setFetchMode("memberships", FetchMode.JOIN)
            .setFetchMode("memberships.permissions", FetchMode.JOIN)
            .setFetchMode("memberships.roles", FetchMode.JOIN)
            .setFetchMode("memberships.roles.permissions", FetchMode.JOIN)
            .add(Restrictions.idEq(userId));

        User user = null; // ActionContext.uniqueResult(criteria, User.class);

        Set<ManagedMembership> mms = new HashSet<ManagedMembership>();
        ManagedMembership mm;
        for (Membership membership : user.getMemberships()) {
            // mm = new ManagedMembership
        }

        return new ListResult<ManagedMembership>(mms);
    }
}
