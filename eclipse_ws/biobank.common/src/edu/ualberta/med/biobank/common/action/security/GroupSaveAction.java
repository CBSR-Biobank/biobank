package edu.ualberta.med.biobank.common.action.security;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;

public class GroupSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final GroupSaveInput input;

    public GroupSaveAction(GroupSaveInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User executingUser = context.getUser();
        Group group = context.get(Group.class, input.getGroupId(), new Group());

        checkFullyManageable(group, executingUser);

        group.setName(input.getName());
        group.setDescription(input.getDescription());

        Set<User> users = context.load(User.class, input.getUserIds());
        group.getUsers().clear();
        group.getUsers().addAll(users);

        /** Overwrite all {@link Membership}-s. */
        group.getMemberships().clear();
        for (Membership m : input.getMemberships()) {
            m.setPrincipal(group);
            group.getMemberships().add(m);
        }

        checkFullyManageable(group, executingUser);

        context.getSession().saveOrUpdate(group);

        return new IdResult(group.getId());
    }

    private void checkFullyManageable(Group group, User executingUser) {
        if (!group.isFullyManageable(executingUser)) {
            // TODO: better message
            throw new ActionException("group is not manageable");
        }
    }
}
