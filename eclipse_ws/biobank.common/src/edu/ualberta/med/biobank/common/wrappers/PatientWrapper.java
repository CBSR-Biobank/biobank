package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShptSampleSource;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends ModelWrapper<Patient> {

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public PatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getNumber() {
        return wrappedObject.getNumber();
    }

    public void setNumber(String number) {
        String oldNumber = getNumber();
        wrappedObject.setNumber(number);
        propertyChangeSupport.firePropertyChange("number", oldNumber, number);
    }

    public StudyWrapper getStudy() {
        Study study = wrappedObject.getStudy();
        if (study == null) {
            return null;
        }
        return new StudyWrapper(appService, study);
    }

    public void setStudy(Study study) {
        String oldStudy = getNumber();
        wrappedObject.setStudy(study);
        propertyChangeSupport.firePropertyChange("study", oldStudy, study);
    }

    public void setStudy(StudyWrapper study) {
        setStudy(study.wrappedObject);
    }

    public boolean checkPatientNumberUnique() throws ApplicationException {
        String isSamePatient = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getStudy().getSite().getId());
        params.add(getNumber());
        if (!isNew()) {
            isSamePatient = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + Patient.class.getName()
            + " where study.site.id = ? and number = ?" + isSamePatient, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    /**
     * When retrieve the values from the database, need to fire the
     * modifications for the different objects contained in the wrapped object
     * 
     * @throws Exception
     */
    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "number", "study", "patientVisitCollection",
            "shptSampleSourceCollection", "shipmentCollection" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (!checkPatientNumberUnique()) {
            throw new BiobankCheckException("A patient with number \""
                + getNumber() + "\" already exists.");
        }
    }

    @SuppressWarnings("unchecked")
    public List<PatientVisitWrapper> getPatientVisitCollection() {
        List<PatientVisitWrapper> patientVisitCollection = (List<PatientVisitWrapper>) propertiesMap
            .get("patientVisitCollection");
        if (patientVisitCollection == null) {
            Collection<PatientVisit> children = wrappedObject
                .getPatientVisitCollection();
            if (children != null) {
                patientVisitCollection = new ArrayList<PatientVisitWrapper>();
                for (PatientVisit pv : children) {
                    patientVisitCollection.add(new PatientVisitWrapper(
                        appService, pv));
                }
                propertiesMap.put("patientVisitCollection",
                    patientVisitCollection);
            }
        }
        return patientVisitCollection;
    }

    public void setPatientVisitCollection(
        Collection<PatientVisit> patientVisitCollection, boolean setNull) {
        Collection<PatientVisit> oldCollection = wrappedObject
            .getPatientVisitCollection();
        wrappedObject.setPatientVisitCollection(patientVisitCollection);
        propertyChangeSupport.firePropertyChange("patientVisitCollection",
            oldCollection, patientVisitCollection);
        if (setNull) {
            propertiesMap.put("patientVisitCollection", null);
        }
    }

    public void setPatientVisitCollection(
        Collection<PatientVisitWrapper> patientVisitCollection) {
        Collection<PatientVisit> pvCollection = new HashSet<PatientVisit>();
        for (PatientVisitWrapper pv : patientVisitCollection) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPatientVisitCollection(pvCollection, false);
        propertiesMap.put("patientVisitCollection", patientVisitCollection);
    }

    public static PatientWrapper getPatientInSite(
        WritableApplicationService appService, String patientNumber,
        SiteWrapper siteWrapper) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName()
            + " where study.site.id = ? and number = ?", Arrays
            .asList(new Object[] { siteWrapper.getId(), patientNumber }));
        List<Patient> patients;
        patients = appService.query(criteria);
        if (patients.size() == 1) {
            return new PatientWrapper(appService, patients.get(0));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ShptSampleSourceWrapper> getShptSampleSourceCollection() {
        List<ShptSampleSourceWrapper> shptSampleSourceCollection = (List<ShptSampleSourceWrapper>) propertiesMap
            .get("shptSampleSourceCollection");
        if (shptSampleSourceCollection == null) {
            Collection<ShptSampleSource> children = wrappedObject
                .getShptSampleSourceCollection();
            if (children != null) {
                shptSampleSourceCollection = new ArrayList<ShptSampleSourceWrapper>();
                for (ShptSampleSource pvSampleSource : children) {
                    shptSampleSourceCollection.add(new ShptSampleSourceWrapper(
                        appService, pvSampleSource));
                }
                propertiesMap.put("shptSampleSourceCollection",
                    shptSampleSourceCollection);
            }
        }
        return shptSampleSourceCollection;
    }

    public void setShptSampleSourceCollection(
        Collection<ShptSampleSource> shptSampleSources, boolean setNull) {
        Collection<ShptSampleSource> oldCollection = wrappedObject
            .getShptSampleSourceCollection();
        wrappedObject.setShptSampleSourceCollection(shptSampleSources);
        propertyChangeSupport.firePropertyChange("shptSampleSourceCollection",
            oldCollection, shptSampleSources);
        if (setNull) {
            propertiesMap.put("shptSampleSourceCollection", null);
        }
    }

    public void setShptSampleSourceCollection(
        Collection<ShptSampleSourceWrapper> shptSampleSources) {
        Collection<ShptSampleSource> shptCollection = new HashSet<ShptSampleSource>();
        for (ShptSampleSourceWrapper pv : shptSampleSources) {
            shptCollection.add(pv.getWrappedObject());
        }
        setShptSampleSourceCollection(shptCollection, false);
        propertiesMap.put("shptSampleSourceCollection", shptSampleSources);
    }

    @SuppressWarnings("unchecked")
    public List<ShipmentWrapper> getShipmentCollection(boolean sort)
        throws ApplicationException {
        List<ShipmentWrapper> shipmentCollection = (List<ShipmentWrapper>) propertiesMap
            .get("shipmentCollection");

        if (shipmentCollection == null) {
            shipmentCollection = new ArrayList<ShipmentWrapper>();
            HQLCriteria c = new HQLCriteria(
                "select ss.shipment from "
                    + Patient.class.getName()
                    + "  as p inner join p.shptSampleSourceCollection as ss where p.id=?",
                Arrays.asList(new Object[] { getId() }));
            List<Shipment> collection = appService.query(c);
            for (Shipment s : collection) {
                shipmentCollection.add(new ShipmentWrapper(appService, s));
            }
            if (sort)
                Collections.sort(shipmentCollection);
            propertiesMap.put("shipmentCollection", shipmentCollection);
        }
        return shipmentCollection;
    }

    @Override
    public Class<Patient> getWrappedClass() {
        return Patient.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    public int compareTo(ModelWrapper<Patient> wrapper) {
        String number1 = wrappedObject.getNumber();
        String number2 = wrapper.wrappedObject.getNumber();
        return number1.compareTo(number2);
    }

    @Override
    public String toString() {
        return getNumber();
    }
}