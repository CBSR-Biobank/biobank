package edu.ualberta.med.biobank.common.action.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
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
    private Integer aStatusId;
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

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (login == null) {
            throw new NullPropertyException(User.class, UserPeer.LOGIN);
        }
        if (csmUserId == null) {
            throw new NullPropertyException(User.class, UserPeer.CSM_USER_ID);
        }
        if (recvBulkEmails == null) {
            throw new NullPropertyException(User.class,
                UserPeer.RECV_BULK_EMAILS);
        }
        if (fullName == null) {
            throw new NullPropertyException(User.class, UserPeer.FULL_NAME);
        }
        if (email == null) {
            throw new NullPropertyException(User.class, UserPeer.EMAIL);
        }
        if (needPwdChange == null) {
            throw new NullPropertyException(User.class,
                UserPeer.NEED_PWD_CHANGE);
        }
        if (aStatusId == null) {
            throw new NullPropertyException(User.class, "activity status id");
        }
        if (groupIds == null) {
            throw new NullPropertyException(User.class, "group ids");
        }

        // check for duplicate login
        List<ValueProperty<User>> uniqueValProps =
            new ArrayList<ValueProperty<User>>();
        uniqueValProps.add(new ValueProperty<User>(UserPeer.LOGIN, login));
        new UniquePreCheck<User>(User.class, principalId, uniqueValProps).run(
            context);

        // check for duplicate csmUserId
        uniqueValProps = new ArrayList<ValueProperty<User>>();
        uniqueValProps.add(
            new ValueProperty<User>(UserPeer.CSM_USER_ID, csmUserId));
        new UniquePreCheck<User>(User.class, principalId, uniqueValProps).run(
            context);

        // check for duplicate fullname
        uniqueValProps = new ArrayList<ValueProperty<User>>();
        uniqueValProps.add(
            new ValueProperty<User>(UserPeer.FULL_NAME, fullName));
        new UniquePreCheck<User>(User.class, principalId, uniqueValProps).run(
            context);

        newUser = context.get(User.class, principalId, new User());

        newUser.setLogin(login);
        newUser.setCsmUserId(csmUserId);
        newUser.setRecvBulkEmails(recvBulkEmails);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setNeedPwdChange(needPwdChange);

        ActivityStatus aStatus =
            context.load(ActivityStatus.class, aStatusId);
        newUser.setActivityStatus(aStatus);

        saveGroups(context);

        return run(context, newUser);
    }

    private void saveGroups(ActionContext context) {
        Map<Integer, BbGroup> groups =
            context.load(BbGroup.class, groupIds);

        SetDifference<BbGroup> groupsDiff =
            new SetDifference<BbGroup>(newUser.getGroupCollection(),
                groups.values());
        newUser.setGroupCollection(groupsDiff.getNewSet());

        // remove newUser from groups in removed list
        for (BbGroup group : groupsDiff.getRemoveSet()) {
            Collection<User> groupUsers = group.getUserCollection();
            if (groupUsers.remove(newUser)) {
                group.setUserCollection(groupUsers);
            } else {
                throw new ActionException(
                    "user not found in group's collection");
            }
        }
    }
}
