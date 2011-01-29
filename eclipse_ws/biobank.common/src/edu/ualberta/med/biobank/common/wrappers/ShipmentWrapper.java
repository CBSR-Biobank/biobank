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
import edu.ualberta.med.biobank.common.peer.ShipmentPeer;
import edu.ualberta.med.biobank.common.wrappers.internal.ShipmentPatientWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShipmentPatient;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ShipmentWrapper extends AbstractShipmentWrapper<Shipment> {
    private static final String PROP_KEY_CSP_COLLECTION = "cspCollection";

    private Set<PatientWrapper> patientsAdded = new HashSet<PatientWrapper>();
    private Set<PatientWrapper> patientsRemoved = new HashSet<PatientWrapper>();

    public ShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentWrapper(WritableApplicationService appService,
        Shipment wrappedObject) {
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
    protected List<String> getPropertyChangeNames() {
        return ShipmentPeer.PROP_NAMES;
    }

    @Override
    public Class<Shipment> getWrappedClass() {
        return Shipment.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
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
                throw new BiobankCheckException("A shipment with waybill "
                    + getWaybill() + " already exist in clinic "
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

    @Override
    protected void persistDependencies(Shipment origObject) throws Exception {
        if (!isNew()) {
            for (PatientWrapper patient : patientsRemoved) {
                for (ShipmentPatientWrapper csp : patient
                    .getShipmentPatientCollection()) {
                    if (csp.getShipment() != null
                        && csp.getShipment().getId().equals(getId())) {
                        csp.delete();
                    }
                }
            }
        }
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
        HQLCriteria c = new HQLCriteria("from " + Shipment.class.getName()
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

    public List<PatientVisitWrapper> getPatientVisitCollection() {
        List<PatientVisitWrapper> patientVisitCollection = new ArrayList<PatientVisitWrapper>();
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null) {
            for (ShipmentPatientWrapper csp : csps) {
                for (PatientVisitWrapper visit : csp
                    .getPatientVisitCollection()) {
                    patientVisitCollection.add(visit);
                }
            }
        }
        return patientVisitCollection;
    }

    public void addPatientVisits(List<PatientVisitWrapper> newPatientVisits)
        throws BiobankCheckException {
        if (newPatientVisits != null && newPatientVisits.size() > 0) {
            Collection<PatientVisit> allVisitObjects = new HashSet<PatientVisit>();
            List<PatientVisitWrapper> allVisitWrappers = new ArrayList<PatientVisitWrapper>();
            // already added visits
            List<PatientVisitWrapper> currentList = getPatientVisitCollection();
            Collection<PatientVisit> oldCollection = new ArrayList<PatientVisit>();
            for (PatientVisitWrapper visit : currentList) {
                oldCollection.add(visit.getWrappedObject());
                allVisitObjects.add(visit.getWrappedObject());
                allVisitWrappers.add(visit);
            }
            // new
            Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
            Collection<PatientVisitWrapper> pvs;
            for (PatientVisitWrapper newVisit : newPatientVisits) {
                boolean isFound = false;
                if (csps != null) {
                    for (ShipmentPatientWrapper csp : csps) {
                        if (csp.isSameShipmentAndPatient(newVisit
                            .getShipmentPatient())) {
                            pvs = csp.getPatientVisitCollection();
                            pvs.add(newVisit);
                            csp.setPatientVisitCollection(pvs);

                            allVisitObjects.add(newVisit.getWrappedObject());
                            allVisitWrappers.add(newVisit);
                            isFound = true;
                        }
                    }
                }
                if (!isFound) {
                    throw new BiobankCheckException(
                        "Cannot add this visit until patient "
                            + newVisit.getPatient().getPnumber()
                            + " has been linked to shipment "
                            + newVisit.getShipment().getWaybill() + ".");
                }
            }
            propertyChangeSupport.firePropertyChange("patientVisitCollection",
                oldCollection, allVisitObjects);
        }
    }

    public List<PatientWrapper> getPatientCollection(boolean sort) {
        List<PatientWrapper> patientCollection = new ArrayList<PatientWrapper>();
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null) {
            for (ShipmentPatientWrapper csp : csps) {
                patientCollection.add(csp.getPatient());
            }
        }
        if ((patientCollection != null) && sort)
            Collections.sort(patientCollection);
        return patientCollection;
    }

    private Collection<ShipmentPatientWrapper> getShipmentPatientCollection() {
        // TODO: this method is (almost) exactly the same as
        // PatientWrapper.getShipmentPatientCollection() - should share
        // code
        @SuppressWarnings("unchecked")
        Collection<ShipmentPatientWrapper> csps = (Collection<ShipmentPatientWrapper>) propertiesMap
            .get(PROP_KEY_CSP_COLLECTION);
        if (csps == null && wrappedObject != null) {
            Collection<ShipmentPatient> rawCsps = wrappedObject
                .getShipmentPatientCollection();
            if (rawCsps != null) {
                csps = ShipmentPatientWrapper.wrapShipmentPatientCollection(
                    appService, rawCsps);
            }
            propertiesMap.put(PROP_KEY_CSP_COLLECTION, csps);
        }
        return csps;
    }

    private void setShipmentPatientCollection(
        Collection<ShipmentPatientWrapper> newCsps) throws ApplicationException {
        Collection<ShipmentPatient> oldRawCsps = new ArrayList<ShipmentPatient>();
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null) {
            for (ShipmentPatientWrapper csp : csps) {
                oldRawCsps.add(csp.getWrappedObject());
            }
        }

        Collection<ShipmentPatient> newRawCsps = new HashSet<ShipmentPatient>();
        for (ShipmentPatientWrapper csp : newCsps) {
            newRawCsps.add(csp.getWrappedObject());
        }

        newCsps = updateExistingShipmentPatients(newCsps);

        wrappedObject.setShipmentPatientCollection(newRawCsps);
        propertiesMap.put(PROP_KEY_CSP_COLLECTION, newCsps);
        propertyChangeSupport.firePropertyChange(PROP_KEY_CSP_COLLECTION,
            oldRawCsps, newRawCsps);
    }

    private Collection<ShipmentPatientWrapper> updateExistingShipmentPatients(
        Collection<ShipmentPatientWrapper> csps) throws ApplicationException {

        Collection<ShipmentPatientWrapper> updatedCsps = new ArrayList<ShipmentPatientWrapper>();

        HQLCriteria criteria = new HQLCriteria("select c from "
            + ShipmentPatient.class.getName()
            + " as c left join fetch c.patient p where c.shipment.id = ?");
        criteria.setParameters(Arrays.asList((Object) getId()));

        List<ShipmentPatient> rows = appService.query(criteria);
        for (ShipmentPatientWrapper csp : csps) {
            ShipmentPatient rawCsp = csp.getWrappedObject();
            for (ShipmentPatient dbCsp : rows) {
                if (csp.getPatient().getId().equals(dbCsp.getPatient().getId())) {
                    rawCsp = dbCsp;
                }
            }
            updatedCsps.add(new ShipmentPatientWrapper(appService, rawCsp));
        }

        return updatedCsps;
    }

    public List<PatientWrapper> getPatientCollection() {
        return getPatientCollection(true);
    }

    private void setPatients(Collection<Patient> allPatientObjects,
        List<PatientWrapper> allPatientWrappers) throws ApplicationException {
        Collection<Patient> oldPatients = new ArrayList<Patient>();
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null) {
            for (ShipmentPatientWrapper csp : csps) {
                oldPatients.add(csp.getPatient().getWrappedObject());
            }
        }

        Collection<ShipmentPatientWrapper> cspCollection = new ArrayList<ShipmentPatientWrapper>();
        for (PatientWrapper patient : allPatientWrappers) {
            ShipmentPatientWrapper csp = null;

            if (getId() != null) {
                if (csps != null && patient.getId() != null) {
                    // first try to reuse our own CSPs
                    csp = findCspIn(getId(), patient.getId(), csps);
                }

                if (csp == null) {
                    // next try to reuse the patient CSPs
                    csp = findCspIn(getId(), patient.getId(),
                        patient.getShipmentPatientCollection());
                }
            }

            if (csp == null) {
                csp = new ShipmentPatientWrapper(appService,
                    new ShipmentPatient());
                csp.setShipment(this);
                csp.setPatient(patient);
            }

            cspCollection.add(csp);
        }

        setShipmentPatientCollection(cspCollection);

        propertyChangeSupport.firePropertyChange("patientCollection",
            oldPatients, allPatientObjects);
    }

    /**
     * Find a <code>ShipmentPatientWrapper</code> in the given collection of
     * <code>ShipmentPatientWrapper</code>-s that has the supplied
     * <code>Shipment</code> id and <code>Patient</code> id.
     * 
     * @param shipmentId
     * @param patientId
     * @param haystack collection of <code>ShipmentPatientWrapper</code>-s to
     *            search
     * @return
     */
    private static ShipmentPatientWrapper findCspIn(Integer shipmentId,
        Integer patientId, Collection<ShipmentPatientWrapper> haystack) {
        ShipmentPatientWrapper match = null;
        if (shipmentId != null && patientId != null && haystack != null) {
            for (ShipmentPatientWrapper csp : haystack) {
                if (csp.getShipment() != null && csp.getPatient() != null
                    && shipmentId.equals(csp.getShipment().getId())
                    && patientId.equals(csp.getPatient().getId())) {
                    match = csp;
                }
            }
        }
        return match;
    }

    public void addPatients(List<PatientWrapper> newPatients)
        throws ApplicationException {
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

    public void removePatients(List<PatientWrapper> patientsToRemove)
        throws ApplicationException {
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
    public static List<ShipmentWrapper> getShipmentsInSites(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Shipment.class.getName()
            + " where site is not null and waybill = ?",
            Arrays.asList(new Object[] { waybill }));
        List<Shipment> shipments = appService.query(criteria);
        List<ShipmentWrapper> wrappers = new ArrayList<ShipmentWrapper>();
        for (Shipment s : shipments) {
            wrappers.add(new ShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute.
     */
    public static List<ShipmentWrapper> getShipmentsInSites(
        WritableApplicationService appService, Date dateReceived)
        throws ApplicationException {
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
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Shipment.class.getName()
                + " where site is not null and dateReceived >= ? and dateReceived <= ?",
            Arrays.asList(new Object[] { startDate, endDate }));
        List<Shipment> shipments = appService.query(criteria);
        List<ShipmentWrapper> wrappers = new ArrayList<ShipmentWrapper>();
        for (Shipment s : shipments) {
            wrappers.add(new ShipmentWrapper(appService, s));
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

    public static List<ShipmentWrapper> getTodayShipments(
        WritableApplicationService appService) throws ApplicationException {
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
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Shipment.class.getName()
                + " where site is not null and dateReceived >= ? and dateReceived <= ?",
            Arrays.asList(new Object[] { startDate, endDate }));
        List<Shipment> res = appService.query(criteria);
        List<ShipmentWrapper> ships = new ArrayList<ShipmentWrapper>();
        for (Shipment s : res) {
            ships.add(new ShipmentWrapper(appService, s));
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

    public Boolean needDeparted() {
        ShippingMethodWrapper shippingMethod = getShippingMethod();
        return shippingMethod == null || shippingMethod.needDate();
    }

}
