/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.AliquotedSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AliquotedSpecimenBaseWrapper extends ModelWrapper<AliquotedSpecimen> {

    public AliquotedSpecimenBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AliquotedSpecimenBaseWrapper(WritableApplicationService appService,
        AliquotedSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<AliquotedSpecimen> getWrappedClass() {
        return AliquotedSpecimen.class;
    }

    @Override
    public Property<Integer, ? super AliquotedSpecimen> getIdProperty() {
        return AliquotedSpecimenPeer.ID;
    }

    @Override
    protected List<Property<?, ? super AliquotedSpecimen>> getProperties() {
        return AliquotedSpecimenPeer.PROPERTIES;
    }

    public BigDecimal getVolume() {
        return getProperty(AliquotedSpecimenPeer.VOLUME);
    }

    public void setVolume(BigDecimal volume) {
        setProperty(AliquotedSpecimenPeer.VOLUME, volume);
    }

    public Integer getQuantity() {
        return getProperty(AliquotedSpecimenPeer.QUANTITY);
    }

    public void setQuantity(Integer quantity) {
        setProperty(AliquotedSpecimenPeer.QUANTITY, quantity);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public StudyWrapper getStudy() {
        boolean notCached = !isPropertyCached(AliquotedSpecimenPeer.STUDY);
        StudyWrapper study = getWrappedProperty(AliquotedSpecimenPeer.STUDY, StudyWrapper.class);
        if (study != null && notCached) ((StudyBaseWrapper) study).addToAliquotedSpecimenCollectionInternal(Arrays.asList(this));
        return study;
    }

    public void setStudy(StudyBaseWrapper study) {
        if (isInitialized(AliquotedSpecimenPeer.STUDY)) {
            StudyBaseWrapper oldStudy = getStudy();
            if (oldStudy != null) oldStudy.removeFromAliquotedSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (study != null) study.addToAliquotedSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(AliquotedSpecimenPeer.STUDY, study);
    }

    void setStudyInternal(StudyBaseWrapper study) {
        setWrappedProperty(AliquotedSpecimenPeer.STUDY, study);
    }

    public SpecimenTypeWrapper getSpecimenType() {
        SpecimenTypeWrapper specimenType = getWrappedProperty(AliquotedSpecimenPeer.SPECIMEN_TYPE, SpecimenTypeWrapper.class);
        return specimenType;
    }

    public void setSpecimenType(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(AliquotedSpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

    void setSpecimenTypeInternal(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(AliquotedSpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

}
