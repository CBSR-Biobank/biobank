package edu.ualberta.med.biobank.common.wrappers;

import java.util.Date;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSourceVessel;
import edu.ualberta.med.biobank.model.SourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvSourceVesselWrapper extends ModelWrapper<PvSourceVessel> {

    public PvSourceVesselWrapper(WritableApplicationService appService,
        PvSourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvSourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "quantity", "patientVisit", "sampleSource",
            "patientCollection", "dateDrawn" };
    }

    @Override
    public Class<PvSourceVessel> getWrappedClass() {
        return PvSourceVessel.class;
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

    public void setPatientVisit(PatientVisitWrapper visit) {
        PatientVisit oldPv = wrappedObject.getPatientVisit();
        PatientVisit newPv = visit.wrappedObject;
        wrappedObject.setPatientVisit(newPv);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPv, newPv);
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

    public SampleSourceWrapper getSourceVessel() {
        SourceVessel ss = wrappedObject.getSourceVessel();
        if (ss == null) {
            return null;
        }
        return new SampleSourceWrapper(appService, ss);
    }

    protected void setSourceVessel(SourceVessel ss) {
        SourceVessel oldSs = wrappedObject.getSourceVessel();
        wrappedObject.setSourceVessel(ss);
        propertyChangeSupport.firePropertyChange("sampleSource", oldSs, ss);
    }

    public void setSourceVessel(SampleSourceWrapper ss) {
        if (ss == null) {
            setSourceVessel((SourceVessel) null);
        } else {
            setSourceVessel(ss.getWrappedObject());
        }
    }

    @Override
    public int compareTo(ModelWrapper<PvSourceVessel> o) {
        if (o instanceof PvSourceVesselWrapper) {
            return getSourceVessel().compareTo(
                ((PvSourceVesselWrapper) o).getSourceVessel());
        }
        return 0;
    }
}
