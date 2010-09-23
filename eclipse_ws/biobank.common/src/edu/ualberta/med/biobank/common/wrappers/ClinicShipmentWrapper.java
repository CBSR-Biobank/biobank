package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicShipmentWrapper extends
    AbstractShipmentWrapper<ClinicShipment> {

    private Set<PatientWrapper> patientsAdded = new HashSet<PatientWrapper>();
    private Set<PatientWrapper> patientsRemoved = new HashSet<PatientWrapper>();

    public ClinicShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicShipmentWrapper(WritableApplicationService appService,
        ClinicShipment wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void deleteChecks() throws Exception {
        checkNoMorePatientVisits();
    }

    private void checkNoMorePatientVisits() throws Exception {
        List<PatientVisitWrapper> visits = getPatientVisitCollection();
        if (visits != null && visits.size() > 0) {
            throw new BiobankCheckException(
                "Visits are still linked to this shipment. Delete them before attempting to remove the shipment.");
        }
    }

    @Override
    protected String[] getPropertyChangeNames() {
        String[] properties = super.getPropertyChangeNames();
        List<String> list = new ArrayList<String>(Arrays.asList(properties));
        list.addAll(Arrays.asList("clinic", "patientVisitCollection",
            "patientCollection", "activityStatus"));
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Class<ClinicShipment> getWrappedClass() {
        return ClinicShipment.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        super.persistChecks();
        if (getClinic() == null) {
            throw new BiobankCheckException("A clinic should be set");
        }
        if (getSite() == null) {
            throw new BiobankCheckException("A site should be set");
        }
        if (getClinic() != null
            && Boolean.TRUE.equals(getClinic().getSendsShipments())) {
            if (getWaybill() == null || getWaybill().isEmpty()) {
                throw new BiobankCheckException(
                    "A waybill should be set on this shipment");
            }
            if (!checkWaybillUniqueForClinic()) {
                throw new BiobankCheckException(
                    "A clinic shipment with waybill " + getWaybill()
                        + " already exist in clinic "
                        + getClinic().getNameShort());
            }
        } else {
            if (getWaybill() != null) {
                throw new BiobankCheckException(
                    "This shipment clinic doesn't send shipment: waybill should not be set");
            }
        }
        checkAlLeastOnePatient();
        checkPatientsStudy();
        checkRemovedPatients();
    }

    private void checkRemovedPatients() throws BiobankCheckException {
        if (!isNew()) {
            for (PatientWrapper patient : patientsRemoved) {
                checkCanRemovePatient(patient);
            }
        }
    }

    public void checkCanRemovePatient(PatientWrapper patient)
        throws BiobankCheckException {
        if (hasVisitForPatient(patient)) {
            throw new BiobankCheckException("Cannot remove patient "
                + patient.getPnumber()
                + ": a visit related to this shipment has "
                + "already been created. Remove visit first "
                + "and then you will be able to remove this "
                + "patient from this shipment.");
        }
    }

    public boolean hasVisitForPatient(PatientWrapper patient) {
        List<PatientVisitWrapper> pvs = patient.getPatientVisitCollection();
        if (pvs != null)
            for (PatientVisitWrapper pv : pvs)
                if (pv.getShipment() != null && pv.getShipment().equals(this))
                    return true;
        return false;
    }

    public void checkAlLeastOnePatient() throws BiobankCheckException {
        List<PatientWrapper> patients = getPatientCollection();
        if (patients == null || patients.size() == 0) {
            throw new BiobankCheckException(
                "At least one patient should be added to this shipment");
        }
    }

    public void checkPatientsStudy() throws BiobankCheckException,
        ApplicationException {
        String patientsInError = "";
        for (PatientWrapper patient : patientsAdded) {
            if (!patient.canBeAddedToShipment(this)) {
                patientsInError += patient.getPnumber() + ", ";
            }
        }
        if (!patientsInError.isEmpty()) {
            // remove last ", "
            patientsInError = patientsInError.substring(0,
                patientsInError.length() - 2);
            throw new BiobankCheckException("Patient(s) " + patientsInError
                + " are not part of a study that has contact with clinic "
                + getClinic().getName());
        }
    }

    private boolean checkWaybillUniqueForClinic() throws ApplicationException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getClinic().getId());
        params.add(getWaybill());
        if (!isNew()) {
            isSameShipment = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from "
            + ClinicShipment.class.getName()
            + " where clinic.id=? and waybill = ?" + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public ClinicWrapper getClinic() {
        ClinicWrapper clinic = (ClinicWrapper) propertiesMap.get("clinic");
        if (clinic == null) {
            Clinic c = wrappedObject.getClinic();
            if (c == null)
                return null;
            clinic = new ClinicWrapper(appService, c);
            propertiesMap.put("clinic", clinic);
        }
        return clinic;
    }

    public void setClinic(ClinicWrapper clinic) {
        propertiesMap.put("clinic", clinic);
        Clinic oldClinic = wrappedObject.getClinic();
        Clinic newClinic = null;
        if (clinic != null) {
            newClinic = clinic.getWrappedObject();
        }
        wrappedObject.setClinic(newClinic);
        propertyChangeSupport
            .firePropertyChange("clinic", oldClinic, newClinic);
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        return (site != null) ? new SiteWrapper(appService, site) : null;
    }

    public void setSite(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatusWrapper activity = (ActivityStatusWrapper) propertiesMap
            .get("activityStatus");
        if (activity == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activity = new ActivityStatusWrapper(appService, a);
        }
        return activity;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        propertiesMap.put("activityStatus", activityStatus);
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
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

    private void setPatientVisitCollection(
        Collection<PatientVisit> allVisitObjects,
        List<PatientVisitWrapper> allVisitWrappers) {
        Collection<PatientVisit> oldCollection = wrappedObject
            .getPatientVisitCollection();
        wrappedObject.setPatientVisitCollection(allVisitObjects);
        propertyChangeSupport.firePropertyChange("patientVisitCollection",
            oldCollection, allVisitObjects);
        propertiesMap.put("patientVisitCollection", allVisitWrappers);
    }

    public void addPatientVisits(List<PatientVisitWrapper> newPatientVisits) {
        if (newPatientVisits != null && newPatientVisits.size() > 0) {
            Collection<PatientVisit> allVisitObjects = new HashSet<PatientVisit>();
            List<PatientVisitWrapper> allVisitWrappers = new ArrayList<PatientVisitWrapper>();
            // already added visits
            List<PatientVisitWrapper> currentList = getPatientVisitCollection();
            if (currentList != null) {
                for (PatientVisitWrapper visit : currentList) {
                    allVisitObjects.add(visit.getWrappedObject());
                    allVisitWrappers.add(visit);
                }
            }
            // new
            for (PatientVisitWrapper visit : newPatientVisits) {
                allVisitObjects.add(visit.getWrappedObject());
                allVisitWrappers.add(visit);
            }
            setPatientVisitCollection(allVisitObjects, allVisitWrappers);
        }
    }

    @SuppressWarnings("unchecked")
    public List<PatientWrapper> getPatientCollection(boolean sort) {
        List<PatientWrapper> patientCollection = (List<PatientWrapper>) propertiesMap
            .get("patientCollection");
        if (patientCollection == null) {
            Collection<Patient> children = wrappedObject.getPatientCollection();
            if (children != null) {
                patientCollection = new ArrayList<PatientWrapper>();
                for (Patient patient : children) {
                    patientCollection.add(new PatientWrapper(appService,
                        patient));
                }
                propertiesMap.put("patientCollection", patientCollection);
            }
        }
        if ((patientCollection != null) && sort)
            Collections.sort(patientCollection);
        return patientCollection;
    }

    public List<PatientWrapper> getPatientCollection() {
        return getPatientCollection(true);
    }

    private void setPatients(Collection<Patient> allPatientObjects,
        List<PatientWrapper> allPatientWrappers) {
        Collection<Patient> oldPatients = wrappedObject.getPatientCollection();
        wrappedObject.setPatientCollection(allPatientObjects);
        propertyChangeSupport.firePropertyChange("patientCollection",
            oldPatients, allPatientObjects);
        propertiesMap.put("patientCollection", allPatientWrappers);
    }

    public void addPatients(List<PatientWrapper> newPatients) {
        if (newPatients != null && newPatients.size() > 0) {
            Collection<Patient> allPatientsObjects = new HashSet<Patient>();
            List<PatientWrapper> allPatientsWrappers = new ArrayList<PatientWrapper>();
            // already in list
            List<PatientWrapper> patientsList = getPatientCollection();
            if (patientsList != null) {
                for (PatientWrapper patient : patientsList) {
                    allPatientsObjects.add(patient.getWrappedObject());
                    allPatientsWrappers.add(patient);
                }
            }
            // new patients
            for (PatientWrapper patient : newPatients) {
                patientsAdded.add(patient);
                patientsRemoved.remove(patient);
                allPatientsObjects.add(patient.getWrappedObject());
                allPatientsWrappers.add(patient);
            }
            setPatients(allPatientsObjects, allPatientsWrappers);
        }
    }

    public void removePatients(List<PatientWrapper> patientsToRemove) {
        if (patientsToRemove != null && patientsToRemove.size() > 0) {
            patientsAdded.removeAll(patientsToRemove);
            patientsRemoved.addAll(patientsToRemove);
            Collection<Patient> allPatientsObjects = new HashSet<Patient>();
            List<PatientWrapper> allPatientsWrappers = new ArrayList<PatientWrapper>();
            // already in list
            List<PatientWrapper> patientsList = getPatientCollection();
            if (patientsList != null) {
                for (PatientWrapper patient : patientsList) {
                    if (!patientsToRemove.contains(patient)) {
                        allPatientsObjects.add(patient.getWrappedObject());
                        allPatientsWrappers.add(patient);
                    }
                }
            }
            setPatients(allPatientsObjects, allPatientsWrappers);
        }
    }

    /**
     * Search for shipments in the site with the given waybill
     */
    public static List<ClinicShipmentWrapper> getShipmentsInSite(
        WritableApplicationService appService, String waybill, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + ClinicShipment.class.getName()
            + " where site.id = ? and waybill = ?", Arrays.asList(new Object[] {
            site.getId(), waybill }));
        List<ClinicShipment> shipments = appService.query(criteria);
        List<ClinicShipmentWrapper> wrappers = new ArrayList<ClinicShipmentWrapper>();
        for (ClinicShipment s : shipments) {
            wrappers.add(new ClinicShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute.
     */
    public static List<ClinicShipmentWrapper> getShipmentsInSite(
        WritableApplicationService appService, Date dateReceived,
        SiteWrapper site) throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // date at 0:0am
        cal.setTime(dateReceived);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        // date at 0:0pm
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        HQLCriteria criteria = new HQLCriteria("from "
            + ClinicShipment.class.getName()
            + " where site.id = ? and dateReceived >= ? and dateReceived <= ?",
            Arrays.asList(new Object[] { site.getId(), startDate, endDate }));
        List<ClinicShipment> shipments = appService.query(criteria);
        List<ClinicShipmentWrapper> wrappers = new ArrayList<ClinicShipmentWrapper>();
        for (ClinicShipment s : shipments) {
            wrappers.add(new ClinicShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     */
    public boolean hasPatient(String patientNumber) {
        List<PatientWrapper> ps = getPatientCollection();
        if (ps != null)
            for (PatientWrapper p : ps)
                if (p.getPnumber().equals(patientNumber))
                    return true;
        return false;
    }

    @Override
    public void resetInternalFields() {
        patientsAdded.clear();
        patientsRemoved.clear();
    }

    public static List<ClinicShipmentWrapper> getTodayShipments(
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
        HQLCriteria criteria = new HQLCriteria("from "
            + ClinicShipment.class.getName()
            + " where site.id = ? and dateReceived >= ? and dateReceived <= ?",
            Arrays.asList(new Object[] { site.getId(), startDate, endDate }));
        List<ClinicShipment> res = appService.query(criteria);
        List<ClinicShipmentWrapper> ships = new ArrayList<ClinicShipmentWrapper>();
        for (ClinicShipment s : res) {
            ships.add(new ClinicShipmentWrapper(appService, s));
        }
        return ships;
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setSite(getSite().getNameShort());
        } else {
            log.setSite(site);
        }
        details += "Received:" + getFormattedDateReceived();
        String waybill = getWaybill();
        if (waybill != null) {
            details += " - Waybill:" + waybill;
        }
        log.setDetails(details);
        log.setType("Shipment");
        return log;
    }

}
