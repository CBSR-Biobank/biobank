package edu.ualberta.med.biobank.action.security;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.security.Group;
import edu.ualberta.med.biobank.model.security.Role;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Study;

public class ManagerContextGetAction implements Action<ManagerContextGetOutput> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    @SuppressWarnings("unused")
    private final ManagerContextGetInput input;

    public ManagerContextGetAction(ManagerContextGetInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ManagerContextGetOutput run(ActionContext context)
        throws ActionException {

        @SuppressWarnings("unchecked")
        List<Center> centers = context.getSession()
            .createCriteria(Center.class)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .addOrder(Order.asc("name"))
            .list();

        @SuppressWarnings("unchecked")
        List<Study> studies = context.getSession()
            .createCriteria(Study.class)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .addOrder(Order.asc("name"))
            .list();

        @SuppressWarnings("unchecked")
        List<Role> roles = context.getSession()
            .createCriteria(Role.class)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .addOrder(Order.asc("name"))
            .list();

        @SuppressWarnings("unchecked")
        List<User> users = context.getSession()
            .createCriteria(User.class)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .addOrder(Order.asc("login"))
            .list();

        GroupGetAllOutput groupGetAllOutput = new GroupGetAllAction(
            new GroupGetAllInput()).run(context);
        List<Group> groups =
            new ArrayList<Group>(groupGetAllOutput.getAllManageableGroups());

        boolean roleManager = new RoleManagementPermission().isAllowed(context);

        // TODO: redesign this whole context system.
        User manager = context.getUser();
        MembershipContextGetAction.initManager(manager);

        ManagerContext managerContext =
            new ManagerContext(manager, roles, groups,
                users, centers, studies, roleManager);

        return new ManagerContextGetOutput(managerContext);
    }
}
