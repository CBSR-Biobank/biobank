package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends ModelWrapper<Patient> {

    private StudyWrapper study = null;

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public PatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getPnumber() {
        return wrappedObject.getPnumber();
    }

    public void setPnumber(String number) {
        String oldNumber = getPnumber();
        wrappedObject.setPnumber(number);
        propertyChangeSupport.firePropertyChange("pnumber", oldNumber, number);
    }

    public StudyWrapper getStudy() {
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        this.study = study;
        Study oldStudyRaw = wrappedObject.getStudy();
        Study newStudyRaw = study.wrappedObject;
        wrappedObject.setStudy(newStudyRaw);
        propertyChangeSupport.firePropertyChange("study", oldStudyRaw,
            newStudyRaw);
    }

    public boolean checkPatientNumberUnique() throws ApplicationException {
        String isSamePatient = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getPnumber());
        if (!isNew()) {
            isSamePatient = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + Patient.class.getName()
            + " where pnumber = ?" + isSamePatient, params);

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
    protected String[] getPropertyChangeNames() {
        return new String[] { "pnumber", "study", "patientVisitCollection",
            "shptSourceVesselCollection", "shipmentCollection" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getPnumber() == null || getPnumber().isEmpty()) {
            throw new BiobankCheckException(
                "Pnumber of patient should not be empty");
        }
        if (!checkPatientNumberUnique()) {
            throw new BiobankCheckException("A patient with number \""
                + getPnumber() + "\" already exists.");
        }
        checkVisitsFromLinkedShipment();
    }

    private void checkVisitsFromLinkedShipment() throws BiobankCheckException {
        List<ClinicShipmentWrapper> shipments = getShipmentCollection();
        List<PatientVisitWrapper> visits = getPatientVisitCollection();
        if (visits != null && visits.size() > 0) {
            if (shipments == null || shipments.size() == 0) {
                throw new BiobankCheckException(
                    "This patient should be linked to a shipment if we want to add a visit linked to this same shipment.");
            }
            for (PatientVisitWrapper visit : visits) {
                if (!shipments.contains(visit.getShipment())) {
                    throw new BiobankCheckException(
                        "Visits should be linked to shipment in which this patient participate.");
                }
            }
        }
    }

    public List<PatientVisitWrapper> getPatientVisitCollection() {
        return getPatientVisitCollection(true, false);
    }

    @SuppressWarnings("unchecked")
    public List<PatientVisitWrapper> getPatientVisitCollection(boolean sort,
        final boolean ascending) {
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
        if (sort && patientVisitCollection != null) {
            Collections.sort(patientVisitCollection,
                new Comparator<PatientVisitWrapper>() {
                    @Override
                    public int compare(PatientVisitWrapper pv1,
                        PatientVisitWrapper pv2) {
                        int res = pv1.compareTo(pv2);
                        if (ascending) {
                            return res;
                        }
                        return -res;
                    }
                });
        }
        return patientVisitCollection;
    }

    public void addPatientVisits(
        Collection<PatientVisitWrapper> newPatientVisits) {
        if (newPatientVisits != null && newPatientVisits.size() > 0) {
            Collection<PatientVisit> allPvObjects = new HashSet<PatientVisit>();
            List<PatientVisitWrapper> allPvWrappers = new ArrayList<PatientVisitWrapper>();
            // already added visits
            List<PatientVisitWrapper> currentList = getPatientVisitCollection();
            if (currentList != null) {
                for (PatientVisitWrapper visit : currentList) {
                    allPvObjects.add(visit.getWrappedObject());
                    allPvWrappers.add(visit);
                }
            }
            // new
            for (PatientVisitWrapper visit : newPatientVisits) {
                visit.setPatient(this);
                allPvObjects.add(visit.getWrappedObject());
                allPvWrappers.add(visit);
            }
            Collection<PatientVisit> oldCollection = wrappedObject
                .getPatientVisitCollection();
            wrappedObject.setPatientVisitCollection(allPvObjects);
            propertyChangeSupport.firePropertyChange("patientVisitCollection",
                oldCollection, allPvObjects);
            propertiesMap.put("patientVisitCollection", allPvWrappers);
        }
    }

    /**
     * Search patient visits with the given date processed.
     */
    public List<PatientVisitWrapper> getVisits(Date dateProcessed,
        Date dateDrawn) {
        List<PatientVisitWrapper> visits = getPatientVisitCollection();
        List<PatientVisitWrapper> result = new ArrayList<PatientVisitWrapper>();
        if (visits != null)
            for (PatientVisitWrapper visit : visits) {
                if ((DateCompare.compare(visit.getDateDrawn(), dateDrawn) == 0)
                    && (DateCompare.compare(visit.getDateProcessed(),
                        dateProcessed) == 0))
                    result.add(visit);
            }
        return result;
    }

    /**
     * Search a patient in the site with the given number
     */
    public static PatientWrapper getPatient(
        WritableApplicationService appService, String patientNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName() + " where pnumber = ?",
            Arrays.asList(new Object[] { patientNumber }));
        List<Patient> patients = appService.query(criteria);
        if (patients.size() == 1) {
            return new PatientWrapper(appService, patients.get(0));
        }
        return null;
    }

    /**
     * Get the shipment collection. To link patients and shipments, use
     * Shipment.setPatientCollection method
     */
    @SuppressWarnings("unchecked")
    public List<ClinicShipmentWrapper> getShipmentCollection(boolean sort,
        final boolean ascending) {
        List<ClinicShipmentWrapper> shipmentCollection = (List<ClinicShipmentWrapper>) propertiesMap
            .get("shipmentCollection");
        if (shipmentCollection == null) {
            Collection<ClinicShipment> children = wrappedObject
                .getShipmentCollection();
            if (children != null) {
                shipmentCollection = new ArrayList<ClinicShipmentWrapper>();
                for (ClinicShipment ship : children) {
                    shipmentCollection.add(new ClinicShipmentWrapper(
                        appService, ship));
                }
                propertiesMap.put("shipmentCollection", shipmentCollection);
            }
        }

        if (sort && shipmentCollection != null) {
            Collections.sort(shipmentCollection,
                new Comparator<ClinicShipmentWrapper>() {
                    @Override
                    public int compare(ClinicShipmentWrapper ship1,
                        ClinicShipmentWrapper ship2) {
                        int res = ship1.compareTo(ship2);
                        if (ascending) {
                            return res;
                        }
                        return -res;
                    }
                });
        }
        return shipmentCollection;
    }

    public List<ClinicShipmentWrapper> getShipmentCollection() {
        return getShipmentCollection(false, true);
    }

    @Override
    public Class<Patient> getWrappedClass() {
        return Patient.class;
    }

    @Override
    protected void deleteDependencies() throws Exception {
        List<PatientVisitWrapper> visits = getPatientVisitCollection();
        if (visits != null) {
            for (PatientVisitWrapper visit : visits) {
                visit.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        checkNoMorePatientVisits();
        if (hasAliquots())
            throw new BiobankCheckException("Unable to delete patient "
                + getPnumber()
                + " because patient has samples stored in database.");
        if (hasShipments())
            throw new BiobankCheckException("Unable to delete patient "
                + getPnumber()
                + " because patient has shipments recorded in database.");
    }

    private boolean hasShipments() {
        if (getShipmentCollection() != null
            && getShipmentCollection().size() > 0)
            return true;
        return false;
    }

    private void checkNoMorePatientVisits() throws BiobankCheckException {
        List<PatientVisitWrapper> patients = getPatientVisitCollection();
        if (patients != null && patients.size() > 0) {
            throw new BiobankCheckException(
                "Visits are still linked to this patient. Delete them before attempting to remove the patient.");
        }
    }

    public long getAliquotsCount() {
        long total = 0;
        List<PatientVisitWrapper> pvs = getPatientVisitCollection();
        if (pvs != null)
            for (PatientVisitWrapper pv : pvs)
                total += pv.getAliquotsCount();
        return total;
    }

    public boolean hasAliquots() {
        return (getAliquotsCount() > 0);
    }

    @Override
    public int compareTo(ModelWrapper<Patient> wrapper) {
        if (wrapper instanceof PatientWrapper) {
            String number1 = wrappedObject.getPnumber();
            String number2 = wrapper.wrappedObject.getPnumber();
            return number1.compareTo(number2);
        }
        return 0;
    }

    @Override
    public String toString() {
        return getPnumber();
    }

    public boolean canBeAddedToShipment(ClinicShipmentWrapper shipment)
        throws ApplicationException, BiobankCheckException {
        if (shipment.getClinic() == null) {
            return true;
        }
        return getStudy().isLinkedToClinic(shipment.getClinic());
    }

    public static List<PatientWrapper> getPatientsInTodayShipments(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // yesterday midnight
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        // today midnight
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        HQLCriteria criteria = new HQLCriteria("select p from "
            + Patient.class.getName()
            + " as p join p.shipmentCollection as ships"
            + " where ships.site.id = ?"
            + " and ships.dateReceived >= ? and ships.dateReceived <= ?",
            Arrays.asList(new Object[] { site.getId(), startDate, endDate }));
        List<Patient> res = appService.query(criteria);
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        for (Patient p : res) {
            patients.add(new PatientWrapper(appService, p));
        }
        return patients;
    }

    public List<PatientVisitWrapper> getLast7DaysPatientVisits()
        throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // today midnight
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date endDate = cal.getTime();
        // 7 days ago, at midnight
        cal.add(Calendar.DATE, -8);
        Date startDate = cal.getTime();
        HQLCriteria criteria = new HQLCriteria(
            "select visits from "
                + Patient.class.getName()
                + " as p join p.patientVisitCollection as visits"
                + " where p.id = ? and visits.dateProcessed > ? and visits.dateProcessed < ?",
            Arrays.asList(new Object[] { getId(), startDate, endDate }));
        List<PatientVisit> res = appService.query(criteria);
        List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
        for (PatientVisit v : res) {
            visits.add(new PatientVisitWrapper(appService, v));
        }
        return visits;
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        // FIXME site == null when persist and delete
        // don't know the site: patient belong to a study, that doesn't depend
        // on a site
        log.setSite(site);
        log.setPatientNumber(getPnumber());
        log.setDetails(details);
        log.setType("Patient");
        return log;
    }

    @Override
    public void resetInternalFields() {
        study = null;
    }

    public void setPatientVisitCollection(List<PatientVisitWrapper> pvws) {
        List<PatientVisit> pvs = new ArrayList<PatientVisit>();
        for (PatientVisitWrapper pvw : pvws)
            pvs.add(pvw.getWrappedObject());
        List<PatientVisitWrapper> oldCollection = getPatientVisitCollection();
        wrappedObject.setPatientVisitCollection(pvs);
        propertiesMap.put("patientVisitCollection", pvs);
        propertyChangeSupport.firePropertyChange("patientVisitCollection",
            oldCollection, pvs);
    }
}