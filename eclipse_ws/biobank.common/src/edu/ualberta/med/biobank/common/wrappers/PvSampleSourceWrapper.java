package edu.ualberta.med.biobank.common.wrappers;

import java.util.Date;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;
import gov.nih.nci.system.applicationservice.ApplicationException;
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
    protected String[] getPropertyChangeNames() {
        return new String[] { "quantity", "patientVisit", "sampleSource",
            "patientCollection" };
    }

    @Override
    public Class<PvSampleSource> getWrappedClass() {
        return PvSampleSource.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
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
        PatientVisit p = wrappedObject.getPatientVisit();
        if (p == null) {
            return null;
        }
        return new PatientVisitWrapper(appService, p);
    }

    public void setPatientVisit(PatientVisit visit) {
        PatientVisitWrapper old = getPatientVisit();
        wrappedObject.setPatientVisit(visit);
        propertyChangeSupport.firePropertyChange("patientVisit", old, visit);
    }

    public void setPatientVisit(PatientVisitWrapper visit) {
        setPatientVisit(visit.wrappedObject);
    }

    public Date getDateDrawn() {
        return wrappedObject.getDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDateTime(getDateDrawn());
    }

    public void setDateDrawn(Date date) {
        Date oldDate = getDateDrawn();
        wrappedObject.setDateDrawn(date);
        propertyChangeSupport.firePropertyChange("dateDrawn", oldDate, date);
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

    public void setSampleSource(SampleSourceWrapper ss) {
        if (ss == null) {
            setSampleSource((SampleSource) null);
        } else {
            setSampleSource(ss.getWrappedObject());
        }
    }

    @Override
    public int compareTo(ModelWrapper<PvSampleSource> o) {
        if (o instanceof PvSampleSourceWrapper) {
            return getSampleSource().compareTo(
                ((PvSampleSourceWrapper) o).getSampleSource());
        }
        return 0;
    }
}
