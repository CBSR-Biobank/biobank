package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.DiffSet;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.User;

public class UserSaveGroupsAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer userId;
    private final DiffSet<Integer> groupIdsDiff;

    public UserSaveGroupsAction(User user, DiffSet<Integer> groupIdsDiff) {
        this.userId = user.getId();
        this.groupIdsDiff = groupIdsDiff;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId);

        DiffSet<Group> groupsDiff = context.load(Group.class, groupIdsDiff);

        Set<Group> unmanageableGroups = new HashSet<Group>();
        for (Group group : groupsDiff) {
            if (!group.isFullyManageable(context.getUser())) {
                unmanageableGroups.add(group);
            }
        }
        if (!unmanageableGroups.isEmpty()) {
            Set<String> groupNames = new HashSet<String>();
            for (Group group : unmanageableGroups) {
                groupNames.add(group.getName());
            }
        }
        groupsDiff.apply(user.getGroups());

        return new EmptyResult();
    }
}
