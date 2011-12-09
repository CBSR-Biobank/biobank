package edu.ualberta.med.biobank.common.action.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
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
    public IdResult run(User user, Session session) throws ActionException {
        if (description == null) {
            throw new NullPropertyException(BbGroup.class,
                BbGroupPeer.DESCRIPTION);
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        group = sessionUtil.get(BbGroup.class, principalId, new BbGroup());

        group.setDescription(description);
        saveUsers();

        return run(user, session, group);
    }

    private void saveUsers() {
        Map<Integer, User> users = sessionUtil.load(User.class, userIds);

        SetDifference<User> usersDiff =
            new SetDifference<User>(group.getUserCollection(),
                users.values());
        group.setUserCollection(usersDiff.getNewSet());

        // remove this group from users in removed list
        for (User user : usersDiff.getRemoveSet()) {
            Collection<BbGroup> userGroups = user.getGroupCollection();
            if (userGroups.remove(user)) {
                user.setGroupCollection(userGroups);
            } else {
                throw new ActionException(
                    "group not found in user's collection");
            }
        }
    }

}
