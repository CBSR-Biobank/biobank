/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerPositionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerTypeBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenPositionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ActivityStatusBaseWrapper;
import java.util.Arrays;

public class ContainerBaseWrapper extends ModelWrapper<Container> {

    public ContainerBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerBaseWrapper(WritableApplicationService appService,
        Container wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Container> getWrappedClass() {
        return Container.class;
    }

    @Override
    public Property<Integer, ? super Container> getIdProperty() {
        return ContainerPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Container>> getProperties() {
        return ContainerPeer.PROPERTIES;
    }

    public String getProductBarcode() {
        return getProperty(ContainerPeer.PRODUCT_BARCODE);
    }

    public void setProductBarcode(String productBarcode) {
        String trimmed = productBarcode == null ? null : productBarcode.trim();
        setProperty(ContainerPeer.PRODUCT_BARCODE, trimmed);
    }

    public String getPath() {
        return getProperty(ContainerPeer.PATH);
    }

    public void setPath(String path) {
        String trimmed = path == null ? null : path.trim();
        setProperty(ContainerPeer.PATH, trimmed);
    }

    public String getLabel() {
        return getProperty(ContainerPeer.LABEL);
    }

    public void setLabel(String label) {
        String trimmed = label == null ? null : label.trim();
        setProperty(ContainerPeer.LABEL, trimmed);
    }

    public Double getTemperature() {
        return getProperty(ContainerPeer.TEMPERATURE);
    }

    public void setTemperature(Double temperature) {
        setProperty(ContainerPeer.TEMPERATURE, temperature);
    }

    public SiteWrapper getSite() {
        boolean notCached = !isPropertyCached(ContainerPeer.SITE);
        SiteWrapper site = getWrappedProperty(ContainerPeer.SITE, SiteWrapper.class);
        if (site != null && notCached) ((SiteBaseWrapper) site).addToContainerCollectionInternal(Arrays.asList(this));
        return site;
    }

    public void setSite(SiteBaseWrapper site) {
        if (isInitialized(ContainerPeer.SITE)) {
            SiteBaseWrapper oldSite = getSite();
            if (oldSite != null) oldSite.removeFromContainerCollectionInternal(Arrays.asList(this));
        }
        if (site != null) site.addToContainerCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ContainerPeer.SITE, site);
    }

    void setSiteInternal(SiteBaseWrapper site) {
        setWrappedProperty(ContainerPeer.SITE, site);
    }

    public ContainerPositionWrapper getPosition() {
        boolean notCached = !isPropertyCached(ContainerPeer.POSITION);
        ContainerPositionWrapper position = getWrappedProperty(ContainerPeer.POSITION, ContainerPositionWrapper.class);
        if (position != null && notCached) ((ContainerPositionBaseWrapper) position).setContainerInternal(this);
        return position;
    }

    public void setPosition(ContainerPositionBaseWrapper position) {
        if (isInitialized(ContainerPeer.POSITION)) {
            ContainerPositionBaseWrapper oldPosition = getPosition();
            if (oldPosition != null) oldPosition.setContainerInternal(null);
        }
        if (position != null) position.setContainerInternal(this);
        setWrappedProperty(ContainerPeer.POSITION, position);
    }

    void setPositionInternal(ContainerPositionBaseWrapper position) {
        setWrappedProperty(ContainerPeer.POSITION, position);
    }

    public ContainerTypeWrapper getContainerType() {
        ContainerTypeWrapper containerType = getWrappedProperty(ContainerPeer.CONTAINER_TYPE, ContainerTypeWrapper.class);
        return containerType;
    }

    public void setContainerType(ContainerTypeBaseWrapper containerType) {
        setWrappedProperty(ContainerPeer.CONTAINER_TYPE, containerType);
    }

    void setContainerTypeInternal(ContainerTypeBaseWrapper containerType) {
        setWrappedProperty(ContainerPeer.CONTAINER_TYPE, containerType);
    }

    public List<SpecimenPositionWrapper> getSpecimenPositionCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContainerPeer.SPECIMEN_POSITION_COLLECTION);
        List<SpecimenPositionWrapper> specimenPositionCollection = getWrapperCollection(ContainerPeer.SPECIMEN_POSITION_COLLECTION, SpecimenPositionWrapper.class, sort);
        if (notCached) {
            for (SpecimenPositionBaseWrapper e : specimenPositionCollection) {
                e.setContainerInternal(this);
            }
        }
        return specimenPositionCollection;
    }

    public void addToSpecimenPositionCollection(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) {
        addToWrapperCollection(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        for (SpecimenPositionBaseWrapper e : specimenPositionCollection) {
            e.setContainerInternal(this);
        }
    }

    void addToSpecimenPositionCollectionInternal(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) {
        if (isInitialized(ContainerPeer.SPECIMEN_POSITION_COLLECTION)) {
            addToWrapperCollection(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        } else {
            getElementQueue().add(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        }
    }

    public void removeFromSpecimenPositionCollection(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) {
        removeFromWrapperCollection(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        for (SpecimenPositionBaseWrapper e : specimenPositionCollection) {
            e.setContainerInternal(null);
        }
    }

    void removeFromSpecimenPositionCollectionInternal(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) {
        if (isPropertyCached(ContainerPeer.SPECIMEN_POSITION_COLLECTION)) {
            removeFromWrapperCollection(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        } else {
            getElementQueue().remove(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        }
    }

    public void removeFromSpecimenPositionCollectionWithCheck(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
        for (SpecimenPositionBaseWrapper e : specimenPositionCollection) {
            e.setContainerInternal(null);
        }
    }

    void removeFromSpecimenPositionCollectionWithCheckInternal(List<? extends SpecimenPositionBaseWrapper> specimenPositionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.SPECIMEN_POSITION_COLLECTION, specimenPositionCollection);
    }

    public List<ContainerPositionWrapper> getChildPositionCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContainerPeer.CHILD_POSITION_COLLECTION);
        List<ContainerPositionWrapper> childPositionCollection = getWrapperCollection(ContainerPeer.CHILD_POSITION_COLLECTION, ContainerPositionWrapper.class, sort);
        if (notCached) {
            for (ContainerPositionBaseWrapper e : childPositionCollection) {
                e.setParentContainerInternal(this);
            }
        }
        return childPositionCollection;
    }

    public void addToChildPositionCollection(List<? extends ContainerPositionBaseWrapper> childPositionCollection) {
        addToWrapperCollection(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        for (ContainerPositionBaseWrapper e : childPositionCollection) {
            e.setParentContainerInternal(this);
        }
    }

    void addToChildPositionCollectionInternal(List<? extends ContainerPositionBaseWrapper> childPositionCollection) {
        if (isInitialized(ContainerPeer.CHILD_POSITION_COLLECTION)) {
            addToWrapperCollection(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        } else {
            getElementQueue().add(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        }
    }

    public void removeFromChildPositionCollection(List<? extends ContainerPositionBaseWrapper> childPositionCollection) {
        removeFromWrapperCollection(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        for (ContainerPositionBaseWrapper e : childPositionCollection) {
            e.setParentContainerInternal(null);
        }
    }

    void removeFromChildPositionCollectionInternal(List<? extends ContainerPositionBaseWrapper> childPositionCollection) {
        if (isPropertyCached(ContainerPeer.CHILD_POSITION_COLLECTION)) {
            removeFromWrapperCollection(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        } else {
            getElementQueue().remove(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        }
    }

    public void removeFromChildPositionCollectionWithCheck(List<? extends ContainerPositionBaseWrapper> childPositionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
        for (ContainerPositionBaseWrapper e : childPositionCollection) {
            e.setParentContainerInternal(null);
        }
    }

    void removeFromChildPositionCollectionWithCheckInternal(List<? extends ContainerPositionBaseWrapper> childPositionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.CHILD_POSITION_COLLECTION, childPositionCollection);
    }

    public ContainerWrapper getTopContainer() {
        ContainerWrapper topContainer = getWrappedProperty(ContainerPeer.TOP_CONTAINER, ContainerWrapper.class);
        return topContainer;
    }

    public void setTopContainer(ContainerBaseWrapper topContainer) {
        setWrappedProperty(ContainerPeer.TOP_CONTAINER, topContainer);
    }

    void setTopContainerInternal(ContainerBaseWrapper topContainer) {
        setWrappedProperty(ContainerPeer.TOP_CONTAINER, topContainer);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(ContainerPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(ContainerPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(ContainerPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(ContainerPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(ContainerPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(ContainerPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(ContainerPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(ContainerPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(ContainerPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerPeer.COMMENT_COLLECTION, commentCollection);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatusWrapper activityStatus = getWrappedProperty(ContainerPeer.ACTIVITY_STATUS, ActivityStatusWrapper.class);
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusBaseWrapper activityStatus) {
        setWrappedProperty(ContainerPeer.ACTIVITY_STATUS, activityStatus);
    }

    void setActivityStatusInternal(ActivityStatusBaseWrapper activityStatus) {
        setWrappedProperty(ContainerPeer.ACTIVITY_STATUS, activityStatus);
    }

}
