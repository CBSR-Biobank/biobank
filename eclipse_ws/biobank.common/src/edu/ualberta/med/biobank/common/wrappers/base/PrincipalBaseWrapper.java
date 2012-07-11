/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
        boolean notCached = !isPropertyCached(PrincipalPeer.MEMBERSHIPS);
        List<MembershipWrapper> membershipCollection = getWrapperCollection(PrincipalPeer.MEMBERSHIPS, MembershipWrapper.class, sort);
        if (notCached) {
            for (MembershipBaseWrapper e : membershipCollection) {
                e.setPrincipalInternal(this);
            }
        }
        return membershipCollection;
    }

    public void addToMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        addToWrapperCollection(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(this);
        }
    }

    void addToMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isInitialized(PrincipalPeer.MEMBERSHIPS)) {
            addToWrapperCollection(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        } else {
            getElementQueue().add(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        }
    }

    public void removeFromMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        removeFromWrapperCollection(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(null);
        }
    }

    void removeFromMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isPropertyCached(PrincipalPeer.MEMBERSHIPS)) {
            removeFromWrapperCollection(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        } else {
            getElementQueue().remove(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        }
    }

    public void removeFromMembershipCollectionWithCheck(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PrincipalPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setPrincipalInternal(null);
        }
    }

    void removeFromMembershipCollectionWithCheckInternal(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PrincipalPeer.MEMBERSHIPS, membershipCollection);
    }

}
