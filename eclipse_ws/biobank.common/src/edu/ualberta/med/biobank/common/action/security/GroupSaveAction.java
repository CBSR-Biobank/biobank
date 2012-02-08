package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.User;

public class GroupSaveAction extends PrincipalSaveAction {

    private static final long serialVersionUID = 1L;

    private String description = null;
    private BbGroup group = null;
    private Set<Integer> userIds;

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        group = context.get(BbGroup.class, principalId, new BbGroup());

        group.setDescription(description);
        saveUsers(context);

        return run(context, group);
    }

    private void saveUsers(ActionContext context) {
        Map<Integer, User> users = context.load(User.class, userIds);

        SetDifference<User> usersDiff =
            new SetDifference<User>(group.getUserCollection(),
                users.values());
        group.setUserCollection(usersDiff.getNewSet());

        // remove this group from users in removed list
        for (User user : usersDiff.getRemoveSet()) {
            Set<BbGroup> userGroups = user.getGroupCollection();
            if (userGroups.remove(user)) {
                user.setGroupCollection(userGroups);
            } else {
                throw new ActionException(
                    "group not found in user's collection");
            }
        }
    }

}
