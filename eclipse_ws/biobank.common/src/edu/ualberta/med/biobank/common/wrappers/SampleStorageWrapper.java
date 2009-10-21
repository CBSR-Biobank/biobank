package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleStorageWrapper extends ModelWrapper<SampleStorage> {

    public SampleStorageWrapper(WritableApplicationService appService,
        SampleStorage wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleStorageWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public Study getStudy() {
        return wrappedObject.getStudy();
    }

    public void setStudy(Study study) {
        Study oldStudy = getStudy();
        wrappedObject.setStudy(study);
        propertyChangeSupport.firePropertyChange("study", oldStudy, study);
    }

    public void setStudy(StudyWrapper study) {
        setStudy(study.wrappedObject);
    }

    public SampleTypeWrapper getSampleType() {
        SampleType type = wrappedObject.getSampleType();
        if (type == null) {
            return null;
        }
        return new SampleTypeWrapper(appService, type);
    }

    public void setSampleType(SampleType sampleType) {
        SampleType oldSampleType = wrappedObject.getSampleType();
        wrappedObject.setSampleType(sampleType);
        propertyChangeSupport.firePropertyChange("sampleType", oldSampleType,
            sampleType);
    }

    public void setSampleType(SampleTypeWrapper sampleType) {
        SampleType type = null;
        if (sampleType != null) {
            type = sampleType.getWrappedObject();
        }
        setSampleType(type);
    }

    public Integer getQuantity() {
        return wrappedObject.getQuantity();
    }

    public void setQuantity(Integer quantity) {
        Integer oldQuantity = getQuantity();
        wrappedObject.setQuantity(quantity);
        propertyChangeSupport.firePropertyChange("quantity", oldQuantity,
            quantity);
    }

    public Double getVolume() {
        return wrappedObject.getVolume();
    }

    public void setVolume(Double volume) {
        Double oldVolume = getVolume();
        wrappedObject.setVolume(volume);
        propertyChangeSupport.firePropertyChange("volume", oldVolume, volume);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "study", "sampleType", "quantity", "volume" };
    }

    @Override
    public Class<SampleStorage> getWrappedClass() {
        return SampleStorage.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    public int compareTo(ModelWrapper<SampleStorage> wrapper) {
        String name1 = wrappedObject.getSampleType().getName();
        String name2 = wrapper.wrappedObject.getSampleType().getName();
        return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
            : -1));
    }

}
