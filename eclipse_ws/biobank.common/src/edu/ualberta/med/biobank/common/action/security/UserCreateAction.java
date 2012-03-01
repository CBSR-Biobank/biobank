package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class UserCreateAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private final Set<Membership> memberships = new HashSet<Membership>(0);
    private final Set<Integer> groupIds = new HashSet<Integer>(0);

    public void setUser(User user) {

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId, new User());

        context.getSession().saveOrUpdate(user);

        return new IdResult(user.getId());
    }
}
