package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.base.PatientBaseWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("unused")
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
    @Deprecated
    public List<CollectionEventWrapper> getVisits(Date dateProcessed,
        Date dateDrawn) {
        return null;
    }

    private static final String PATIENT_QRY = "from " + Patient.class.getName()
        + " where " + PatientPeer.PNUMBER.getName() + "=?";

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
    // FIXME not sure the result is still what is explain in the comments
    public static PatientWrapper getPatient(
        WritableApplicationService appService, String patientNumber, User user)
        throws ApplicationException {
        PatientWrapper patient = getPatient(appService, patientNumber);
        if (patient != null) {
            boolean canRead = true;
            // StudyWrapper study = patient.getStudy();
            // List<SiteWrapper> sites = study.getSiteCollection(false);
            // boolean canRead = false;
            // for (SiteWrapper site : sites) {
            // if (user.hasPrivilegeOnObject(Privilege.READ, null, Site.class,
            // site.getId())) {
            // canRead = true;
            // break;
            // }
            // }
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
        List<CollectionEventWrapper> cevents = getCollectionEventCollection(false);
        for (CollectionEventWrapper cevent : cevents) {
            cevent.delete();
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkNoMoreCollectionEvents();
        if (getAllSpecimensCount(false) > 0)
            throw new BiobankDeleteException("Unable to delete patient "
                + getPnumber()
                + " because patient has specimens stored in database.");
    }

    private void checkNoMoreCollectionEvents() throws BiobankDeleteException {
        List<CollectionEventWrapper> pvs = getCollectionEventCollection(false);
        if (pvs != null && !pvs.isEmpty()) {
            throw new BiobankDeleteException(
                "Collection events are still linked to this patient."
                    + " Delete them before attempting to remove the patient.");
        }
    }

    private static final String ALL_SPECIMEN_COUNT_QRY = "select count(spcs) from "
        + CollectionEvent.class.getName()
        + " as cevent join cevent."
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " as spcs where cevent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=?";

    public long getAllSpecimensCount(boolean fast) throws ApplicationException,
        BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(ALL_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        long total = 0;
        for (CollectionEventWrapper cevent : getCollectionEventCollection(false))
            total += cevent.getAllSpecimensCount(false);
        return total;
    }

    private static final String SOURCE_SPECIMEN_COUNT_QRY = "select count(spcs) from "
        + CollectionEvent.class.getName()
        + " as cevent join cevent."
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName()
        + " as spcs where cevent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=?";

    public long getSourceSpecimensCount(boolean fast)
        throws ApplicationException, BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(SOURCE_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        long total = 0;
        for (CollectionEventWrapper cevent : getCollectionEventCollection(false))
            total += cevent.getSourceSpecimensCount(false);
        return total;
    }

    private static final String ALIQUOTED_SPECIMEN_COUNT_QRY = "select count(spcs) from "
        + CollectionEvent.class.getName()
        + " as cevent join cevent."
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " as spcs where cevent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=? and spcs."
        + SpecimenPeer.PARENT_SPECIMEN.getName()
        + " is not null";

    public long getAliquotedSpecimensCount(boolean fast)
        throws ApplicationException, BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(
                ALIQUOTED_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        long total = 0;
        for (CollectionEventWrapper cevent : getCollectionEventCollection(false))
            total += cevent.getAliquotedSpecimensCount(false);
        return total;
    }

    @Deprecated
    public boolean canBeAddedToCollectionEvent(CollectionEventWrapper cevent)
        throws ApplicationException, BiobankException {
        // TODO: does this make sense anymore?
        return false;
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

    // private static final String PATIENTS_IN_TODAYS_COLLECTION_EVENTS_QRY =
    // "select p from "
    // + Patient.class.getName()
    // + " as p join p."
    // + PatientPeer.SOURCE_VESSEL_COLLECTION.getName()
    // + " as svc join svc."
    // + SpecimenPeer.COLLECTION_EVENT.getName()
    // + " as ces where ces."
    // + CollectionEventPeer.DATE_RECEIVED.getName()
    // + ">=? and ces."
    // + CollectionEventPeer.DATE_RECEIVED.getName() + "<=?";

    @Deprecated
    public static List<PatientWrapper> getPatientsInTodayCollectionEvents(
        WritableApplicationService appService) throws ApplicationException {
        // Calendar cal = Calendar.getInstance();
        // // yesterday midnight
        // cal.set(Calendar.AM_PM, Calendar.AM);
        // cal.set(Calendar.HOUR, 0);
        // cal.set(Calendar.MINUTE, 0);
        // cal.set(Calendar.SECOND, 0);
        // Date startDate = cal.getTime();
        // // today midnight
        // cal.add(Calendar.DATE, 1);
        // Date endDate = cal.getTime();
        // HQLCriteria criteria = new HQLCriteria(
        // PATIENTS_IN_TODAYS_COLLECTION_EVENTS_QRY,
        // Arrays.asList(new Object[] { startDate, endDate }));
        // List<Patient> res = appService.query(criteria);
        // List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        // for (Patient p : res) {
        // patients.add(new PatientWrapper(appService, p));
        // }
        // return patients;
        return null;
    }

    // private static final String LAST_7_DAYS_PROCESSING_EVENTS_QRY =
    // "select pEvent from "
    // + Patient.class.getName()
    // + " as p join p."
    // + PatientPeer.COLLECTION_EVENT_COLLECTION.getName()
    // + " as ces join ces."
    // + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
    // + " as specimens join specimens."
    // + SpecimenPeer.SPECIMEN_LINK_COLLECTION.getName()
    // + " as spLink join spLink."
    // + SpecimenLinkPeer.PROCESSING_EVENT
    // + " as pes where p."
    // + PatientPeer.ID.getName()
    // + "=? and pes."
    // + ProcessingEventPeer.CREATED_AT.getName()
    // + ">? and pes."
    // + ProcessingEventPeer.CREATED_AT.getName() + "<?";

    @Deprecated
    // FIXME :in Scan Link, wants the collection event instead. ? What is a last
    // 7
    // days collection events if we don't have the date drawn on the collection
    // event itself ?
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
        // HQLCriteria criteria = new HQLCriteria(
        // LAST_7_DAYS_PROCESSING_EVENTS_QRY, Arrays.asList(new Object[] {
        // getId(), site.getId(), startDate, endDate }));
        // List<ProcessingEvent> res = appService.query(criteria);
        // return ModelWrapper.wrapModelCollection(appService, res,
        // ProcessingEventWrapper.class);
        return new ArrayList<ProcessingEventWrapper>();
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        log.setCenter(site);
        log.setPatientNumber(getPnumber());
        log.setDetails(details);
        log.setType("Patient");
        return log;
    }

    /**
     * merge patient2 into this patient
     */
    public void merge(PatientWrapper patient2) throws Exception {
        reload();
        patient2.reload();
        if (getStudy().equals(patient2.getStudy())) {
            List<CollectionEventWrapper> cevents = patient2
                .getCollectionEventCollection(false);

            if (!cevents.isEmpty()) {
                patient2.removeFromCollectionEventCollection(cevents);
                Set<CollectionEventWrapper> toAdd = new HashSet<CollectionEventWrapper>();
                List<CollectionEventWrapper> toDelete = new ArrayList<CollectionEventWrapper>();
                boolean merged = false;
                for (CollectionEventWrapper p2event : cevents) {
                    for (CollectionEventWrapper p1event : getCollectionEventCollection(false))
                        if (p1event.getVisitNumber().equals(
                            p2event.getVisitNumber())) {
                            p1event.addToOriginalSpecimenCollection(p2event
                                .getOriginalSpecimenCollection(false));
                            p1event.addToAllSpecimenCollection(p2event
                                .getAllSpecimenCollection(false));
                            for (SpecimenWrapper spec : p2event
                                .getAllSpecimenCollection(false))
                                spec.setCollectionEvent(p1event);
                            toDelete.add(p2event);
                            p1event.persist();
                            merged = true;
                        }
                    if (!merged)
                        toAdd.add(p2event);
                    merged = false;
                }

                for (CollectionEventWrapper addMe : toAdd) {
                    addMe.setPatient(this);
                    addMe.persist();
                }
                for (CollectionEventWrapper deleteMe : toDelete) {
                    deleteMe.persist();
                    deleteMe.delete();
                }
                patient2.delete();
                persist();

                ((BiobankApplicationService) appService).logActivity("merge",
                    null, patient2.getPnumber(), null, null,
                    patient2.getPnumber() + " --> " + getPnumber(), "Patient");
                ((BiobankApplicationService) appService).logActivity("merge",
                    null, getPnumber(), null, null, getPnumber() + " <-- "
                        + patient2.getPnumber(), "Patient");
            }
        } else {
            throw new BiobankCheckException(
                "Cannot merge patients from different studies.");
        }
    }

    public List<CollectionEventWrapper> getCollectionEventCollection(
        boolean sort, final boolean ascending) {
        List<CollectionEventWrapper> cEvents = getCollectionEventCollection(false);
        if (sort) {
            Collections.sort(cEvents, new Comparator<CollectionEventWrapper>() {
                @Override
                public int compare(CollectionEventWrapper ce1,
                    CollectionEventWrapper ce2) {
                    int res = ce1.compareTo(ce2);
                    if (ascending) {
                        return res;
                    }
                    return -res;
                }
            });
        }
        return cEvents;
    }

    public List<ProcessingEventWrapper> getProcessingEventCollection() {
        List<CollectionEventWrapper> ces = getCollectionEventCollection(false);
        List<SpecimenWrapper> specs = new ArrayList<SpecimenWrapper>();
        Set<ProcessingEventWrapper> pes = new HashSet<ProcessingEventWrapper>();
        for (CollectionEventWrapper ce : ces)
            specs.addAll(ce.getAllSpecimenCollection(false));
        for (SpecimenWrapper spec : specs) {
            if (spec.getProcessingEvent() != null)
                pes.add(spec.getProcessingEvent());
        }
        return new ArrayList<ProcessingEventWrapper>(pes);
    }

    @Deprecated
    public boolean canBeAddedToShipment(CollectionEventWrapper shipment) {
        // TODO Auto-generated method stub
        return false;
    }

    private static final String CEVENT_COUNT_QRY = "select count(cevent) from "
        + CollectionEvent.class.getName() + " as cevent where cevent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=?";

    public Long getCollectionEventCount(boolean fast)
        throws BiobankQueryResultSizeException, ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(CEVENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        return (long) getCollectionEventCollection(false).size();
    }

}