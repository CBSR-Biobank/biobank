package edu.ualberta.med.biobank.common.action.security;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.User;

/**
 * Returns a list of {@link BbGroup}-s that the executing user has
 * <em>complete</em> control over.
 * 
 * @author Jonathan Ferland
 */
public class GroupGetAllAction implements Action<ListResult<BbGroup>> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagementPermission();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public ListResult<BbGroup> run(ActionContext context)
        throws ActionException {
        Criteria c = context.getSession().createCriteria(BbGroup.class, "g")
            .createAlias("g.memberships", "m", Criteria.LEFT_JOIN)
            .createAlias("m.roles", "r", Criteria.LEFT_JOIN)
            .addOrder(Order.asc("name"));

        User user = context.getUser();
        List<BbGroup> groups = new ArrayList<BbGroup>();

        @SuppressWarnings("unchecked")
        List<BbGroup> allGroups = c.list();
        for (BbGroup group : allGroups) {
            if (group.isFullyManageable(user)) {
                groups.add(group);
            }
        }

        return new ListResult<BbGroup>(groups);
    }
}
