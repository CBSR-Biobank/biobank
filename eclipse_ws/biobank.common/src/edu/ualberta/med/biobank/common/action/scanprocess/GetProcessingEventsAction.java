package edu.ualberta.med.biobank.common.action.scanprocess;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenLinkPermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * Retrieves the processing events for the given patient, at the specified centre, for the last 7
 * days.
 * 
 * @author nelson
 * 
 */
public class GetProcessingEventsAction implements Action<ListResult<ProcessingEvent>> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GetProcessingEventsAction.class.getName());

    @SuppressWarnings("nls")
    private static final String PROCESSING_EVENTS_GET_HQL =
        "SELECT distinct(processingEvent)" +
            " FROM " + Patient.class.getName() + " AS patient" +
            " JOIN patient.collectionEvents AS collectionEvents" +
            " JOIN collectionEvents.allSpecimens AS specimens" +
            " JOIN specimens.processingEvent AS processingEvent" +
            " WHERE patient.pnumber = ?" +
            " AND processingEvent.center.id = ?";

    @SuppressWarnings("nls")
    private static final String PROCESSING_EVENTS_LAST_7_DAYS_HQL =
        " AND processingEvent.createdAt > ?" +
            " AND processingEvent.createdAt < ?";

    protected final String pNumber;

    protected final Integer currentWorkingCenterId;

    protected final boolean lastSevenDays;

    @SuppressWarnings("nls")
    public GetProcessingEventsAction(
        String pNumber, Integer currentWorkingCenterId, boolean lastSevenDays) {
        if (pNumber == null) {
            throw new IllegalArgumentException("patient number is null");
        }

        if (currentWorkingCenterId == null) {
            throw new IllegalArgumentException("currentWorkingCenterId is null");
        }

        this.pNumber = pNumber;
        this.currentWorkingCenterId = currentWorkingCenterId;
        this.lastSevenDays = lastSevenDays;
    }

    public GetProcessingEventsAction(
        String pNumber, Integer currentWorkingCenterId) {
        this(pNumber, currentWorkingCenterId, false);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenLinkPermission(currentWorkingCenterId, null).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<ProcessingEvent> run(ActionContext context) throws ActionException {
        log.info("run: pNumber={}, currentWorkingCenterId={}", pNumber, currentWorkingCenterId);

        List<ProcessingEvent> processingEvents;

        if (lastSevenDays) {
            processingEvents = getProcessingEventsForLastSevenDays(context);
        } else {
            processingEvents = getProcessingEvents(context);
        }

        // load required fields
        for (ProcessingEvent pe : processingEvents) {
            for (Specimen spc : pe.getSpecimens()) {
                spc.getCollectionEvent().getPatient().getStudy().getName();
            }
        }

        return new ListResult<ProcessingEvent>(processingEvents);
    }

    @SuppressWarnings("unchecked")
    private List<ProcessingEvent> getProcessingEvents(ActionContext context) {
        Query query = context.getSession().createQuery(PROCESSING_EVENTS_GET_HQL);
        query.setParameter(0, pNumber);
        query.setParameter(1, currentWorkingCenterId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    private List<ProcessingEvent> getProcessingEventsForLastSevenDays(ActionContext context) {
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

        Query query = context.getSession().createQuery(
            PROCESSING_EVENTS_GET_HQL + PROCESSING_EVENTS_LAST_7_DAYS_HQL);
        query.setParameter(0, pNumber);
        query.setParameter(1, currentWorkingCenterId);
        query.setParameter(2, startDate);
        query.setParameter(3, endDate);

        return query.list();
    }
}
