package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
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
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.PatientBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.NotUsedCheck;
import edu.ualberta.med.biobank.common.wrappers.loggers.PatientLogProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends PatientBaseWrapper {
    private static final PatientLogProvider LOG_PROVIDER = new PatientLogProvider();
    private static final String HAS_SPECIMENS_MSG = Messages
        .getString("PatientWrapper.has.specimens.msg"); //$NON-NLS-1$
    private static final String HAS_COLLECTION_EVENTS_MSG = Messages
        .getString("PatientWrapper.has.collection.event.msg"); //$NON-NLS-1$

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public PatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected Patient getNewObject() throws Exception {
        Patient newObject = super.getNewObject();

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(new Date());
        createdAt.set(Calendar.SECOND, 0);

        newObject.setCreatedAt(createdAt.getTime());
        return newObject;
    }

    private static final String PATIENT_QRY = "from " + Patient.class.getName() //$NON-NLS-1$
        + " where " + PatientPeer.PNUMBER.getName() + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

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
     * 
     * @throws Exception
     */
    public static PatientWrapper getPatient(
        WritableApplicationService appService, String patientNumber,
        UserWrapper user) throws Exception {
        PatientWrapper patient = getPatient(appService, patientNumber);
        if (patient != null) {
            StudyWrapper study = patient.getStudy();
            List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>(
                study.getSiteCollection(false));
            centers.addAll(study.getClinicCollection());
            if (Collections.disjoint(centers, user.getWorkingCenters())) {
                throw new ApplicationException(
                    MessageFormat
                        .format(
                            Messages.getString("PatientWrapper.patient.access.msg"), //$NON-NLS-1$
                            patientNumber));
            }
        }
        return patient;
    }

    private static final String SOURCE_SPECIMEN_COUNT_QRY = "select count(spcs) from " //$NON-NLS-1$
        + CollectionEvent.class.getName()
        + " as cevent join cevent." //$NON-NLS-1$
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName()
        + " as spcs where cevent." //$NON-NLS-1$
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=?"; //$NON-NLS-1$

    public long getSourceSpecimenCount(boolean fast)
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

    private static final String ALIQUOTED_SPECIMEN_COUNT_QRY = "select count(spcs) from " //$NON-NLS-1$
        + CollectionEvent.class.getName()
        + " as cevent join cevent." //$NON-NLS-1$
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " as spcs where cevent." //$NON-NLS-1$
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=? and spcs." //$NON-NLS-1$
        + SpecimenPeer.PARENT_SPECIMEN.getName() + " is not null"; //$NON-NLS-1$

    public long getAliquotedSpecimenCount(boolean fast)
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

    private static final String LAST_7_DAYS_PROCESSING_EVENTS_FOR_CENTER_QRY = "select distinct(pEvent) from " //$NON-NLS-1$
        + Patient.class.getName()
        + " as patient join patient." //$NON-NLS-1$
        + PatientPeer.COLLECTION_EVENT_COLLECTION.getName()
        + " as ces join ces." //$NON-NLS-1$
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " as specimens join specimens." //$NON-NLS-1$
        + SpecimenPeer.PROCESSING_EVENT.getName()
        + " as pEvent where patient." //$NON-NLS-1$
        + PatientPeer.ID.getName()
        + "=? and pEvent." //$NON-NLS-1$
        + Property.concatNames(ProcessingEventPeer.CENTER, CenterPeer.ID)
        + "=? and pEvent." //$NON-NLS-1$
        + ProcessingEventPeer.CREATED_AT.getName()
        + ">? and pEvent." + ProcessingEventPeer.CREATED_AT.getName() + "<?"; //$NON-NLS-1$ //$NON-NLS-2$

    // used in scan link and cabinet link
    public List<ProcessingEventWrapper> getLast7DaysProcessingEvents(
        CenterWrapper<?> center) throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // today at midnight
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
            LAST_7_DAYS_PROCESSING_EVENTS_FOR_CENTER_QRY,
            Arrays.asList(new Object[] { getId(), center.getId(), startDate,
                endDate }));
        List<ProcessingEvent> res = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, res,
            ProcessingEventWrapper.class);
    }

    @Override
    public PatientLogProvider getLogProvider() {
        return LOG_PROVIDER;
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

                Set<CollectionEventWrapper> toAdd = new HashSet<CollectionEventWrapper>();
                boolean merged = false;
                for (CollectionEventWrapper p2event : cevents) {
                    for (CollectionEventWrapper p1event : getCollectionEventCollection(false))
                        if (p1event.getVisitNumber().equals(
                            p2event.getVisitNumber())) {
                            // merge collection event
                            p1event.merge(p2event);
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

                persist();
                patient2.delete();

                ((BiobankApplicationService) appService).logActivity(
                    "merge", //$NON-NLS-1$
                    null, patient2.getPnumber(), null, null,
                    patient2.getPnumber() + " --> " + getPnumber(), "Patient"); //$NON-NLS-1$ //$NON-NLS-2$
                ((BiobankApplicationService) appService).logActivity("merge", //$NON-NLS-1$
                    null, getPnumber(), null, null, getPnumber() + " <-- " //$NON-NLS-1$
                        + patient2.getPnumber(), "Patient"); //$NON-NLS-1$
            }
        } else {
            throw new BiobankCheckException(
                Messages.getString("PatientWrapper.merge.patient.error.msg")); //$NON-NLS-1$
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
                    if (ascending) {
                        return ce1.compareTo(ce2);
                    }
                    return ce2.compareTo(ce1);
                }
            });
        }
        return cEvents;
    }

    public List<ProcessingEventWrapper> getProcessingEventCollection(
        boolean originalOnly) {
        List<CollectionEventWrapper> ces = getCollectionEventCollection(false);
        Set<ProcessingEventWrapper> pes = new HashSet<ProcessingEventWrapper>();
        for (CollectionEventWrapper ce : ces)
            if (originalOnly)
                addProcessingEvents(pes,
                    ce.getOriginalSpecimenCollection(false));
            else
                addProcessingEvents(pes, ce.getAllSpecimenCollection(false));
        return new ArrayList<ProcessingEventWrapper>(pes);
    }

    private void addProcessingEvents(Set<ProcessingEventWrapper> pes,
        List<SpecimenWrapper> specimens) {
        for (SpecimenWrapper spec : specimens) {
            if (spec.getProcessingEvent() != null)
                pes.add(spec.getProcessingEvent());
        }
    }

    public Long getCollectionEventCount(boolean fast)
        throws BiobankQueryResultSizeException, ApplicationException {
        return getPropertyCount(PatientPeer.COLLECTION_EVENT_COLLECTION, fast);
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(PatientPeer.PNUMBER));
        tasks.add(check().unique(PatientPeer.PNUMBER));

        super.addPersistTasks(tasks);
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        String hasCollectionEventsMsg = HAS_COLLECTION_EVENTS_MSG;
        tasks.add(new CollectionIsEmptyCheck<Patient>(this,
            PatientPeer.COLLECTION_EVENT_COLLECTION, hasCollectionEventsMsg));

        String hasSpecimensMsg = MessageFormat.format(HAS_SPECIMENS_MSG,
            getPnumber());
        tasks.add(new NotUsedCheck<Patient>(this, SpecimenPeer.COLLECTION_EVENT
            .to(CollectionEventPeer.PATIENT), Specimen.class, hasSpecimensMsg));

        super.addDeleteTasks(tasks);
    }

    public static Integer getNextVisitNumber(
        WritableApplicationService appService, PatientWrapper patient)
        throws Exception {
        HQLCriteria c = new HQLCriteria("select max(ce.visitNumber) from "
            + CollectionEvent.class.getName() + " ce where ce.patient.id=?",
            Arrays.asList(patient.getId()));
        List<Object> result = appService.query(c);
        if (result == null || result.size() == 0 || result.get(0) == null)
            return 1;
        else
            return (Integer) result.get(0) + 1;
    }

    /**
     * return true if the user can delete this object
     */
    @Override
    public boolean canDelete(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canDelete(user, center, study)
            && (getStudy() == null || user.getCurrentWorkingCenter() == null || user
                .getCurrentWorkingCenter().getStudyCollection()
                .contains(getStudy()));
    }

    /**
     * return true if the user can edit this object
     */
    @Override
    public boolean canUpdate(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canUpdate(user, center, study)
            && (getStudy() == null || user.getCurrentWorkingCenter() == null || user
                .getCurrentWorkingCenter().getStudyCollection()
                .contains(getStudy()));
    }
}