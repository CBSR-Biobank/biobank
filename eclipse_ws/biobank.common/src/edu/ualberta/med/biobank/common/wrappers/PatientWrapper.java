package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

//FIXME get/set for model object should use wrapper ?
public class PatientWrapper extends ModelWrapper<Patient> {

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public String getNumber() {
        return wrappedObject.getNumber();
    }

    public void setNumber(String number) {
        String oldNumber = getNumber();
        wrappedObject.setNumber(number);
        propertyChangeSupport.firePropertyChange("number", oldNumber, number);
    }

    public Study getStudy() {
        return wrappedObject.getStudy();
    }

    public void setStudy(Study study) {
        String oldStudy = getNumber();
        wrappedObject.setStudy(study);
        propertyChangeSupport.firePropertyChange("study", oldStudy, study);
    }

    public boolean checkPatientNumberUnique() throws ApplicationException {
        if (isNew()) {
            HQLCriteria c = new HQLCriteria("from " + Patient.class.getName()
                + " where study = ? and number = ?", Arrays
                .asList(new Object[] { getStudy(), getNumber() }));

            List<Object> results = appService.query(c);
            return results.size() == 0;
        }
        return true;
    }

    /**
     * When retrieve the values from the database, need to fire the
     * modifications for the different objects contained in the wrapped object
     */
    @Override
    protected void firePropertyChanges(Patient oldPatient, Patient newpatient) {
        propertyChangeSupport.firePropertyChange("number", oldPatient
            .getNumber(), newpatient.getNumber());
        propertyChangeSupport.firePropertyChange("study",
            oldPatient.getStudy(), newpatient.getStudy());
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (!checkPatientNumberUnique()) {
            throw new BiobankCheckException("A patient with number \""
                + getNumber() + "\" already exists.");
        }
    }

    public Collection<PatientVisit> getPatientVisitCollection() {
        return wrappedObject.getPatientVisitCollection();
    }

    public void setPatientVisitCollection(
        Collection<PatientVisit> patientVisitCollection) {
        wrappedObject.setPatientVisitCollection(patientVisitCollection);
    }

    public static PatientWrapper getPatientWrapperInSite(
        WritableApplicationService appService, String patientNumber, Site site)
        throws ApplicationException {
        Patient patient = getPatientInSite(appService, patientNumber, site);
        if (patient != null) {
            return new PatientWrapper(appService, patient);
        }
        return null;
    }

    public static Patient getPatientInSite(
        WritableApplicationService appService, String patientNumber, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName() + " where study.site = ? and number = ?",
            Arrays.asList(new Object[] { site, patientNumber }));
        List<Patient> patients;
        patients = appService.query(criteria);
        if (patients.size() == 1) {
            return patients.get(0);
        }
        return null;
    }

    @Override
    protected Class<Patient> getWrappedClass() {
        return Patient.class;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        // TODO Auto-generated method stub
    }
}