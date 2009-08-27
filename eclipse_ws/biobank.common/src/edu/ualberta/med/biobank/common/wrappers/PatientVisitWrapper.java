package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> {

    public PatientVisitWrapper(WritableApplicationService appService,
        PatientVisit wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(PatientVisit oldWrappedObject,
        PatientVisit newWrappedObject) {
        propertyChangeSupport.firePropertyChange("patient", oldWrappedObject,
            newWrappedObject);
        propertyChangeSupport.firePropertyChange("dateDrawn", oldWrappedObject,
            newWrappedObject);
        propertyChangeSupport.firePropertyChange("clinic", oldWrappedObject,
            newWrappedObject);
    }

    public Date getDateDrawn() {
        return wrappedObject.getDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDateTime(getDateDrawn());
    }

    public PatientWrapper getPatientWrapper() {
        return new PatientWrapper(appService, wrappedObject.getPatient());
    }

    public void setPatientWrapper(PatientWrapper patientWrapper) {
        Patient oldPatient = wrappedObject.getPatient();
        Patient newPatient = patientWrapper.getWrappedObject();
        wrappedObject.setPatient(newPatient);
        propertyChangeSupport.firePropertyChange("patient", oldPatient,
            newPatient);
    }

    public Collection<Sample> getSampleCollection() {
        return wrappedObject.getSampleCollection();
    }

    public Clinic getClinic() {
        return wrappedObject.getClinic();
    }

    public Collection<PvSampleSource> getPvSampleSourceCollection() {
        return wrappedObject.getPvSampleSourceCollection();
    }

    public Collection<PvInfoData> getPvInfoDataCollection() {
        return wrappedObject.getPvInfoDataCollection();
    }

    public void setDateDrawn(Date date) {
        Date oldDate = getDateDrawn();
        wrappedObject.setDateDrawn(date);
        propertyChangeSupport.firePropertyChange("dateDrawn", oldDate, date);
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        if (checkVisitDateDrawnUnique()) {
            return DatabaseResult.OK;
        }
        return new DatabaseResult("A patient visit with date drawn "
            + getDateDrawn() + " already exist in patient "
            + getPatientWrapper().getNumber() + ".");
    }

    private boolean checkVisitDateDrawnUnique() throws ApplicationException {
        if (isNew()) {
            HQLCriteria c = new HQLCriteria("from "
                + PatientVisit.class.getName()
                + " where patient=? and dateDrawn = ?", Arrays
                .asList(new Object[] { getPatientWrapper().getWrappedObject(),
                    getDateDrawn() }));

            List<Object> results = appService.query(c);
            return results.size() == 0;
        }
        return true;
    }

    public void setClinic(Clinic clinic) {
        Clinic oldClinic = getClinic();
        wrappedObject.setClinic(clinic);
        propertyChangeSupport.firePropertyChange("clinic", oldClinic, clinic);
    }

    public void setPvSampleSourceCollection(
        Collection<PvSampleSource> pvSampleSources) {
        wrappedObject.setPvSampleSourceCollection(pvSampleSources);
    }

    public void setPvInfoDataCollection(Collection<PvInfoData> pvDataCollection) {
        wrappedObject.setPvInfoDataCollection(pvDataCollection);
    }

    @Override
    protected Class<PatientVisit> getWrappedClass() {
        return PatientVisit.class;
    }

}
