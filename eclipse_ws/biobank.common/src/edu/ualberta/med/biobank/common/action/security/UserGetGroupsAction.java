package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.security.UserGetGroupsAction.Result;
import edu.ualberta.med.biobank.common.action.util.WorkingSet;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.User;

public class UserGetGroupsAction implements Action<Result> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer userId;

    public UserGetGroupsAction(User user) {
        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public Result run(ActionContext context) throws ActionException {
        Criteria criteria = context.getSession().createCriteria(User.class)
            .setFetchMode("groups", FetchMode.JOIN)
            .setFetchMode("groups.permissions", FetchMode.JOIN)
            .setFetchMode("groups.roles", FetchMode.JOIN)
            .setFetchMode("groups.roles.permissions", FetchMode.JOIN)
            .add(Restrictions.idEq(userId));

        User user = null;

        Set<Group> groups = new HashSet<Group>();
        for (Group group : user.getGroups()) {
            if (group.isFullyManageable(context.getUser())) {
                groups.add(group);
            }
        }

        return new Result(groups);
    }

    public static class Result implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final WorkingSet<Group> groups;

        Result(Set<Group> groups) {
            this.groups = new WorkingSet<Group>(groups);
        }

        public WorkingSet<Group> getGroups() {
            return groups;
        }
    }
}
