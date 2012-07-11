/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.GroupPeer;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.Group;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class GroupBaseWrapper extends PrincipalWrapper<Group> {

    public GroupBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public GroupBaseWrapper(WritableApplicationService appService,
        Group wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Group> getWrappedClass() {
        return Group.class;
    }

    @Override
    public Property<Integer, ? super Group> getIdProperty() {
        return GroupPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Group>> getProperties() {
        List<Property<?, ? super Group>> superNames = super.getProperties();
        List<Property<?, ? super Group>> all = new ArrayList<Property<?, ? super Group>>();
        all.addAll(superNames);
        all.addAll(GroupPeer.PROPERTIES);
        return all;
    }

    public String getDescription() {
        return getProperty(GroupPeer.DESCRIPTION);
    }

    public void setDescription(String description) {
        String trimmed = description == null ? null : description.trim();
        setProperty(GroupPeer.DESCRIPTION, trimmed);
    }

    public String getName() {
        return getProperty(GroupPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(GroupPeer.NAME, trimmed);
    }

    public List<UserWrapper> getUserCollection(boolean sort) {
        boolean notCached = !isPropertyCached(GroupPeer.USERS);
        List<UserWrapper> userCollection = getWrapperCollection(GroupPeer.USERS, UserWrapper.class, sort);
        if (notCached) {
            for (UserBaseWrapper e : userCollection) {
                e.addToGroupCollectionInternal(Arrays.asList(this));
            }
        }
        return userCollection;
    }

    public void addToUserCollection(List<? extends UserBaseWrapper> userCollection) {
        addToWrapperCollection(GroupPeer.USERS, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.addToGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void addToUserCollectionInternal(List<? extends UserBaseWrapper> userCollection) {
        if (isInitialized(GroupPeer.USERS)) {
            addToWrapperCollection(GroupPeer.USERS, userCollection);
        } else {
            getElementQueue().add(GroupPeer.USERS, userCollection);
        }
    }

    public void removeFromUserCollection(List<? extends UserBaseWrapper> userCollection) {
        removeFromWrapperCollection(GroupPeer.USERS, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.removeFromGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromUserCollectionInternal(List<? extends UserBaseWrapper> userCollection) {
        if (isPropertyCached(GroupPeer.USERS)) {
            removeFromWrapperCollection(GroupPeer.USERS, userCollection);
        } else {
            getElementQueue().remove(GroupPeer.USERS, userCollection);
        }
    }

    public void removeFromUserCollectionWithCheck(List<? extends UserBaseWrapper> userCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(GroupPeer.USERS, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.removeFromGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromUserCollectionWithCheckInternal(List<? extends UserBaseWrapper> userCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(GroupPeer.USERS, userCollection);
    }

}
