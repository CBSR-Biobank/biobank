package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleStorageWrapper extends ModelWrapper<SampleStorage> implements
    Comparable<SampleStorageWrapper> {

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

    public SampleType getSampleType() {
        return wrappedObject.getSampleType();
    }

    public void setSampleType(SampleType sampleType) {
        SampleType oldSampleType = getSampleType();
        wrappedObject.setSampleType(sampleType);
        propertyChangeSupport.firePropertyChange("sampleType", oldSampleType,
            sampleType);
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

    public int compareTo(SampleStorageWrapper wrapper) {
        String myName = wrappedObject.getSampleType().getName();
        String wrapperName = wrapper.wrappedObject.getSampleType().getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

}
