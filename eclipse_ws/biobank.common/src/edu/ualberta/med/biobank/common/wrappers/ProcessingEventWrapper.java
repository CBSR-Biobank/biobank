package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessingEventWrapper extends ProcessingEventBaseWrapper {

    private Set<SpecimenWrapper> removedSpecimens = new HashSet<SpecimenWrapper>();

    public ProcessingEventWrapper(WritableApplicationService appService,
        ProcessingEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    public ProcessingEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        // TODO: new checks required
        // TODO at least one specimen added ?
        if (isNew()) {
            if (getWorksheet() == null || getWorksheet().isEmpty())
                throw new BiobankCheckException("Worksheet cannot be empty.");
            else if (getProcessingEventsWithWorksheetCount(appService,
                getWorksheet()) > 0)
                throw new BiobankCheckException("Worksheet " + getWorksheet()
                    + " is already used.");
        }
    }

    @Override
    protected void persistDependencies(ProcessingEvent origObject)
        throws Exception {
        for (SpecimenWrapper ss : removedSpecimens) {
            if (!ss.isNew()) {
                ss.setProcessingEvent(null);
                ss.setActivityStatus(ActivityStatusWrapper
                    .getActiveActivityStatus(appService));
                ss.persist();
            }
        }
    }

    @Override
    public void addToSpecimenCollection(List<SpecimenWrapper> specimenCollection) {
        removedSpecimens.removeAll(specimenCollection);
        super.addToSpecimenCollection(specimenCollection);
    }

    @Override
    public void removeFromSpecimenCollection(
        List<SpecimenWrapper> specimenCollection) {
        removedSpecimens.addAll(specimenCollection);
        super.removeFromSpecimenCollection(specimenCollection);
    }

    @Override
    protected void deleteChecks() throws ApplicationException, BiobankException {
        if (getDerivedSpecimenCount(false) > 0) {
            throw new BiobankDeleteException(
                "Unable to delete processing event '"
                    + getWorksheet()
                    + "' ("
                    + getFormattedCreatedAt()
                    + ") since some of its specimens have already been derived "
                    + "into others specimens.");
        }
    }

    @Override
    protected void deleteDependencies() throws Exception {
        for (SpecimenWrapper ss : getSpecimenCollection(false)) {
            if (!ss.isNew()) {
                ss.setProcessingEvent(null);
                ss.persist();
            }
        }
    }

    private static final String SPECIMEN_COUNT_QRY = "select count(specimen) from "
        + Specimen.class.getName()
        + " as specimen where specimen."
        + Property.concatNames(SpecimenPeer.PROCESSING_EVENT,
            ProcessingEventPeer.ID) + "=?";

    public long getSpecimenCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        return getSpecimenCollection(false).size();
    }

    private static final String DERIVED_SPECIMEN_COUNT_QRY = "select count(specimen) from "
        + Specimen.class.getName()
        + " as specimen where specimen."
        + Property.concatNames(SpecimenPeer.PARENT_SPECIMEN,
            SpecimenPeer.PROCESSING_EVENT, ProcessingEventPeer.ID) + "=?";

    public long getDerivedSpecimenCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(DERIVED_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        return getDerivedSpecimenCollection(false).size();
    }

    public List<SpecimenWrapper> getDerivedSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> derivedSpecimens = new ArrayList<SpecimenWrapper>();
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
        return "Date created:" + getFormattedCreatedAt() + " - Worksheet:"
            + getWorksheet();
    }

    public String getFormattedCreatedAt() {
        return DateFormatter.formatAsDateTime(getCreatedAt());
    }

    @Override
    protected Log getLogMessage(String action, String site, String details)
        throws Exception {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setCenter(getCenter().getNameShort());
        } else {
            log.setCenter(site);
        }
        List<String> detailsList = new ArrayList<String>();
        if (details.length() > 0) {
            detailsList.add(details);
        }

        detailsList.add(new StringBuilder("Source Specimens: ").append(
            getSpecimenCount(false)).toString());
        String worksheet = getWorksheet();
        if (worksheet != null) {
            detailsList.add(new StringBuilder("Worksheet: ").append(worksheet)
                .toString());
        }
        log.setDetails(StringUtils.join(detailsList, ", "));
        log.setType("ProcessingEvent");
        return log;
    }

    private static final String PROCESSING_EVENT_BY_DATE_QRY = "select pEvent from "
        + ProcessingEvent.class.getName()
        + " pEvent where DATE(pEvent."
        + ProcessingEventPeer.CREATED_AT.getName() + ")=DATE(?)";

    public static List<ProcessingEventWrapper> getProcessingEventsWithDate(
        WritableApplicationService appService, Date date) throws Exception {
        HQLCriteria c = new HQLCriteria(PROCESSING_EVENT_BY_DATE_QRY,
            Arrays.asList(new Object[] { date }));
        List<ProcessingEvent> pvs = appService.query(c);
        List<ProcessingEventWrapper> pvws = new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent pv : pvs)
            pvws.add(new ProcessingEventWrapper(appService, pv));
        if (pvws.size() == 0)
            return new ArrayList<ProcessingEventWrapper>();
        return pvws;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_QRY = "select pEvent from "
        + ProcessingEvent.class.getName()
        + " pEvent where pEvent."
        + ProcessingEventPeer.WORKSHEET.getName() + "=?";

    public static List<ProcessingEventWrapper> getProcessingEventsWithWorksheet(
        WritableApplicationService appService, String worksheetNumber)
        throws Exception {
        HQLCriteria c = new HQLCriteria(PROCESSING_EVENT_BY_WORKSHEET_QRY,
            Arrays.asList(new Object[] { worksheetNumber }));
        List<ProcessingEvent> pvs = appService.query(c);
        List<ProcessingEventWrapper> pvws = new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent pv : pvs)
            pvws.add(new ProcessingEventWrapper(appService, pv));
        if (pvws.size() == 0)
            return new ArrayList<ProcessingEventWrapper>();
        return pvws;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_COUNT_QRY = "select count(pEvent) from "
        + ProcessingEvent.class.getName()
        + " pEvent where pEvent."
        + ProcessingEventPeer.WORKSHEET.getName() + "=?";

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
            appService.search(ProcessingEvent.class, new ProcessingEvent()),
            ProcessingEventWrapper.class);
    }

    @SuppressWarnings("unchecked")
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

    private static String CEVENT_FROM_SPECIMEN_AND_PATIENT_QRY = "select distinct(cEvent) from "
        + CollectionEvent.class.getName()
        + " as cEvent join cEvent."
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " as specs where cEvent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
        + "=? and specs."
        + Property.concatNames(SpecimenPeer.PROCESSING_EVENT,
            ProcessingEventPeer.ID) + "=?";

    public List<CollectionEventWrapper> getCollectionEventFromSpecimensAndPatient(
        PatientWrapper patient) throws ApplicationException {
        HQLCriteria c = new HQLCriteria(CEVENT_FROM_SPECIMEN_AND_PATIENT_QRY,
            Arrays.asList(new Object[] { patient.getId(), getId() }));
        List<CollectionEvent> res = appService.query(c);
        return wrapModelCollection(appService, res,
            CollectionEventWrapper.class);
    }

    /**
     * return true if the user can delete this object
     */
    @Override
    public boolean canDelete(User user) {
        return super.canDelete(user)
            && (getCenter() == null || user.getCurrentWorkingCenter().equals(
                getCenter()));
    }

    /**
     * return true if the user can edit this object
     */
    @Override
    public boolean canUpdate(User user) {
        return super.canUpdate(user)
            && (getCenter() == null || user.getCurrentWorkingCenter().equals(
                getCenter()));
    }

    public static String PE_BY_PATIENT_STRING = "select distinct s."
        + SpecimenPeer.PROCESSING_EVENT.getName()
        + " from "
        + Specimen.class.getName()
        + " s where s."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.PATIENT, PatientPeer.PNUMBER)
        + " = ? order by s."
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
}
