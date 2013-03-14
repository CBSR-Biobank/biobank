/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.type.LabelingLayout;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerTypeBaseWrapper extends ModelWrapper<ContainerType> {

    public ContainerTypeBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerTypeBaseWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

    @Override
    protected ContainerType getNewObject() throws Exception {
        ContainerType newObject = super.getNewObject();
        newObject.setTopLevel(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super ContainerType> getIdProperty() {
        return ContainerTypePeer.ID;
    }

    @Override
    protected List<Property<?, ? super ContainerType>> getProperties() {
        return ContainerTypePeer.PROPERTIES;
    }

    public Double getDefaultTemperature() {
        return getProperty(ContainerTypePeer.DEFAULT_TEMPERATURE);
    }

    public void setDefaultTemperature(Double defaultTemperature) {
        setProperty(ContainerTypePeer.DEFAULT_TEMPERATURE, defaultTemperature);
    }

    public String getName() {
        return getProperty(ContainerTypePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ContainerTypePeer.NAME, trimmed);
    }

    public Boolean getTopLevel() {
        return getProperty(ContainerTypePeer.TOP_LEVEL);
    }

    public void setTopLevel(Boolean topLevel) {
        setProperty(ContainerTypePeer.TOP_LEVEL, topLevel);
    }

    public String getNameShort() {
        return getProperty(ContainerTypePeer.NAME_SHORT);
    }

    public void setNameShort(String nameShort) {
        String trimmed = nameShort == null ? null : nameShort.trim();
        setProperty(ContainerTypePeer.NAME_SHORT, trimmed);
    }

    public SiteWrapper getSite() {
        boolean notCached = !isPropertyCached(ContainerTypePeer.SITE);
        SiteWrapper site = getWrappedProperty(ContainerTypePeer.SITE, SiteWrapper.class);
        if (site != null && notCached)
            ((SiteBaseWrapper) site).addToContainerTypeCollectionInternal(Arrays.asList(this));
        return site;
    }

    public void setSite(SiteBaseWrapper site) {
        if (isInitialized(ContainerTypePeer.SITE)) {
            SiteBaseWrapper oldSite = getSite();
            if (oldSite != null)
                oldSite.removeFromContainerTypeCollectionInternal(Arrays.asList(this));
        }
        if (site != null) site.addToContainerTypeCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ContainerTypePeer.SITE, site);
    }

    void setSiteInternal(SiteBaseWrapper site) {
        setWrappedProperty(ContainerTypePeer.SITE, site);
    }

    public ContainerLabelingSchemeWrapper getChildLabelingScheme() {
        ContainerLabelingSchemeWrapper childLabelingScheme = getWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME, ContainerLabelingSchemeWrapper.class);
        return childLabelingScheme;
    }

    public void setChildLabelingScheme(ContainerLabelingSchemeBaseWrapper childLabelingScheme) {
        setWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME, childLabelingScheme);
    }

    void setChildLabelingSchemeInternal(ContainerLabelingSchemeBaseWrapper childLabelingScheme) {
        setWrappedProperty(ContainerTypePeer.CHILD_LABELING_SCHEME, childLabelingScheme);
    }

    public LabelingLayout getLabelingLayout() {
        return getProperty(ContainerTypePeer.LABELING_LAYOUT);
    }

    public void setLabelingLayout(LabelingLayout labelingLayout) {
        setProperty(ContainerTypePeer.LABELING_LAYOUT, labelingLayout);
    }

    public List<SpecimenTypeWrapper> getSpecimenTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContainerTypePeer.SPECIMEN_TYPES);
        List<SpecimenTypeWrapper> specimenTypeCollection = getWrapperCollection(ContainerTypePeer.SPECIMEN_TYPES, SpecimenTypeWrapper.class, sort);
        if (notCached) {
            for (SpecimenTypeBaseWrapper e : specimenTypeCollection) {
                e.addToContainerTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return specimenTypeCollection;
    }

    public void addToSpecimenTypeCollection(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection) {
        addToWrapperCollection(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : specimenTypeCollection) {
            e.addToContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToSpecimenTypeCollectionInternal(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection) {
        if (isInitialized(ContainerTypePeer.SPECIMEN_TYPES)) {
            addToWrapperCollection(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        } else {
            getElementQueue().add(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        }
    }

    public void removeFromSpecimenTypeCollection(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection) {
        removeFromWrapperCollection(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : specimenTypeCollection) {
            e.removeFromContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSpecimenTypeCollectionInternal(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection) {
        if (isPropertyCached(ContainerTypePeer.SPECIMEN_TYPES)) {
            removeFromWrapperCollection(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        } else {
            getElementQueue().remove(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        }
    }

    public void removeFromSpecimenTypeCollectionWithCheck(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : specimenTypeCollection) {
            e.removeFromContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSpecimenTypeCollectionWithCheckInternal(
        List<? extends SpecimenTypeBaseWrapper> specimenTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.SPECIMEN_TYPES, specimenTypeCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(ContainerTypePeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(ContainerTypePeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(ContainerTypePeer.COMMENTS)) {
            addToWrapperCollection(ContainerTypePeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(ContainerTypePeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(ContainerTypePeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(ContainerTypePeer.COMMENTS)) {
            removeFromWrapperCollection(ContainerTypePeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(ContainerTypePeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(
        List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(
        List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.COMMENTS, commentCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public Capacity getCapacity() {
        return wrappedObject.getCapacity();
    }

    public void setCapacity(Capacity capacity) {
        wrappedObject.setCapacity(capacity);
    }

    public List<ContainerTypeWrapper> getChildContainerTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContainerTypePeer.CHILD_CONTAINER_TYPES);
        List<ContainerTypeWrapper> childContainerTypeCollection = getWrapperCollection(ContainerTypePeer.CHILD_CONTAINER_TYPES, ContainerTypeWrapper.class, sort);
        if (notCached) {
            for (ContainerTypeBaseWrapper e : childContainerTypeCollection) {
                e.addToParentContainerTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return childContainerTypeCollection;
    }

    public void addToChildContainerTypeCollection(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection) {
        addToWrapperCollection(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : childContainerTypeCollection) {
            e.addToParentContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToChildContainerTypeCollectionInternal(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection) {
        if (isInitialized(ContainerTypePeer.CHILD_CONTAINER_TYPES)) {
            addToWrapperCollection(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        } else {
            getElementQueue().add(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        }
    }

    public void removeFromChildContainerTypeCollection(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection) {
        removeFromWrapperCollection(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : childContainerTypeCollection) {
            e.removeFromParentContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromChildContainerTypeCollectionInternal(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection) {
        if (isPropertyCached(ContainerTypePeer.CHILD_CONTAINER_TYPES)) {
            removeFromWrapperCollection(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        } else {
            getElementQueue().remove(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        }
    }

    public void removeFromChildContainerTypeCollectionWithCheck(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : childContainerTypeCollection) {
            e.removeFromParentContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromChildContainerTypeCollectionWithCheckInternal(
        List<? extends ContainerTypeBaseWrapper> childContainerTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.CHILD_CONTAINER_TYPES, childContainerTypeCollection);
    }

    public List<ContainerTypeWrapper> getParentContainerTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContainerTypePeer.PARENT_CONTAINER_TYPES);
        List<ContainerTypeWrapper> parentContainerTypeCollection = getWrapperCollection(ContainerTypePeer.PARENT_CONTAINER_TYPES, ContainerTypeWrapper.class, sort);
        if (notCached) {
            for (ContainerTypeBaseWrapper e : parentContainerTypeCollection) {
                e.addToChildContainerTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return parentContainerTypeCollection;
    }

    public void addToParentContainerTypeCollection(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection) {
        addToWrapperCollection(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : parentContainerTypeCollection) {
            e.addToChildContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToParentContainerTypeCollectionInternal(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection) {
        if (isInitialized(ContainerTypePeer.PARENT_CONTAINER_TYPES)) {
            addToWrapperCollection(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        } else {
            getElementQueue().add(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        }
    }

    public void removeFromParentContainerTypeCollection(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection) {
        removeFromWrapperCollection(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : parentContainerTypeCollection) {
            e.removeFromChildContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromParentContainerTypeCollectionInternal(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection) {
        if (isPropertyCached(ContainerTypePeer.PARENT_CONTAINER_TYPES)) {
            removeFromWrapperCollection(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        } else {
            getElementQueue().remove(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        }
    }

    public void removeFromParentContainerTypeCollectionWithCheck(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
        for (ContainerTypeBaseWrapper e : parentContainerTypeCollection) {
            e.removeFromChildContainerTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromParentContainerTypeCollectionWithCheckInternal(
        List<? extends ContainerTypeBaseWrapper> parentContainerTypeCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContainerTypePeer.PARENT_CONTAINER_TYPES, parentContainerTypeCollection);
    }

}
