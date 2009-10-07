package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvSampleSourceWrapper extends ModelWrapper<PvSampleSource> {

    public PvSampleSourceWrapper(WritableApplicationService appService,
        PvSampleSource wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvSampleSourceWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "quantity", "patientVisit", "sampleSource" };
    }

    @Override
    public Class<PvSampleSource> getWrappedClass() {
        return PvSampleSource.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
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

    public PatientVisitWrapper getPatientVisit() {
        PatientVisit pv = wrappedObject.getPatientVisit();
        if (pv == null) {
            return null;
        }
        return new PatientVisitWrapper(appService, pv);
    }

    public void setPatientVisit(PatientVisit pv) {
        PatientVisitWrapper oldPv = getPatientVisit();
        wrappedObject.setPatientVisit(pv);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPv, pv);
    }

    public void setPatientVisit(PatientVisitWrapper pv) {
        setPatientVisit(pv.wrappedObject);
    }

    public SampleSourceWrapper getSampleSource() {
        SampleSource ss = wrappedObject.getSampleSource();
        if (ss == null) {
            return null;
        }
        return new SampleSourceWrapper(appService, ss);
    }

    public void setSampleSource(SampleSource ss) {
        SampleSource oldSs = wrappedObject.getSampleSource();
        wrappedObject.setSampleSource(ss);
        propertyChangeSupport.firePropertyChange("sampleSource", oldSs, ss);
    }

}
