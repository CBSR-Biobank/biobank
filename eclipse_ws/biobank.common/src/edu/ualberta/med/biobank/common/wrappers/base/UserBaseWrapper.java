/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.BbGroupBaseWrapper;
import java.util.Arrays;

public abstract class UserBaseWrapper extends PrincipalWrapper<User> {

    public UserBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public UserBaseWrapper(WritableApplicationService appService,
        User wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<User> getWrappedClass() {
        return User.class;
    }

    @Override
   protected User getNewObject() throws Exception {
        User newObject = super.getNewObject();
        newObject.setNeedPwdChange(false);
        newObject.setRecvBulkEmails(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super User> getIdProperty() {
        return UserPeer.ID;
    }

    @Override
    protected List<Property<?, ? super User>> getProperties() {
        List<Property<?, ? super User>> superNames = super.getProperties();
        List<Property<?, ? super User>> all = new ArrayList<Property<?, ? super User>>();
        all.addAll(superNames);
        all.addAll(UserPeer.PROPERTIES);
        return all;
    }

    public String getEmail() {
        return getProperty(UserPeer.EMAIL);
    }

    public void setEmail(String email) {
        String trimmed = email == null ? null : email.trim();
        setProperty(UserPeer.EMAIL, trimmed);
    }

    public Boolean getNeedPwdChange() {
        return getProperty(UserPeer.NEED_PWD_CHANGE);
    }

    public void setNeedPwdChange(Boolean needPwdChange) {
        setProperty(UserPeer.NEED_PWD_CHANGE, needPwdChange);
    }

    public Long getCsmUserId() {
        return getProperty(UserPeer.CSM_USER_ID);
    }

    public void setCsmUserId(Long csmUserId) {
        setProperty(UserPeer.CSM_USER_ID, csmUserId);
    }

    public String getLogin() {
        return getProperty(UserPeer.LOGIN);
    }

    public void setLogin(String login) {
        String trimmed = login == null ? null : login.trim();
        setProperty(UserPeer.LOGIN, trimmed);
    }

    public String getFullName() {
        return getProperty(UserPeer.FULL_NAME);
    }

    public void setFullName(String fullName) {
        String trimmed = fullName == null ? null : fullName.trim();
        setProperty(UserPeer.FULL_NAME, trimmed);
    }

    public Boolean getRecvBulkEmails() {
        return getProperty(UserPeer.RECV_BULK_EMAILS);
    }

    public void setRecvBulkEmails(Boolean recvBulkEmails) {
        setProperty(UserPeer.RECV_BULK_EMAILS, recvBulkEmails);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        boolean notCached = !isPropertyCached(UserPeer.COMMENT_COLLECTION);
        List<CommentWrapper> commentCollection = getWrapperCollection(UserPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        if (notCached) {
            for (CommentBaseWrapper e : commentCollection) {
                e.setUserInternal(this);
            }
        }
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(UserPeer.COMMENT_COLLECTION, commentCollection);
        for (CommentBaseWrapper e : commentCollection) {
            e.setUserInternal(this);
        }
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(UserPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(UserPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(UserPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(UserPeer.COMMENT_COLLECTION, commentCollection);
        for (CommentBaseWrapper e : commentCollection) {
            e.setUserInternal(null);
        }
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(UserPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(UserPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(UserPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(UserPeer.COMMENT_COLLECTION, commentCollection);
        for (CommentBaseWrapper e : commentCollection) {
            e.setUserInternal(null);
        }
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(UserPeer.COMMENT_COLLECTION, commentCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public List<BbGroupWrapper> getGroupCollection(boolean sort) {
        boolean notCached = !isPropertyCached(UserPeer.GROUP_COLLECTION);
        List<BbGroupWrapper> groupCollection = getWrapperCollection(UserPeer.GROUP_COLLECTION, BbGroupWrapper.class, sort);
        if (notCached) {
            for (BbGroupBaseWrapper e : groupCollection) {
                e.addToUserCollectionInternal(Arrays.asList(this));
            }
        }
        return groupCollection;
    }

    public void addToGroupCollection(List<? extends BbGroupBaseWrapper> groupCollection) {
        addToWrapperCollection(UserPeer.GROUP_COLLECTION, groupCollection);
        for (BbGroupBaseWrapper e : groupCollection) {
            e.addToUserCollectionInternal(Arrays.asList(this));
        }
    }

    void addToGroupCollectionInternal(List<? extends BbGroupBaseWrapper> groupCollection) {
        if (isInitialized(UserPeer.GROUP_COLLECTION)) {
            addToWrapperCollection(UserPeer.GROUP_COLLECTION, groupCollection);
        } else {
            getElementQueue().add(UserPeer.GROUP_COLLECTION, groupCollection);
        }
    }

    public void removeFromGroupCollection(List<? extends BbGroupBaseWrapper> groupCollection) {
        removeFromWrapperCollection(UserPeer.GROUP_COLLECTION, groupCollection);
        for (BbGroupBaseWrapper e : groupCollection) {
            e.removeFromUserCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromGroupCollectionInternal(List<? extends BbGroupBaseWrapper> groupCollection) {
        if (isPropertyCached(UserPeer.GROUP_COLLECTION)) {
            removeFromWrapperCollection(UserPeer.GROUP_COLLECTION, groupCollection);
        } else {
            getElementQueue().remove(UserPeer.GROUP_COLLECTION, groupCollection);
        }
    }

    public void removeFromGroupCollectionWithCheck(List<? extends BbGroupBaseWrapper> groupCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(UserPeer.GROUP_COLLECTION, groupCollection);
        for (BbGroupBaseWrapper e : groupCollection) {
            e.removeFromUserCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromGroupCollectionWithCheckInternal(List<? extends BbGroupBaseWrapper> groupCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(UserPeer.GROUP_COLLECTION, groupCollection);
    }

}
