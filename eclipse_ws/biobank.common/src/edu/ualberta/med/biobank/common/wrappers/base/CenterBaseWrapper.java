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
        boolean notCached = !isPropertyCached(CenterPeer.DST_DISPATCH_COLLECTION);
        List<DispatchWrapper> dstDispatchCollection = getWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, DispatchWrapper.class, sort);
        if (notCached) {
            for (DispatchBaseWrapper e : dstDispatchCollection) {
                e.setReceiverCenterInternal(this);
            }
        }
        return dstDispatchCollection;
    }

    public void addToDstDispatchCollection(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        addToWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(this);
        }
    }

    void addToDstDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        if (isInitialized(CenterPeer.DST_DISPATCH_COLLECTION)) {
            addToWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        } else {
            getElementQueue().add(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        }
    }

    public void removeFromDstDispatchCollection(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        removeFromWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(null);
        }
    }

    void removeFromDstDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) {
        if (isPropertyCached(CenterPeer.DST_DISPATCH_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        } else {
            getElementQueue().remove(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        }
    }

    public void removeFromDstDispatchCollectionWithCheck(List<? extends DispatchBaseWrapper> dstDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
        for (DispatchBaseWrapper e : dstDispatchCollection) {
            e.setReceiverCenterInternal(null);
        }
    }

    void removeFromDstDispatchCollectionWithCheckInternal(List<? extends DispatchBaseWrapper> dstDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.DST_DISPATCH_COLLECTION, dstDispatchCollection);
    }

    public List<DispatchWrapper> getSrcDispatchCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.SRC_DISPATCH_COLLECTION);
        List<DispatchWrapper> srcDispatchCollection = getWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, DispatchWrapper.class, sort);
        if (notCached) {
            for (DispatchBaseWrapper e : srcDispatchCollection) {
                e.setSenderCenterInternal(this);
            }
        }
        return srcDispatchCollection;
    }

    public void addToSrcDispatchCollection(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        addToWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(this);
        }
    }

    void addToSrcDispatchCollectionInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        if (isInitialized(CenterPeer.SRC_DISPATCH_COLLECTION)) {
            addToWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        } else {
            getElementQueue().add(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        }
    }

    public void removeFromSrcDispatchCollection(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        removeFromWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(null);
        }
    }

    void removeFromSrcDispatchCollectionInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) {
        if (isPropertyCached(CenterPeer.SRC_DISPATCH_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        } else {
            getElementQueue().remove(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        }
    }

    public void removeFromSrcDispatchCollectionWithCheck(List<? extends DispatchBaseWrapper> srcDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
        for (DispatchBaseWrapper e : srcDispatchCollection) {
            e.setSenderCenterInternal(null);
        }
    }

    void removeFromSrcDispatchCollectionWithCheckInternal(List<? extends DispatchBaseWrapper> srcDispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.SRC_DISPATCH_COLLECTION, srcDispatchCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(CenterPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(CenterPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(CenterPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(CenterPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(CenterPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(CenterPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(CenterPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(CenterPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.COMMENT_COLLECTION, commentCollection);
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
        boolean notCached = !isPropertyCached(CenterPeer.PROCESSING_EVENT_COLLECTION);
        List<ProcessingEventWrapper> processingEventCollection = getWrapperCollection(CenterPeer.PROCESSING_EVENT_COLLECTION, ProcessingEventWrapper.class, sort);
        if (notCached) {
            for (ProcessingEventBaseWrapper e : processingEventCollection) {
                e.setCenterInternal(this);
            }
        }
        return processingEventCollection;
    }

    public void addToProcessingEventCollection(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        addToWrapperCollection(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToProcessingEventCollectionInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        if (isInitialized(CenterPeer.PROCESSING_EVENT_COLLECTION)) {
            addToWrapperCollection(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        } else {
            getElementQueue().add(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        }
    }

    public void removeFromProcessingEventCollection(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        removeFromWrapperCollection(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromProcessingEventCollectionInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) {
        if (isPropertyCached(CenterPeer.PROCESSING_EVENT_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        } else {
            getElementQueue().remove(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        }
    }

    public void removeFromProcessingEventCollectionWithCheck(List<? extends ProcessingEventBaseWrapper> processingEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
        for (ProcessingEventBaseWrapper e : processingEventCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromProcessingEventCollectionWithCheckInternal(List<? extends ProcessingEventBaseWrapper> processingEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.PROCESSING_EVENT_COLLECTION, processingEventCollection);
    }

    public List<MembershipWrapper> getMembershipCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.MEMBERSHIP_COLLECTION);
        List<MembershipWrapper> membershipCollection = getWrapperCollection(CenterPeer.MEMBERSHIP_COLLECTION, MembershipWrapper.class, sort);
        if (notCached) {
            for (MembershipBaseWrapper e : membershipCollection) {
                e.setCenterInternal(this);
            }
        }
        return membershipCollection;
    }

    public void addToMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        addToWrapperCollection(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isInitialized(CenterPeer.MEMBERSHIP_COLLECTION)) {
            addToWrapperCollection(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().add(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        removeFromWrapperCollection(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isPropertyCached(CenterPeer.MEMBERSHIP_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().remove(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollectionWithCheck(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromMembershipCollectionWithCheckInternal(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.MEMBERSHIP_COLLECTION, membershipCollection);
    }

    public List<OriginInfoWrapper> getOriginInfoCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CenterPeer.ORIGIN_INFO_COLLECTION);
        List<OriginInfoWrapper> originInfoCollection = getWrapperCollection(CenterPeer.ORIGIN_INFO_COLLECTION, OriginInfoWrapper.class, sort);
        if (notCached) {
            for (OriginInfoBaseWrapper e : originInfoCollection) {
                e.setCenterInternal(this);
            }
        }
        return originInfoCollection;
    }

    public void addToOriginInfoCollection(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        addToWrapperCollection(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(this);
        }
    }

    void addToOriginInfoCollectionInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        if (isInitialized(CenterPeer.ORIGIN_INFO_COLLECTION)) {
            addToWrapperCollection(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        } else {
            getElementQueue().add(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        }
    }

    public void removeFromOriginInfoCollection(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        removeFromWrapperCollection(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromOriginInfoCollectionInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) {
        if (isPropertyCached(CenterPeer.ORIGIN_INFO_COLLECTION)) {
            removeFromWrapperCollection(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        } else {
            getElementQueue().remove(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        }
    }

    public void removeFromOriginInfoCollectionWithCheck(List<? extends OriginInfoBaseWrapper> originInfoCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
        for (OriginInfoBaseWrapper e : originInfoCollection) {
            e.setCenterInternal(null);
        }
    }

    void removeFromOriginInfoCollectionWithCheckInternal(List<? extends OriginInfoBaseWrapper> originInfoCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CenterPeer.ORIGIN_INFO_COLLECTION, originInfoCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }
}
