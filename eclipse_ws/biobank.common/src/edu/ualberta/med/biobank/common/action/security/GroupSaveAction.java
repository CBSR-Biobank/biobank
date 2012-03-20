package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.User;

public class GroupSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private String name = null;
    private String description = null;
    private Group group = null;
    private Set<Integer> userIds;

    public GroupSaveAction() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        group = null; // context.get(Group.class, principalId, new Group());

        group.setDescription(description);
        saveUsers(context);

        return null;
    }

    private void saveUsers(ActionContext context) {
        Map<Integer, User> users = context.load(User.class, userIds);

        // TODO: localise and parameterise the exception message
        if (!group.isFullyManageable(context.getUser()))
            throw new ActionException(
                "you do not have adequate permissions to modify this group");

        SetDifference<User> usersDiff =
            new SetDifference<User>(group.getUsers(),
                users.values());
        group.setUsers(usersDiff.getNewSet());

        // remove this group from users in removed list
        for (User user : usersDiff.getRemoveSet()) {
            Set<Group> userGroups = user.getGroups();
            if (userGroups.remove(user)) {
                user.setGroups(userGroups);
            } else {
                throw new ActionException(
                    "group not found in user's collection");
            }
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

}
