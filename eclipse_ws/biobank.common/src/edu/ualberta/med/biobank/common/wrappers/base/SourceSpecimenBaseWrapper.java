/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceSpecimenBaseWrapper extends ModelWrapper<SourceSpecimen> {

    public SourceSpecimenBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SourceSpecimenBaseWrapper(WritableApplicationService appService,
        SourceSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<SourceSpecimen> getWrappedClass() {
        return SourceSpecimen.class;
    }

    @Override
   protected SourceSpecimen getNewObject() throws Exception {
        SourceSpecimen newObject = super.getNewObject();
        newObject.setNeedOriginalVolume(false);
        return newObject;
    }

    @Override
    public Property<Integer, ? super SourceSpecimen> getIdProperty() {
        return SourceSpecimenPeer.ID;
    }

    @Override
    protected List<Property<?, ? super SourceSpecimen>> getProperties() {
        return SourceSpecimenPeer.PROPERTIES;
    }

    public Boolean getNeedOriginalVolume() {
        return getProperty(SourceSpecimenPeer.NEED_ORIGINAL_VOLUME);
    }

    public void setNeedOriginalVolume(Boolean needOriginalVolume) {
        setProperty(SourceSpecimenPeer.NEED_ORIGINAL_VOLUME, needOriginalVolume);
    }

    public StudyWrapper getStudy() {
        boolean notCached = !isPropertyCached(SourceSpecimenPeer.STUDY);
        StudyWrapper study = getWrappedProperty(SourceSpecimenPeer.STUDY, StudyWrapper.class);
        if (study != null && notCached) ((StudyBaseWrapper) study).addToSourceSpecimenCollectionInternal(Arrays.asList(this));
        return study;
    }

    public void setStudy(StudyBaseWrapper study) {
        if (isInitialized(SourceSpecimenPeer.STUDY)) {
            StudyBaseWrapper oldStudy = getStudy();
            if (oldStudy != null) oldStudy.removeFromSourceSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (study != null) study.addToSourceSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SourceSpecimenPeer.STUDY, study);
    }

    void setStudyInternal(StudyBaseWrapper study) {
        setWrappedProperty(SourceSpecimenPeer.STUDY, study);
    }

    public SpecimenTypeWrapper getSpecimenType() {
        SpecimenTypeWrapper specimenType = getWrappedProperty(SourceSpecimenPeer.SPECIMEN_TYPE, SpecimenTypeWrapper.class);
        return specimenType;
    }

    public void setSpecimenType(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(SourceSpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

    void setSpecimenTypeInternal(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(SourceSpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

}
