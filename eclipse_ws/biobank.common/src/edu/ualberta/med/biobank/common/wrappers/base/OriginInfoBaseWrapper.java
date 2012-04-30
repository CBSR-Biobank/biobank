/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ShipmentInfoBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import java.util.Arrays;

public class OriginInfoBaseWrapper extends ModelWrapper<OriginInfo> {

    public OriginInfoBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OriginInfoBaseWrapper(WritableApplicationService appService,
        OriginInfo wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<OriginInfo> getWrappedClass() {
        return OriginInfo.class;
    }

    @Override
    public Property<Integer, ? super OriginInfo> getIdProperty() {
        return OriginInfoPeer.ID;
    }

    @Override
    protected List<Property<?, ? super OriginInfo>> getProperties() {
        return OriginInfoPeer.PROPERTIES;
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getCenter() {
        boolean notCached = !isPropertyCached(OriginInfoPeer.CENTER);
        CenterWrapper <?>center = getWrappedProperty(OriginInfoPeer.CENTER, CenterWrapper.class);
        if (center != null && notCached) ((CenterBaseWrapper<?>) center).addToOriginInfoCollectionInternal(Arrays.asList(this));
        return center;
    }

    public void setCenter(CenterBaseWrapper<?> center) {
        if (isInitialized(OriginInfoPeer.CENTER)) {
            CenterBaseWrapper<?> oldCenter = getCenter();
            if (oldCenter != null) oldCenter.removeFromOriginInfoCollectionInternal(Arrays.asList(this));
        }
        if (center != null) center.addToOriginInfoCollectionInternal(Arrays.asList(this));
        setWrappedProperty(OriginInfoPeer.CENTER, center);
    }

    void setCenterInternal(CenterBaseWrapper<?> center) {
        setWrappedProperty(OriginInfoPeer.CENTER, center);
    }

    public ShipmentInfoWrapper getShipmentInfo() {
        ShipmentInfoWrapper shipmentInfo = getWrappedProperty(OriginInfoPeer.SHIPMENT_INFO, ShipmentInfoWrapper.class);
        return shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfoBaseWrapper shipmentInfo) {
        setWrappedProperty(OriginInfoPeer.SHIPMENT_INFO, shipmentInfo);
    }

    void setShipmentInfoInternal(ShipmentInfoBaseWrapper shipmentInfo) {
        setWrappedProperty(OriginInfoPeer.SHIPMENT_INFO, shipmentInfo);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(OriginInfoPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(OriginInfoPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(OriginInfoPeer.COMMENTS)) {
            addToWrapperCollection(OriginInfoPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(OriginInfoPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(OriginInfoPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(OriginInfoPeer.COMMENTS)) {
            removeFromWrapperCollection(OriginInfoPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(OriginInfoPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(OriginInfoPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(OriginInfoPeer.COMMENTS, commentCollection);
    }

    public SiteWrapper getReceiverSite() {
        SiteWrapper receiverSite = getWrappedProperty(OriginInfoPeer.RECEIVER_SITE, SiteWrapper.class);
        return receiverSite;
    }

    public void setReceiverSite(SiteBaseWrapper receiverSite) {
        setWrappedProperty(OriginInfoPeer.RECEIVER_SITE, receiverSite);
    }

    void setReceiverSiteInternal(SiteBaseWrapper receiverSite) {
        setWrappedProperty(OriginInfoPeer.RECEIVER_SITE, receiverSite);
    }

    public List<SpecimenWrapper> getSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(OriginInfoPeer.SPECIMENS);
        List<SpecimenWrapper> specimenCollection = getWrapperCollection(OriginInfoPeer.SPECIMENS, SpecimenWrapper.class, sort);
        if (notCached) {
            for (SpecimenBaseWrapper e : specimenCollection) {
                e.setOriginInfoInternal(this);
            }
        }
        return specimenCollection;
    }

    public void addToSpecimenCollection(List<? extends SpecimenBaseWrapper> specimenCollection) {
        addToWrapperCollection(OriginInfoPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setOriginInfoInternal(this);
        }
    }

    void addToSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> specimenCollection) {
        if (isInitialized(OriginInfoPeer.SPECIMENS)) {
            addToWrapperCollection(OriginInfoPeer.SPECIMENS, specimenCollection);
        } else {
            getElementQueue().add(OriginInfoPeer.SPECIMENS, specimenCollection);
        }
    }

    public void removeFromSpecimenCollection(List<? extends SpecimenBaseWrapper> specimenCollection) {
        removeFromWrapperCollection(OriginInfoPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setOriginInfoInternal(null);
        }
    }

    void removeFromSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> specimenCollection) {
        if (isPropertyCached(OriginInfoPeer.SPECIMENS)) {
            removeFromWrapperCollection(OriginInfoPeer.SPECIMENS, specimenCollection);
        } else {
            getElementQueue().remove(OriginInfoPeer.SPECIMENS, specimenCollection);
        }
    }

    public void removeFromSpecimenCollectionWithCheck(List<? extends SpecimenBaseWrapper> specimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(OriginInfoPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setOriginInfoInternal(null);
        }
    }

    void removeFromSpecimenCollectionWithCheckInternal(List<? extends SpecimenBaseWrapper> specimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(OriginInfoPeer.SPECIMENS, specimenCollection);
    }

}
