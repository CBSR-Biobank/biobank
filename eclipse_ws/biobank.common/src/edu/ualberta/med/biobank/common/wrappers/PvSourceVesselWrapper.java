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

    private PatientVisitWrapper pv;
    private SourceVesselWrapper ss;

    public PvSourceVesselWrapper(WritableApplicationService appService,
        PvSourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvSourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "quantity", "patientVisit", "sourceVessel",
            "patientCollection", "timeDrawn", "volume" };
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
        if (pv == null) {
            PatientVisit p = wrappedObject.getPatientVisit();
            if (p == null)
                return null;
            pv = new PatientVisitWrapper(appService, p);
        }
        return pv;
    }

    public void setPatientVisit(PatientVisitWrapper visit) {
        this.pv = visit;
        PatientVisit oldPv = wrappedObject.getPatientVisit();
        PatientVisit newPv = visit.wrappedObject;
        wrappedObject.setPatientVisit(newPv);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPv, newPv);
    }

    public Date getTimeDrawn() {
        return wrappedObject.getTimeDrawn();
    }

    public String getFormattedTimeDrawn() {
        return DateFormatter.formatAsTime(getTimeDrawn());
    }

    public void setTimeDrawn(Date date) {
        Date oldDate = getTimeDrawn();
        wrappedObject.setTimeDrawn(date);
        propertyChangeSupport.firePropertyChange("timeDrawn", oldDate, date);
    }

    public String getVolume() {
        return wrappedObject.getVolume();
    }

    public void setVolume(String volume) {
        String oldVol = getVolume();
        wrappedObject.setVolume(volume);
        propertyChangeSupport.firePropertyChange("volume", oldVol, volume);
    }

    public SourceVesselWrapper getSourceVessel() {
        if (ss == null) {
            SourceVessel s = wrappedObject.getSourceVessel();
            if (s == null)
                return null;
            ss = new SourceVesselWrapper(appService, s);
        }
        return ss;
    }

    protected void setSourceVessel(SourceVessel ss) {
        this.ss = new SourceVesselWrapper(appService, ss);
        SourceVessel oldSs = wrappedObject.getSourceVessel();
        wrappedObject.setSourceVessel(ss);
        propertyChangeSupport.firePropertyChange("sourceVessel", oldSs, ss);
    }

    public void setSourceVessel(SourceVesselWrapper ss) {
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

    @Override
    public void reload() throws Exception {
        super.reload();
        ss = null;
        pv = null;
    }
}
