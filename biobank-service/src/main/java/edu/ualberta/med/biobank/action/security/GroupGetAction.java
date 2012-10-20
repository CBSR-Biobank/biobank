package edu.ualberta.med.biobank.action.security;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.security.Group;

public class GroupGetAction implements Action<GroupGetOutput> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final GroupGetInput input;

    public GroupGetAction(GroupGetInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public GroupGetOutput run(ActionContext context) throws ActionException {
        @SuppressWarnings("nls")
        Criteria c = context.getSession()
            .createCriteria(Group.class, "g")
            .createAlias("g.memberships", "m", Criteria.LEFT_JOIN)
            .createAlias("g.users", "u", Criteria.LEFT_JOIN)
            .createAlias("m.domain", "d", Criteria.LEFT_JOIN)
            .createAlias("d.centers", "c", Criteria.LEFT_JOIN)
            .createAlias("d.studies", "s", Criteria.LEFT_JOIN)
            .createAlias("m.roles", "r", Criteria.LEFT_JOIN)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .add(Restrictions.idEq(input.getGroupId()));

        Group group = (Group) c.uniqueResult();

        MembershipContext managerContext = new MembershipContextGetAction(
            new MembershipContextGetInput()).run(context).getContext();

        return new GroupGetOutput(group, managerContext);
    }
}
