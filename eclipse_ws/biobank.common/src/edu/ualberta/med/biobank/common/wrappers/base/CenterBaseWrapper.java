/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.AddressWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.AddressBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;

public abstract class CenterBaseWrapper<E extends Center> extends ModelWrapper<E> {

    public CenterBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CenterBaseWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Property<Integer, ? super E> getIdProperty() {
        return CenterPeer.ID;
    }

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        return new ArrayList<Property<?, ? super E>>(CenterPeer.PROPERTIES);
    }

    public String getName() {
        return getProperty(CenterPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(CenterPeer.NAME, trimmed);
    }

    public String getNameShort() {
        return getProperty(CenterPeer.NAME_SHORT);
    }

    public void setNameShort(String nameShort) {
        String trimmed = nameShort == null ? null : nameShort.trim();
        setProperty(CenterPeer.NAME_SHORT, trimmed);
    }

    public List<DispatchWrapper> getDstDispatchCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.DST_DISPATCHES);
        List<DispatchWrapper> dstDispatchCollection = getWrapperCollection(CenterPeer.DST_DISPATCHES, DispatchWrapper.class, sort);
        if (notCached) {
            for (DispatchBaseWrapper e : dstDispatchCollection) {
                e.setReceiverCenterInternal(this);
            }
        }
        return dstDispatchCollection;
    }

    public void addToDstDispatchCollection(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        addToWrapperCollection(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(this);
        }
    }

    void addToDstDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        if (isInitialized(CenterPeer.DST_DISPATCHES)) {
            addToWrapperCollection(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        } else {
            getElementQueue().add(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        }
    }

    public void removeFromDstDispatchCollection(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        removeFromWrapperCollection(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(null);
        }
    }

    void removeFromDstDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        if (isPropertyCached(CenterPeer.DST_DISPATCHES)) {
            removeFromWrapperCollection(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        } else {
            getElementQueue().remove(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        }
    }

    public void removeFromDstDispatchCollectionWithCheck(List<? extends DispatchBaseWrapper> dstDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(null);
        }
    }

    void removeFromDstDispatchCollectionWithCheckInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.DST_DISPATCHES, dstDispatchCollection);
    }

    public List<DispatchWrapper> getSrcDispatchCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.SRC_DISPATCHES);
        List<DispatchWrapper> srcDispatchCollection = getWrapperCollection(CenterPeer.SRC_DISPATCHES, DispatchWrapper.class, sort);
        if (notCached) {
            for (DispatchBaseWrapper e : srcDispatchCollection) {
                e.setSenderCenterInternal(this);
            }
        }
        return srcDispatchCollection;
    }

    public void addToSrcDispatchCollection(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        addToWrapperCollection(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(this);
        }
    }

    void addToSrcDispatchCollectionInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        if (isInitialized(CenterPeer.SRC_DISPATCHES)) {
            addToWrapperCollection(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        } else {
            getElementQueue().add(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        }
    }

    public void removeFromSrcDispatchCollection(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        removeFromWrapperCollection(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(null);
        }
    }

    void removeFromSrcDispatchCollectionInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        if (isPropertyCached(CenterPeer.SRC_DISPATCHES)) {
            removeFromWrapperCollection(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        } else {
            getElementQueue().remove(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        }
    }

    public void removeFromSrcDispatchCollectionWithCheck(List<? extends DispatchBaseWrapper> srcDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(null);
        }
    }

    void removeFromSrcDispatchCollectionWithCheckInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.SRC_DISPATCHES, srcDispatchCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(CenterPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(CenterPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(CenterPeer.COMMENTS)) {
            addToWrapperCollection(CenterPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(CenterPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(CenterPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(CenterPeer.COMMENTS)) {
            removeFromWrapperCollection(CenterPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(CenterPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.COMMENTS, commentCollection);
    }

    public AddressWrapper getAddress() {
        AddressWrapper address = getWrappedProperty(CenterPeer.ADDRESS, AddressWrapper.class);
        return address;
    }

    public void setAddress(AddressBaseWrapper address) {
        setWrappedProperty(CenterPeer.ADDRESS, address);
    }

    void setAddressInternal(AddressBaseWrapper address) {
        setWrappedProperty(CenterPeer.ADDRESS, address);
    }

    public List<ProcessingEventWrapper> getProcessingEventCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.PROCESSING_EVENTS);
        List<ProcessingEventWrapper> processingEventCollection = getWrapperCollection(CenterPeer.PROCESSING_EVENTS, ProcessingEventWrapper.class, sort);
        if (notCached) {
            for (ProcessingEventBaseWrapper e : processingEventCollection) {
                e.setCenterInternal(this);
            }
        }
        return processingEventCollection;
    }

    public void addToProcessingEventCollection(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        addToWrapperCollection(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToProcessingEventCollectionInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        if (isInitialized(CenterPeer.PROCESSING_EVENTS)) {
            addToWrapperCollection(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        } else {
            getElementQueue().add(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        }
    }

    public void removeFromProcessingEventCollection(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        removeFromWrapperCollection(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromProcessingEventCollectionInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        if (isPropertyCached(CenterPeer.PROCESSING_EVENTS)) {
            removeFromWrapperCollection(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        } else {
            getElementQueue().remove(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        }
    }

    public void removeFromProcessingEventCollectionWithCheck(List<? extends ProcessingEventBaseWrapper> processingEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromProcessingEventCollectionWithCheckInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.PROCESSING_EVENTS, processingEventCollection);
    }

    public List<MembershipWrapper> getMembershipCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.MEMBERSHIPS);
        List<MembershipWrapper> membershipCollection = getWrapperCollection(CenterPeer.MEMBERSHIPS, MembershipWrapper.class, sort);
        if (notCached) {
            for (MembershipBaseWrapper e : membershipCollection) {
                e.setCenterInternal(this);
            }
        }
        return membershipCollection;
    }

    public void addToMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        addToWrapperCollection(CenterPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isInitialized(CenterPeer.MEMBERSHIPS)) {
            addToWrapperCollection(CenterPeer.MEMBERSHIPS, membershipCollection);
        } else {
            getElementQueue().add(CenterPeer.MEMBERSHIPS, membershipCollection);
        }
    }

    public void removeFromMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        removeFromWrapperCollection(CenterPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isPropertyCached(CenterPeer.MEMBERSHIPS)) {
            removeFromWrapperCollection(CenterPeer.MEMBERSHIPS, membershipCollection);
        } else {
            getElementQueue().remove(CenterPeer.MEMBERSHIPS, membershipCollection);
        }
    }

    public void removeFromMembershipCollectionWithCheck(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.MEMBERSHIPS, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromMembershipCollectionWithCheckInternal(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.MEMBERSHIPS, membershipCollection);
    }

    public List<OriginInfoWrapper> getOriginInfoCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.ORIGIN_INFOS);
        List<OriginInfoWrapper> originInfoCollection = getWrapperCollection(CenterPeer.ORIGIN_INFOS, OriginInfoWrapper.class, sort);
        if (notCached) {
            for (OriginInfoBaseWrapper e : originInfoCollection) {
                e.setCenterInternal(this);
            }
        }
        return originInfoCollection;
    }

    public void addToOriginInfoCollection(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        addToWrapperCollection(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToOriginInfoCollectionInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        if (isInitialized(CenterPeer.ORIGIN_INFOS)) {
            addToWrapperCollection(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        } else {
            getElementQueue().add(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        }
    }

    public void removeFromOriginInfoCollection(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        removeFromWrapperCollection(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromOriginInfoCollectionInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        if (isPropertyCached(CenterPeer.ORIGIN_INFOS)) {
            removeFromWrapperCollection(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        } else {
            getElementQueue().remove(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        }
    }

    public void removeFromOriginInfoCollectionWithCheck(List<? extends OriginInfoBaseWrapper> originInfoCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.ORIGIN_INFOS, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromOriginInfoCollectionWithCheckInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.ORIGIN_INFOS, originInfoCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }
}
