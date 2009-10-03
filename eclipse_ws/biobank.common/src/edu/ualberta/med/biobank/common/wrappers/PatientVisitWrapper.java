package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
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

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> implements
    Comparable<PatientVisitWrapper> {

    public PatientVisitWrapper(WritableApplicationService appService,
        PatientVisit wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "patient", "dateDrawn", "dateProcessed",
            "dateReceived", "clinic", "comments", "pvInfoDataCollection" };
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
        Patient patient = wrappedObject.getPatient();
        if (patient == null) {
            return null;
        }
        return new PatientWrapper(appService, patient);
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

    public ClinicWrapper getClinic() {
        Clinic clinic = wrappedObject.getClinic();
        if (clinic == null) {
            return null;
        }
        return new ClinicWrapper(appService, clinic);
    }

    public Collection<PvSampleSource> getPvSampleSourceCollection() {
        return wrappedObject.getPvSampleSourceCollection();
    }

    @SuppressWarnings("unchecked")
    public List<PvInfoDataWrapper> getPvInfoDataCollection() {
        List<PvInfoDataWrapper> pvInfoDataCollection = (List<PvInfoDataWrapper>) propertiesMap
            .get("pvInfoDataCollection");
        if (pvInfoDataCollection == null) {
            Collection<PvInfoData> children = wrappedObject
                .getPvInfoDataCollection();
            if (children != null) {
                pvInfoDataCollection = new ArrayList<PvInfoDataWrapper>();
                for (PvInfoData pvInfo : children) {
                    pvInfoDataCollection.add(new PvInfoDataWrapper(appService,
                        pvInfo));
                }
                propertiesMap.put("pvInfoDataCollection", pvInfoDataCollection);
            }
        }
        return pvInfoDataCollection;
    }

    public void setPvInfoDataCollection(
        Collection<PvInfoData> pvInfoDataCollection, boolean setNull) {
        Collection<PvInfoData> oldCollection = wrappedObject
            .getPvInfoDataCollection();
        wrappedObject.setPvInfoDataCollection(pvInfoDataCollection);
        propertyChangeSupport.firePropertyChange("pvInfoDataCollection",
            oldCollection, pvInfoDataCollection);
        if (setNull) {
            propertiesMap.put("pvInfoDataCollection", null);
        }
    }

    public void setPvInfoDataCollection(
        Collection<PvInfoDataWrapper> pvInfoDataCollection) {
        Collection<PvInfoData> pvCollection = new HashSet<PvInfoData>();
        for (PvInfoDataWrapper pv : pvInfoDataCollection) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPvInfoDataCollection(pvCollection, false);
        propertiesMap.put("pvInfoDataCollection", pvInfoDataCollection);
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
        Clinic oldClinic = wrappedObject.getClinic();
        wrappedObject.setClinic(clinic);
        propertyChangeSupport.firePropertyChange("clinic", oldClinic, clinic);
    }

    public void setPvSampleSourceCollection(
        Collection<PvSampleSource> pvSampleSources) {
        wrappedObject.setPvSampleSourceCollection(pvSampleSources);
    }

    @Override
    protected Class<PatientVisit> getWrappedClass() {
        return PatientVisit.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public int compareTo(PatientVisitWrapper o) {
        Date v1Date = getDateDrawn();
        Date v2Date = o.getDateDrawn();
        return ((v1Date.compareTo(v2Date) > 0) ? 1 : (v1Date.equals(v2Date) ? 0
            : -1));
    }

}
