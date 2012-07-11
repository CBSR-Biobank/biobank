/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.StudyEventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.GlobalEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyEventAttrBaseWrapper extends ModelWrapper<StudyEventAttr> {

    public StudyEventAttrBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public StudyEventAttrBaseWrapper(WritableApplicationService appService,
        StudyEventAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<StudyEventAttr> getWrappedClass() {
        return StudyEventAttr.class;
    }

    @Override
   protected StudyEventAttr getNewObject() throws Exception {
        StudyEventAttr newObject = super.getNewObject();
        newObject.setRequired(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super StudyEventAttr> getIdProperty() {
        return StudyEventAttrPeer.ID;
    }

    @Override
    protected List<Property<?, ? super StudyEventAttr>> getProperties() {
        return StudyEventAttrPeer.PROPERTIES;
    }

    public String getPermissible() {
        return getProperty(StudyEventAttrPeer.PERMISSIBLE);
    }

    public void setPermissible(String permissible) {
        String trimmed = permissible == null ? null : permissible.trim();
        setProperty(StudyEventAttrPeer.PERMISSIBLE, trimmed);
    }

    public Boolean getRequired() {
        return getProperty(StudyEventAttrPeer.REQUIRED);
    }

    public void setRequired(Boolean required) {
        setProperty(StudyEventAttrPeer.REQUIRED, required);
    }

    public GlobalEventAttrWrapper getGlobalEventAttr() {
        GlobalEventAttrWrapper globalEventAttr = getWrappedProperty(StudyEventAttrPeer.GLOBAL_EVENT_ATTR, GlobalEventAttrWrapper.class);
        return globalEventAttr;
    }

    public void setGlobalEventAttr(GlobalEventAttrBaseWrapper globalEventAttr) {
        setWrappedProperty(StudyEventAttrPeer.GLOBAL_EVENT_ATTR, globalEventAttr);
    }

    void setGlobalEventAttrInternal(GlobalEventAttrBaseWrapper globalEventAttr) {
        setWrappedProperty(StudyEventAttrPeer.GLOBAL_EVENT_ATTR, globalEventAttr);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public StudyWrapper getStudy() {
        boolean notCached = !isPropertyCached(StudyEventAttrPeer.STUDY);
        StudyWrapper study = getWrappedProperty(StudyEventAttrPeer.STUDY, StudyWrapper.class);
        if (study != null && notCached) ((StudyBaseWrapper) study).addToStudyEventAttrCollectionInternal(Arrays.asList(this));
        return study;
    }

    public void setStudy(StudyBaseWrapper study) {
        if (isInitialized(StudyEventAttrPeer.STUDY)) {
            StudyBaseWrapper oldStudy = getStudy();
            if (oldStudy != null) oldStudy.removeFromStudyEventAttrCollectionInternal(Arrays.asList(this));
        }
        if (study != null) study.addToStudyEventAttrCollectionInternal(Arrays.asList(this));
        setWrappedProperty(StudyEventAttrPeer.STUDY, study);
    }

    void setStudyInternal(StudyBaseWrapper study) {
        setWrappedProperty(StudyEventAttrPeer.STUDY, study);
    }

}
