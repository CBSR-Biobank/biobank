package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
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
        PatientVisit newWrappedObject) throws Exception {
        String[] members = new String[] { "patient", "dateDrawn",
            "dateProcessed", "dateReceived", "clinic", "comments" };
        firePropertyChanges(members, oldWrappedObject, newWrappedObject);
    }

    public Date getDateDrawn() {
        return wrappedObject.getDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDateTime(getDateDrawn());
    }

    public Date getDateProcessed() {
        return wrappedObject.getDateProcessed();
    }

    public String getFormattedDateProcessed() {
        return DateFormatter.formatAsDateTime(getDateProcessed());
    }

    public Date getDateReceived() {
        return wrappedObject.getDateReceived();
    }

    public String getFormattedDateReceived() {
        return DateFormatter.formatAsDateTime(getDateReceived());
    }

    public String getComments() {
        return wrappedObject.getComments();
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

    public void setSampleCollection(Collection<Sample> sampleCollection) {
        wrappedObject.setSampleCollection(sampleCollection);
    }

    public Collection<SampleWrapper> getSampleWrapperCollection() {
        Collection<SampleWrapper> collection = new HashSet<SampleWrapper>();
        for (Sample sample : wrappedObject.getSampleCollection()) {
            collection.add(new SampleWrapper(appService, sample));
        }
        return collection;
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

    public void setDateProcessed(Date date) {
        Date oldDate = getDateProcessed();
        wrappedObject.setDateProcessed(date);
        propertyChangeSupport
            .firePropertyChange("dateProcessed", oldDate, date);
    }

    public void setDateReceived(Date date) {
        Date oldDate = getDateReceived();
        wrappedObject.setDateReceived(date);
        propertyChangeSupport.firePropertyChange("dateReceived", oldDate, date);
    }

    public void setComments(String comments) {
        String oldComments = getComments();
        wrappedObject.setComments(comments);
        propertyChangeSupport.firePropertyChange("comments", oldComments,
            comments);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        if (!checkVisitDateDrawnUnique()) {
            throw new BiobankCheckException("A patient visit with date drawn "
                + getDateDrawn() + " already exist in patient "
                + getPatientWrapper().getNumber() + ".");
        }
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

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

}
