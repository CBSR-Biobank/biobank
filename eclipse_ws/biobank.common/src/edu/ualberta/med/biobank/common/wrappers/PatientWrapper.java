package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.wrappers.internal.ShipmentPatientWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.ShipmentPatient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends ModelWrapper<Patient> {
    private static final String PROP_KEY_CSP_COLLECTION = "cspCollection";

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
        StudyWrapper study = (StudyWrapper) propertiesMap.get("study");
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
            propertiesMap.put("study", study);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        propertiesMap.put("study", study);
        Study oldStudyRaw = wrappedObject.getStudy();
        Study newStudyRaw = null;
        if (study != null) {
            newStudyRaw = study.wrappedObject;
        }
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
        List<ShipmentWrapper> shipments = getShipmentCollection(null);
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
        return getPatientVisitCollection(true, false, null);
    }

    public List<PatientVisitWrapper> getPatientVisitCollection(boolean sort,
        final boolean ascending, SiteWrapper site) {
        // TODO: gee, I hope that when you modify this collection it isn't meant
        // to modify the internals of the PatientWrapper object. Ask Delphine.
        List<PatientVisitWrapper> patientVisitCollection = null;
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null && csps.size() > 0) {
            patientVisitCollection = new ArrayList<PatientVisitWrapper>();
            for (ShipmentPatientWrapper csp : csps) {
                if (site == null || (csp.getShipment().getSite().equals(site)))
                    patientVisitCollection.addAll(csp
                        .getPatientVisitCollection());
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

    Collection<ShipmentPatientWrapper> getShipmentPatientCollection() {
        @SuppressWarnings("unchecked")
        Collection<ShipmentPatientWrapper> csps = (Collection<ShipmentPatientWrapper>) propertiesMap
            .get(PROP_KEY_CSP_COLLECTION);
        if (csps == null) {
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

    public void addPatientVisits(
        Collection<PatientVisitWrapper> newPatientVisits)
        throws BiobankCheckException {
        if (newPatientVisits != null && newPatientVisits.size() > 0) {
            Collection<PatientVisit> allPvObjects = new ArrayList<PatientVisit>();
            List<PatientVisitWrapper> allPvWrappers = new ArrayList<PatientVisitWrapper>();
            // already added visits
            Collection<PatientVisit> oldCollection = new ArrayList<PatientVisit>();
            List<PatientVisitWrapper> currentList = getPatientVisitCollection();
            for (PatientVisitWrapper visit : currentList) {
                oldCollection.add(visit.getWrappedObject());
                allPvObjects.add(visit.getWrappedObject());
                allPvWrappers.add(visit);
            }
            // new
            Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
            Collection<PatientVisitWrapper> pvs;
            for (PatientVisitWrapper newVisit : newPatientVisits) {
                boolean isFound = false;
                for (ShipmentPatientWrapper csp : csps) {
                    if (csp.isSameShipmentAndPatient(newVisit
                        .getShipmentPatient())) {
                        pvs = csp.getPatientVisitCollection();
                        pvs.add(newVisit);
                        csp.setPatientVisitCollection(pvs);

                        allPvObjects.add(newVisit.getWrappedObject());
                        allPvWrappers.add(newVisit);
                        isFound = true;
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
                oldCollection, allPvObjects);
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
     * Search a patient in the site with the given number. Will return the
     * patient only if the current user has read access on a site that works
     * with this patient study
     */
    public static PatientWrapper getPatient(
        WritableApplicationService appService, String patientNumber, User user)
        throws ApplicationException {
        PatientWrapper patient = getPatient(appService, patientNumber);
        if (patient != null) {
            StudyWrapper study = patient.getStudy();
            List<SiteWrapper> sites = study.getSiteCollection();
            boolean canRead = false;
            for (SiteWrapper site : sites) {
                if (user.hasPrivilegeOnObject(Privilege.READ, null, Site.class,
                    site.getId())) {
                    canRead = true;
                    break;
                }
            }
            if (!canRead) {
                throw new ApplicationException("Patient " + patientNumber
                    + " exists but you don't have access to it."
                    + " Check studies linked to the sites you can access.");
            }
        }
        return patient;
    }

    /**
     * Get the shipment collection. To link patients and shipments, use
     * Shipment.setPatientCollection method If user is not null, will return
     * only shipments that are linked to a site this user can update, unless
     * user is null
     */
    public List<ShipmentWrapper> getShipmentCollection(boolean sort,
        final boolean ascending, User user) {
        List<ShipmentWrapper> shipmentCollection = new ArrayList<ShipmentWrapper>();
        Collection<ShipmentPatientWrapper> csps = getShipmentPatientCollection();
        if (csps != null) {
            for (ShipmentPatientWrapper csp : csps) {
                ShipmentWrapper ship = csp.getShipment();
                if (user == null || user.canUpdateSite(ship.getSite())) {
                    shipmentCollection.add(ship);
                }
            }
        }
        if (sort && shipmentCollection != null) {
            Collections.sort(shipmentCollection,
                new Comparator<ShipmentWrapper>() {
                    @Override
                    public int compare(ShipmentWrapper ship1,
                        ShipmentWrapper ship2) {
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

    /**
     * if user is no null, will return only shipment this user can update.
     */
    public List<ShipmentWrapper> getShipmentCollection(User user) {
        return getShipmentCollection(false, true, user);
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
        if (getAliquotsCount(false) > 0)
            throw new BiobankCheckException("Unable to delete patient "
                + getPnumber()
                + " because patient has samples stored in database.");
        if (hasShipments())
            throw new BiobankCheckException("Unable to delete patient "
                + getPnumber()
                + " because patient has shipments recorded in database.");
    }

    private boolean hasShipments() {
        if (getShipmentCollection(null) != null
            && getShipmentCollection(null).size() > 0)
            return true;
        return false;
    }

    private void checkNoMorePatientVisits() throws BiobankCheckException {
        List<PatientVisitWrapper> pvs = getPatientVisitCollection();
        if (pvs != null && pvs.size() > 0) {
            throw new BiobankCheckException(
                "Visits are still linked to this patient."
                    + " Delete them before attempting to remove the patient.");
        }
    }

    public long getAliquotsCount(boolean fast) throws BiobankCheckException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(
                "select count(aliquots) from " + PatientVisit.class.getName()
                    + " as pv join pv.aliquotCollection as aliquots "
                    + "join pv.shipmentPatient as csp "
                    + "where csp.patient.id = ? ",
                Arrays.asList(new Object[] { getId() }));
            List<Long> results = appService.query(criteria);
            if (results.size() != 1) {
                throw new BiobankCheckException(
                    "Invalid size for HQL query result");
            }
            return results.get(0);
        }
        long total = 0;
        List<PatientVisitWrapper> pvs = getPatientVisitCollection();
        if (pvs != null)
            for (PatientVisitWrapper pv : pvs)
                total += pv.getAliquotsCount(false);
        return total;
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

    public boolean canBeAddedToShipment(ShipmentWrapper shipment)
        throws ApplicationException, BiobankCheckException {
        if (shipment.getClinic() == null) {
            return true;
        }
        return getStudy().isLinkedToClinic(shipment.getClinic());
    }

    public static List<PatientWrapper> getPatientsInTodayShipments(
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
        HQLCriteria criteria = new HQLCriteria("select p from "
            + Patient.class.getName()
            + " as p join p.shipmentPatientCollection as csps"
            + " join csps.shipment as ships" + " where ships.site is not null"
            + " and ships.dateReceived >= ? and ships.dateReceived <= ?",
            Arrays.asList(new Object[] { startDate, endDate }));
        List<Patient> res = appService.query(criteria);
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        for (Patient p : res) {
            patients.add(new PatientWrapper(appService, p));
        }
        return patients;
    }

    public List<PatientVisitWrapper> getLast7DaysPatientVisits(SiteWrapper site)
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
                + " as p join p.shipmentPatientCollection as csps"
                + " join csps.patientVisitCollection as visits"
                + " where p.id = ? and csps.shipment.site.id = ? and visits.dateProcessed > ? and visits.dateProcessed < ?",
            Arrays.asList(new Object[] { getId(), site.getId(), startDate,
                endDate }));
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
        log.setSite(site);
        log.setPatientNumber(getPnumber());
        log.setDetails(details);
        log.setType("Patient");
        return log;
    }

    @Override
    public boolean checkSpecificAccess(User user, Integer siteId) {
        if (isNew()) {
            return true;
        }
        // won't use siteId because patient is not site specific (will be null)
        StudyWrapper study = getStudy();
        if (study != null) {
            List<SiteWrapper> sites = study.getSiteCollection();
            for (SiteWrapper site : sites) {
                // if can update at least one site, then can add/update a
                // patient to the linked study
                if (user.canUpdateSite(site.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * merge patient2 into this patient
     */
    public void merge(PatientWrapper patient2) throws Exception {
        reload();
        patient2.reload();
        if (getStudy().equals(patient2.getStudy())) {
            List<PatientVisitWrapper> pvs = patient2
                .getPatientVisitCollection();
            if (pvs != null)
                for (PatientVisitWrapper pv : pvs) {
                    ShipmentWrapper shipment = pv.getShipment();
                    shipment.addPatients(Arrays.asList(this));
                    shipment.persist();

                    pv.setPatient(this);
                    pv.persist();

                    patient2.reload();
                    shipment.reload();
                    shipment.removePatients(Arrays.asList(patient2));
                    shipment.persist();
                }

            patient2.reload();
            patient2.delete();

            ((BiobankApplicationService) appService).logActivity("merge", null,
                patient2.getPnumber(), null, null, patient2.getPnumber()
                    + " --> " + getPnumber(), "Patient");
            ((BiobankApplicationService) appService).logActivity("merge", null,
                getPnumber(), null, null,
                getPnumber() + " <-- " + patient2.getPnumber(), "Patient");
        } else {
            throw new BiobankCheckException(
                "Cannot merge patients from different studies.");
        }
    }
}