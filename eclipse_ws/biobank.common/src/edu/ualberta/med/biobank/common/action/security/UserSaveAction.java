package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.User;

public class UserSaveAction extends PrincipalSaveAction {

    private static final long serialVersionUID = 1L;

    private String login;
    private Long csmUserId;
    private Boolean recvBulkEmails;
    private String fullName;
    private String email;
    private Boolean needPwdChange;
    private ActivityStatus activityStatus;
    private Set<Integer> groupIds;
    private User newUser;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setCsmUserId(Long csmUserId) {
        this.csmUserId = csmUserId;
    }

    public void setRecvBulkEmails(Boolean recvBulkEmails) {
        this.recvBulkEmails = recvBulkEmails;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNeedPwdChange(Boolean needPwdChange) {
        this.needPwdChange = needPwdChange;
    }

    public void setActivityStatusId(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        newUser = context.get(User.class, principalId, new User());

        newUser.setLogin(login);
        newUser.setCsmUserId(csmUserId);
        newUser.setRecvBulkEmails(recvBulkEmails);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setNeedPwdChange(needPwdChange);

        newUser.setActivityStatus(activityStatus);

        saveGroups(context);

        return run(context, newUser);
    }

    private void saveGroups(ActionContext context) {
        Map<Integer, BbGroup> groups =
            context.load(BbGroup.class, groupIds);

        SetDifference<BbGroup> groupsDiff =
            new SetDifference<BbGroup>(newUser.getGroups(),
                groups.values());
        newUser.setGroups(groupsDiff.getNewSet());

        // remove newUser from groups in removed list
        for (BbGroup group : groupsDiff.getRemoveSet()) {
            Set<User> groupUsers = group.getUsers();
            if (groupUsers.remove(newUser)) {
                group.setUsers(groupUsers);
            } else {
                throw new ActionException(
                    "user not found in group's collection");
            }
        }
    }
}
