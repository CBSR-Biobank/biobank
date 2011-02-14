package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SourceVesselPeer;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.wrappers.base.PatientBaseWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends PatientBaseWrapper {

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public PatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Patient.class, PatientPeer.PNUMBER.getName(),
            getPnumber(), "A patient with PNumber");
    }

    /**
     * Search patient visits with the given date processed.
     */
    public List<ProcessingEventWrapper> getVisits(Date dateProcessed,
        Date dateDrawn) {
        List<ProcessingEventWrapper> visits = getProcessingEventCollection(false);
        List<ProcessingEventWrapper> result = new ArrayList<ProcessingEventWrapper>();
        if (visits != null)
            for (ProcessingEventWrapper visit : visits) {
                if ((DateCompare.compare(visit.getDateDrawn(), dateDrawn) == 0)
                    && (DateCompare.compare(visit.getDateProcessed(),
                        dateProcessed) == 0))
                    result.add(visit);
            }
        return result;
    }

    private static final String PATIENT_QRY = "from " + Patient.class.getName()
        + " where " + PatientPeer.PNUMBER + "=?";

    /**
     * Search a patient in the site with the given number
     */
    public static PatientWrapper getPatient(
        WritableApplicationService appService, String patientNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(PATIENT_QRY,
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

    @Override
    protected void deleteDependencies() throws Exception {
        List<ProcessingEventWrapper> visits = getProcessingEventCollection(false);
        if (visits != null) {
            for (ProcessingEventWrapper visit : visits) {
                visit.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkNoMoreProcessingEvents();
        if (getAliquotsCount(false) > 0)
            throw new BiobankCheckException("Unable to delete patient "
                + getPnumber()
                + " because patient has samples stored in database.");
    }

    private void checkNoMoreProcessingEvents() throws BiobankCheckException {
        List<ProcessingEventWrapper> pvs = getProcessingEventCollection(false);
        if (pvs != null && pvs.size() > 0) {
            throw new BiobankCheckException(
                "Visits are still linked to this patient."
                    + " Delete them before attempting to remove the patient.");
        }
    }

    private static final String ALIQUOT_COUNT_QRY = "select count(aliquots) from "
        + ProcessingEvent.class.getName()
        + " as pv join pv."
        + ProcessingEventPeer.ALIQUOT_COLLECTION.getName()
        + " as aliquots where pv."
        + Property.concatNames(ProcessingEventPeer.PATIENT, PatientPeer.ID)
        + "=?";

    public long getAliquotsCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(ALIQUOT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            List<Long> results = appService.query(criteria);
            if (results.size() != 1) {
                throw new BiobankQueryResultSizeException();
            }
            return results.get(0);
        }
        long total = 0;
        List<ProcessingEventWrapper> pvs = getProcessingEventCollection(false);
        if (pvs != null)
            for (ProcessingEventWrapper pv : pvs)
                total += pv.getAliquotsCount(false);
        return total;
    }

    @Override
    public int compareTo(ModelWrapper<Patient> wrapper) {
        if (wrapper instanceof PatientWrapper) {
            String number1 = getPnumber();
            String number2 = wrapper.wrappedObject.getPnumber();
            return number1.compareTo(number2);
        }
        return 0;
    }

    @Override
    public String toString() {
        return getPnumber();
    }

    private static final String PATIENTS_IN_TODAYS_COLLECTION_EVENTS_QRY = "select p from "
        + Patient.class.getName()
        + " as p join p."
        + PatientPeer.SOURCE_VESSEL_COLLECTION.getName()
        + " as svc join svc."
        + SourceVesselPeer.COLLECTION_EVENT.getName()
        + " as ces where ces."
        + CollectionEventPeer.DATE_RECEIVED.getName()
        + ">=? and ces."
        + CollectionEventPeer.DATE_RECEIVED.getName() + "<=?";

    public static List<PatientWrapper> getPatientsInTodayCollectionEvents(
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
            PATIENTS_IN_TODAYS_COLLECTION_EVENTS_QRY,
            Arrays.asList(new Object[] { startDate, endDate }));
        List<Patient> res = appService.query(criteria);
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        for (Patient p : res) {
            patients.add(new PatientWrapper(appService, p));
        }
        return patients;
    }

    private static final String LAST_7_DAYS_PROCESSING_EVENTS_QRY = "select visits from "
        + Patient.class.getName()
        + " as p join p."
        + PatientPeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pes where p."
        + PatientPeer.ID.getName()
        + "=? and pes."
        + ProcessingEventPeer.DATE_PROCESSED.getName()
        + ">? and pes."
        + ProcessingEventPeer.DATE_PROCESSED.getName() + "<?";

    public List<ProcessingEventWrapper> getLast7DaysProcessingEvents(
        SiteWrapper site) throws ApplicationException {
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
            LAST_7_DAYS_PROCESSING_EVENTS_QRY, Arrays.asList(new Object[] {
                getId(), site.getId(), startDate, endDate }));
        List<ProcessingEvent> res = appService.query(criteria);
        List<ProcessingEventWrapper> visits = new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent v : res) {
            visits.add(new ProcessingEventWrapper(appService, v));
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
            List<SourceVesselWrapper> svs = patient2
                .getSourceVesselCollection(false);
            if (svs != null) {
                patient2.removeFromSourceVesselCollection(svs);
                addToSourceVesselCollection(svs);
            }
            patient2.delete();
            persist();

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

    public List<CollectionEventWrapper> getCollectionEventCollection() {
        Set<CollectionEventWrapper> collectionEvents = new HashSet<CollectionEventWrapper>();
        List<SourceVesselWrapper> svs = getSourceVesselCollection(false);
        for (SourceVesselWrapper sv : svs)
            collectionEvents.add(sv.getCollectionEvent());
        return new ArrayList<CollectionEventWrapper>(collectionEvents);
    }

}