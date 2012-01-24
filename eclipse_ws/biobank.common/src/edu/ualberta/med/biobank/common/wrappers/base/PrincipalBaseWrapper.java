/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;

public abstract class PrincipalBaseWrapper<E extends Principal> extends ModelWrapper<E> {

    public PrincipalBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PrincipalBaseWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Property<Integer, ? super E> getIdProperty() {
        return PrincipalPeer.ID;
    }

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        return new ArrayList<Property<?, ? super E>>(PrincipalPeer.PROPERTIES);
    }

    public List<MembershipWrapper> getMembershipCollection(boolean sort) {
        boolean notCached = !isPropertyCached(PrincipalPeer.MEMBERSHIP_COLLECTION);
        List<MembershipWrapper> membershipCollection = getWrapperCollection(PrincipalPeer.MEMBERSHIP_COLLECTION, MembershipWrapper.class, sort);
        if (notCached) {
            for (MembershipBaseWrapper e : membershipCollection) {
                e.setPrincipalInternal(this);
            }
        }
        return membershipCollection;
    }

    public void addToMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        addToWrapperCollection(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(this);
        }
    }

    void addToMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isInitialized(PrincipalPeer.MEMBERSHIP_COLLECTION)) {
            addToWrapperCollection(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().add(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        removeFromWrapperCollection(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(null);
        }
    }

    void removeFromMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isPropertyCached(PrincipalPeer.MEMBERSHIP_COLLECTION)) {
            removeFromWrapperCollection(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().remove(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollectionWithCheck(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(null);
        }
    }

    void removeFromMembershipCollectionWithCheckInternal(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PrincipalPeer.MEMBERSHIP_COLLECTION, membershipCollection);
    }

}
