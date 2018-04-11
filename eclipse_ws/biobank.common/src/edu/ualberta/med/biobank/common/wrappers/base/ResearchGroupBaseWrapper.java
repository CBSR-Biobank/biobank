/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 *
 * Code Changes -
 * 		1> Changes related to having multiple Studies associated with a particular Research Group
 * 		2> Added methods to add and remove Studies similar to how it happens in SiteBaseWrapper
 *
 * @author OHSDEV
 *
 */
public abstract class ResearchGroupBaseWrapper extends CenterWrapper<ResearchGroup> {

    public ResearchGroupBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ResearchGroupBaseWrapper(WritableApplicationService appService,
        ResearchGroup wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ResearchGroup> getWrappedClass() {
        return ResearchGroup.class;
    }

    @Override
    public Property<Integer, ? super ResearchGroup> getIdProperty() {
        return ResearchGroupPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ResearchGroup>> getProperties() {
        List<Property<?, ? super ResearchGroup>> superNames = super.getProperties();
        List<Property<?, ? super ResearchGroup>> all = new ArrayList<Property<?, ? super ResearchGroup>>();
        all.addAll(superNames);
        all.addAll(ResearchGroupPeer.PROPERTIES);
        return all;
    }

    // OHSDEV -->
    public void addToStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        addToWrapperCollection(ResearchGroupPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.addToResearchGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void addToStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isInitialized(ResearchGroupPeer.STUDIES)) {
            addToWrapperCollection(ResearchGroupPeer.STUDIES, studyCollection);
        } else {
            getElementQueue().add(ResearchGroupPeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        removeFromWrapperCollection(ResearchGroupPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromResearchGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isPropertyCached(ResearchGroupPeer.STUDIES)) {
            removeFromWrapperCollection(ResearchGroupPeer.STUDIES, studyCollection);
        } else {
            getElementQueue().remove(ResearchGroupPeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollectionWithCheck(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ResearchGroupPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromResearchGroupCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionWithCheckInternal(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ResearchGroupPeer.STUDIES, studyCollection);
    }

    public List<StudyWrapper> getStudyCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ResearchGroupPeer.STUDIES);
        List<StudyWrapper> studyCollection = getWrapperCollection(ResearchGroupPeer.STUDIES, StudyWrapper.class, sort);
        if (notCached) {
            for (StudyBaseWrapper e : studyCollection) {
                e.addToResearchGroupCollectionInternal(Arrays.asList(this));
            }
        }
        return studyCollection;
    }
    // <-- OHSDEV

    public List<RequestWrapper> getRequestCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ResearchGroupPeer.REQUESTS);
        List<RequestWrapper> requestCollection = getWrapperCollection(ResearchGroupPeer.REQUESTS, RequestWrapper.class, sort);
        if (notCached) {
            for (RequestBaseWrapper e : requestCollection) {
                e.setResearchGroupInternal(this);
            }
        }
        return requestCollection;
    }

    public void addToRequestCollection(List<? extends RequestBaseWrapper> requestCollection) {
        addToWrapperCollection(ResearchGroupPeer.REQUESTS, requestCollection);
        for (RequestBaseWrapper e : requestCollection) {
            e.setResearchGroupInternal(this);
        }
    }

    void addToRequestCollectionInternal(List<? extends RequestBaseWrapper> requestCollection) {
        if (isInitialized(ResearchGroupPeer.REQUESTS)) {
            addToWrapperCollection(ResearchGroupPeer.REQUESTS, requestCollection);
        } else {
            getElementQueue().add(ResearchGroupPeer.REQUESTS, requestCollection);
        }
    }

    public void removeFromRequestCollection(List<? extends RequestBaseWrapper> requestCollection) {
        removeFromWrapperCollection(ResearchGroupPeer.REQUESTS, requestCollection);
        for (RequestBaseWrapper e : requestCollection) {
            e.setResearchGroupInternal(null);
        }
    }

    void removeFromRequestCollectionInternal(List<? extends RequestBaseWrapper> requestCollection) {
        if (isPropertyCached(ResearchGroupPeer.REQUESTS)) {
            removeFromWrapperCollection(ResearchGroupPeer.REQUESTS, requestCollection);
        } else {
            getElementQueue().remove(ResearchGroupPeer.REQUESTS, requestCollection);
        }
    }

    public void removeFromRequestCollectionWithCheck(List<? extends RequestBaseWrapper> requestCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ResearchGroupPeer.REQUESTS, requestCollection);
        for (RequestBaseWrapper e : requestCollection) {
            e.setResearchGroupInternal(null);
        }
    }

    void removeFromRequestCollectionWithCheckInternal(List<? extends RequestBaseWrapper> requestCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ResearchGroupPeer.REQUESTS, requestCollection);
    }

}
