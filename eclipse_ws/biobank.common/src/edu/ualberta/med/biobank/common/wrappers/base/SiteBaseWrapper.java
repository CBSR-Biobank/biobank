/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerTypeBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyBaseWrapper;
import java.util.Arrays;

public abstract class SiteBaseWrapper extends CenterWrapper<Site> {

    public SiteBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SiteBaseWrapper(WritableApplicationService appService,
        Site wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    public Property<Integer, ? super Site> getIdProperty() {
        return SitePeer.ID;
    }

    @Override
    protected List<Property<?, ? super Site>> getProperties() {
        List<Property<?, ? super Site>> superNames = super.getProperties();
        List<Property<?, ? super Site>> all = new ArrayList<Property<?, ? super Site>>();
        all.addAll(superNames);
        all.addAll(SitePeer.PROPERTIES);
        return all;
    }

    public List<ContainerWrapper> getContainerCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SitePeer.CONTAINERS);
        List<ContainerWrapper> containerCollection = getWrapperCollection(SitePeer.CONTAINERS, ContainerWrapper.class, sort);
        if (notCached) {
            for (ContainerBaseWrapper e : containerCollection) {
                e.setSiteInternal(this);
            }
        }
        return containerCollection;
    }

    public void addToContainerCollection(List<? extends ContainerBaseWrapper> containerCollection) {
        addToWrapperCollection(SitePeer.CONTAINERS, containerCollection);
        for (ContainerBaseWrapper e : containerCollection) {
            e.setSiteInternal(this);
        }
    }

    void addToContainerCollectionInternal(List<? extends ContainerBaseWrapper> containerCollection) {
        if (isInitialized(SitePeer.CONTAINERS)) {
            addToWrapperCollection(SitePeer.CONTAINERS, containerCollection);
        } else {
            getElementQueue().add(SitePeer.CONTAINERS, containerCollection);
        }
    }

    public void removeFromContainerCollection(List<? extends ContainerBaseWrapper> containerCollection) {
        removeFromWrapperCollection(SitePeer.CONTAINERS, containerCollection);
        for (ContainerBaseWrapper e : containerCollection) {
            e.setSiteInternal(null);
        }
    }

    void removeFromContainerCollectionInternal(List<? extends ContainerBaseWrapper> containerCollection) {
        if (isPropertyCached(SitePeer.CONTAINERS)) {
            removeFromWrapperCollection(SitePeer.CONTAINERS, containerCollection);
        } else {
            getElementQueue().remove(SitePeer.CONTAINERS, containerCollection);
        }
    }

    public void removeFromContainerCollectionWithCheck(List<? extends ContainerBaseWrapper> containerCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.CONTAINERS, containerCollection);
        for (ContainerBaseWrapper e : containerCollection) {
            e.setSiteInternal(null);
        }
    }

    void removeFromContainerCollectionWithCheckInternal(List<? extends ContainerBaseWrapper> containerCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.CONTAINERS, containerCollection);
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SitePeer.CONTAINER_TYPES);
        List<ContainerTypeWrapper> containerTypeCollection = getWrapperCollection(SitePeer.CONTAINER_TYPES, ContainerTypeWrapper.class, sort);
        if (notCached) {
            for (ContainerTypeBaseWrapper e : containerTypeCollection) {
                e.setSiteInternal(this);
            }
        }
        return containerTypeCollection;
    }

    public void addToContainerTypeCollection(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        addToWrapperCollection(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.setSiteInternal(this);
        }
    }

    void addToContainerTypeCollectionInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        if (isInitialized(SitePeer.CONTAINER_TYPES)) {
            addToWrapperCollection(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        } else {
            getElementQueue().add(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        }
    }

    public void removeFromContainerTypeCollection(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        removeFromWrapperCollection(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.setSiteInternal(null);
        }
    }

    void removeFromContainerTypeCollectionInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        if (isPropertyCached(SitePeer.CONTAINER_TYPES)) {
            removeFromWrapperCollection(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        } else {
            getElementQueue().remove(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        }
    }

    public void removeFromContainerTypeCollectionWithCheck(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.setSiteInternal(null);
        }
    }

    void removeFromContainerTypeCollectionWithCheckInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.CONTAINER_TYPES, containerTypeCollection);
    }

    public List<StudyWrapper> getStudyCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SitePeer.STUDIES);
        List<StudyWrapper> studyCollection = getWrapperCollection(SitePeer.STUDIES, StudyWrapper.class, sort);
        if (notCached) {
            for (StudyBaseWrapper e : studyCollection) {
                e.addToSiteCollectionInternal(Arrays.asList(this));
            }
        }
        return studyCollection;
    }

    public void addToStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        addToWrapperCollection(SitePeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.addToSiteCollectionInternal(Arrays.asList(this));
        }
    }

    void addToStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isInitialized(SitePeer.STUDIES)) {
            addToWrapperCollection(SitePeer.STUDIES, studyCollection);
        } else {
            getElementQueue().add(SitePeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        removeFromWrapperCollection(SitePeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromSiteCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isPropertyCached(SitePeer.STUDIES)) {
            removeFromWrapperCollection(SitePeer.STUDIES, studyCollection);
        } else {
            getElementQueue().remove(SitePeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollectionWithCheck(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromSiteCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionWithCheckInternal(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SitePeer.STUDIES, studyCollection);
    }

}
