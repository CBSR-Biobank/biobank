package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.DateUtil;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.loggers.ProcessingEventLogProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessingEventWrapper extends ProcessingEventBaseWrapper {
    private static final ProcessingEventLogProvider LOG_PROVIDER =
        new ProcessingEventLogProvider();
    private final Set<SpecimenWrapper> removedSpecimens =
        new HashSet<SpecimenWrapper>();

    public ProcessingEventWrapper(WritableApplicationService appService,
        ProcessingEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    public ProcessingEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public long getSpecimenCount(boolean fast) throws BiobankException,
        ApplicationException {
        return getPropertyCount(ProcessingEventPeer.SPECIMENS, fast);
    }

    public List<SpecimenWrapper> getDerivedSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> derivedSpecimens =
            new ArrayList<SpecimenWrapper>();
        for (SpecimenWrapper spec : getSpecimenCollection(false)) {
            derivedSpecimens.addAll(spec.getChildSpecimenCollection(false));
        }
        if (sort) {
            Collections.sort(derivedSpecimens);
        }
        return derivedSpecimens;
    }

    @Override
    public int compareTo(ModelWrapper<ProcessingEvent> wrapper) {
        if (wrapper instanceof ProcessingEventWrapper) {
            Date v1Date = getCreatedAt();
            Date v2Date = ((ProcessingEventWrapper) wrapper).getCreatedAt();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    public void resetInternalFields() {
        removedSpecimens.clear();
    }

    @Override
    public String toString() {
        return "Date created:" + getFormattedCreatedAt() + " - Worksheet:" //$NON-NLS-1$ //$NON-NLS-2$
            + getWorksheet();
    }

    public String getFormattedCreatedAt() {
        return DateFormatter.formatAsDateTime(getCreatedAt());
    }

    @Override
    public ProcessingEventLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    private static final String PROCESSING_EVENT_BY_DATE_QRY =
        "select pEvent from " //$NON-NLS-1$
            + ProcessingEvent.class.getName()
            + " pEvent where pEvent." //$NON-NLS-1$
            + ProcessingEventPeer.CREATED_AT.getName()
            + ">=? and pEvent." //$NON-NLS-1$
            + ProcessingEventPeer.CREATED_AT.getName()
            + "<? and pEvent." //$NON-NLS-1$
            + Property.concatNames(ProcessingEventPeer.CENTER, CenterPeer.ID)
            + "= ?"; //$NON-NLS-1$

    public static List<ProcessingEventWrapper> getProcessingEventsWithDateForCenter(
        WritableApplicationService appService, Date date,
        CenterWrapper<?> center) throws Exception {
        HQLCriteria c =
            new HQLCriteria(PROCESSING_EVENT_BY_DATE_QRY,
                Arrays.asList(new Object[] { DateUtil.startOfDay(date),
                    DateUtil.endOfDay(date),
                    center.getId() }));
        List<ProcessingEvent> pvs = appService.query(c);
        List<ProcessingEventWrapper> pvws =
            new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent pv : pvs)
            pvws.add(new ProcessingEventWrapper(appService, pv));
        if (pvws.size() == 0)
            return new ArrayList<ProcessingEventWrapper>();
        return pvws;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_QRY =
        "select pEvent from " //$NON-NLS-1$
            + ProcessingEvent.class.getName()
            + " pEvent where pEvent." //$NON-NLS-1$
            + ProcessingEventPeer.WORKSHEET.getName() + "=?"; //$NON-NLS-1$

    public static List<ProcessingEventWrapper> getProcessingEventsWithWorksheet(
        WritableApplicationService appService, String worksheetNumber)
        throws Exception {
        HQLCriteria c = new HQLCriteria(PROCESSING_EVENT_BY_WORKSHEET_QRY,
            Arrays.asList(new Object[] { worksheetNumber }));
        List<ProcessingEvent> pvs = appService.query(c);
        List<ProcessingEventWrapper> pvws =
            new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent pv : pvs)
            pvws.add(new ProcessingEventWrapper(appService, pv));
        if (pvws.size() == 0)
            return new ArrayList<ProcessingEventWrapper>();
        return pvws;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_COUNT_QRY =
        "select count(pEvent) from " //$NON-NLS-1$
            + ProcessingEvent.class.getName()
            + " pEvent where pEvent." //$NON-NLS-1$
            + ProcessingEventPeer.WORKSHEET.getName() + "=?"; //$NON-NLS-1$

    public static long getProcessingEventsWithWorksheetCount(
        WritableApplicationService appService, String worksheetNumber)
        throws BiobankQueryResultSizeException, ApplicationException {
        HQLCriteria c = new HQLCriteria(
            PROCESSING_EVENT_BY_WORKSHEET_COUNT_QRY,
            Arrays.asList(new Object[] { worksheetNumber }));
        return getCountResult(appService, c);
    }

    public static Collection<? extends ModelWrapper<?>> getAllProcessingEvents(
        BiobankApplicationService appService) throws ApplicationException {
        return ModelWrapper.wrapModelCollection(appService,
            appService.query(DetachedCriteria.forClass(ProcessingEvent.class)),
            ProcessingEventWrapper.class);
    }

    @Deprecated
    @Override
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        if (getCenter() != null)
            return Arrays.asList(getCenter());
        return super.getSecuritySpecificCenters();
    }

    public Long getChildSpecimenCount() {
        List<SpecimenWrapper> parents = getSpecimenCollection(false);
        Long count = Long.valueOf(0);
        for (SpecimenWrapper sp : parents)
            count += sp.getChildSpecimenCollection(false).size();
        return count;
    }

    private static String CEVENT_FROM_SPECIMEN_AND_PATIENT_QRY =
        "select distinct(cEvent) from " //$NON-NLS-1$
            + CollectionEvent.class.getName()
            + " as cEvent join cEvent." //$NON-NLS-1$
            + CollectionEventPeer.ALL_SPECIMENS.getName()
            + " as specs where cEvent." //$NON-NLS-1$
            + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
            + "=? and specs." //$NON-NLS-1$
            + Property.concatNames(SpecimenPeer.PROCESSING_EVENT,
                ProcessingEventPeer.ID) + "=?"; //$NON-NLS-1$

    public List<CollectionEventWrapper> getCollectionEventFromSpecimensAndPatient(
        PatientWrapper patient) throws ApplicationException {
        HQLCriteria c = new HQLCriteria(CEVENT_FROM_SPECIMEN_AND_PATIENT_QRY,
            Arrays.asList(new Object[] { patient.getId(), getId() }));
        List<CollectionEvent> res = appService.query(c);
        return wrapModelCollection(appService, res,
            CollectionEventWrapper.class);
    }

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        super.addPersistTasks(tasks);

        tasks.persistAdded(this, ProcessingEventPeer.SPECIMENS);
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        tasks.persistRemoved(this, ProcessingEventPeer.SPECIMENS);

        super.addDeleteTasks(tasks);
    }

    /**
     * return true if the user can delete this object
     */
    @Override
    public boolean canDelete(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canDelete(user, center, study)
            && (getCenter() == null || getCenter().equals(
                user.getCurrentWorkingCenter()));
    }

    /**
     * return true if the user can edit this object
     */
    @Override
    public boolean canUpdate(UserWrapper user, CenterWrapper<?> center,
        StudyWrapper study) {
        return super.canUpdate(user, center, study)
            && (getCenter() == null || getCenter().equals(
                user.getCurrentWorkingCenter()));
    }

    public static String PE_BY_PATIENT_STRING = "select distinct s." //$NON-NLS-1$
        + SpecimenPeer.PROCESSING_EVENT.getName()
        + " from " //$NON-NLS-1$
        + Specimen.class.getName()
        + " s where s." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.PATIENT, PatientPeer.PNUMBER)
        + " = ? order by s." //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.PROCESSING_EVENT,
            ProcessingEventPeer.CREATED_AT);

    public static List<ProcessingEventWrapper> getProcessingEventsByPatient(
        BiobankApplicationService appService, String pnum)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(PE_BY_PATIENT_STRING,
            Arrays.asList(pnum));
        List<ProcessingEvent> res = appService.query(c);
        return wrapModelCollection(appService, res,
            ProcessingEventWrapper.class);
    }

    /**
     * Should be addToSpecimenCollection most of the time. But can use this
     * method from tome to tome to reset the collection (used in saving pEvent
     * when want to try to re-add the specimens)
     */
    public void setSpecimenWrapperCollection(List<SpecimenWrapper> specs) {
        setWrapperCollection(ProcessingEventPeer.SPECIMENS, specs);
    }
}
