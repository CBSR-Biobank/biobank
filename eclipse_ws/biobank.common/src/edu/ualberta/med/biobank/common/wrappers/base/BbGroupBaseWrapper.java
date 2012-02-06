/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import java.util.Arrays;

public abstract class BbGroupBaseWrapper extends PrincipalWrapper<BbGroup> {

    public BbGroupBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public BbGroupBaseWrapper(WritableApplicationService appService,
        BbGroup wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<BbGroup> getWrappedClass() {
        return BbGroup.class;
    }

    @Override
    public Property<Integer, ? super BbGroup> getIdProperty() {
        return BbGroupPeer.ID;
    }

    @Override
    protected List<Property<?, ? super BbGroup>> getProperties() {
        List<Property<?, ? super BbGroup>> superNames = super.getProperties();
        List<Property<?, ? super BbGroup>> all = new ArrayList<Property<?, ? super BbGroup>>();
        all.addAll(superNames);
        all.addAll(BbGroupPeer.PROPERTIES);
        return all;
    }

    public String getDescription() {
        return getProperty(BbGroupPeer.DESCRIPTION);
    }

    public void setDescription(String description) {
        String trimmed = description == null ? null : description.trim();
        setProperty(BbGroupPeer.DESCRIPTION, trimmed);
    }

    public String getName() {
        return getProperty(BbGroupPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(BbGroupPeer.NAME, trimmed);
    }

    public List<UserWrapper> getUserCollection(boolean sort) {
        boolean notCached = !isPropertyCached(BbGroupPeer.USER_COLLECTION);
        List<UserWrapper> userCollection = getWrapperCollection(BbGroupPeer.USER_COLLECTION, UserWrapper.class, sort);
        if (notCached) {
            for (UserBaseWrapper e : userCollection) {
                e.addToGroupCollectionInternal(Arrays.asList(this));
            }
        }
        return userCollection;
    }

    public void addToUserCollection(List<? extends UserBaseWrapper> userCollection) {
        addToWrapperCollection(BbGroupPeer.USER_COLLECTION, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.addToGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void addToUserCollectionInternal(List<? extends UserBaseWrapper> userCollection) {
        if (isInitialized(BbGroupPeer.USER_COLLECTION)) {
            addToWrapperCollection(BbGroupPeer.USER_COLLECTION, userCollection);
        } else {
            getElementQueue().add(BbGroupPeer.USER_COLLECTION, userCollection);
        }
    }

    public void removeFromUserCollection(List<? extends UserBaseWrapper> userCollection) {
        removeFromWrapperCollection(BbGroupPeer.USER_COLLECTION, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.removeFromGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromUserCollectionInternal(List<? extends UserBaseWrapper> userCollection) {
        if (isPropertyCached(BbGroupPeer.USER_COLLECTION)) {
            removeFromWrapperCollection(BbGroupPeer.USER_COLLECTION, userCollection);
        } else {
            getElementQueue().remove(BbGroupPeer.USER_COLLECTION, userCollection);
        }
    }

    public void removeFromUserCollectionWithCheck(List<? extends UserBaseWrapper> userCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(BbGroupPeer.USER_COLLECTION, userCollection);
        for (UserBaseWrapper e : userCollection) {
            e.removeFromGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromUserCollectionWithCheckInternal(List<? extends UserBaseWrapper> userCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(BbGroupPeer.USER_COLLECTION, userCollection);
    }

}
