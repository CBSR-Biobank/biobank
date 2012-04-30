/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.CommentPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import java.util.Arrays;

public class CommentBaseWrapper extends ModelWrapper<Comment> {

    public CommentBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CommentBaseWrapper(WritableApplicationService appService,
        Comment wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Comment> getWrappedClass() {
        return Comment.class;
    }

    @Override
    public Property<Integer, ? super Comment> getIdProperty() {
        return CommentPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Comment>> getProperties() {
        return CommentPeer.PROPERTIES;
    }

    public String getMessage() {
        return getProperty(CommentPeer.MESSAGE);
    }

    public void setMessage(String message) {
        String trimmed = message == null ? null : message.trim();
        setProperty(CommentPeer.MESSAGE, trimmed);
    }

    public Date getCreatedAt() {
        return getProperty(CommentPeer.CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        setProperty(CommentPeer.CREATED_AT, createdAt);
    }

    public UserWrapper getUser() {
        boolean notCached = !isPropertyCached(CommentPeer.USER);
        UserWrapper user = getWrappedProperty(CommentPeer.USER, UserWrapper.class);
        if (user != null && notCached) ((UserBaseWrapper) user).addToCommentCollectionInternal(Arrays.asList(this));
        return user;
    }

    public void setUser(UserBaseWrapper user) {
        if (isInitialized(CommentPeer.USER)) {
            UserBaseWrapper oldUser = getUser();
            if (oldUser != null) oldUser.removeFromCommentCollectionInternal(Arrays.asList(this));
        }
        if (user != null) user.addToCommentCollectionInternal(Arrays.asList(this));
        setWrappedProperty(CommentPeer.USER, user);
    }

    void setUserInternal(UserBaseWrapper user) {
        setWrappedProperty(CommentPeer.USER, user);
    }

}
