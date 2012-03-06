package edu.ualberta.med.biobank.common.action.patient;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.LocalizedActionException;
import edu.ualberta.med.biobank.common.i18n.Messages;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;

/**
 * 
 * @author jferland
 * 
 */
public class PatientGetProcessingEventsByPNumberAction implements
    Action<PatientGetProcessingEventsByPNumberResult> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    private static final String PATIENT_EXISTS_HQL = 
        "SELECT 1 FROM " + Patient.class.getName() + "WHERE pnumber = ?";
    private static final String PROCESSING_EVENTS_GET_HQL =
        "SELECT distinct(processingEvent)" +
        " FROM " + Patient.class.getName() + " AS patient" +
        " JOIN patient.collectionEvents AS collectionEvents" +
        " JOIN collectionEvents.allSpecimens AS specimens" +
        " JOIN specimens.processingEvent AS processingEvent" +
        " WHERE patient.pnumber = ?" +
        " AND processingEvent.center.id = ?" +
        " ORDER BY processingEvent.createdAt DESC";
    // @formatter:on

    private final String pNumber;
    private final Integer centerId;

    public PatientGetProcessingEventsByPNumberAction(String pNumber,
        Integer centerId) {
        this.pNumber = pNumber;
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true; // TODO: permission check
    }

    @Override
    public PatientGetProcessingEventsByPNumberResult run(ActionContext context)
        throws ActionException {
        if (pNumber == null || pNumber.isEmpty()) {
            throw new LocalizedActionException(Messages.Greeting, "one", "two");

            // throw new EmptyValueException(Patient.class,
            // PatientPeer.PNUMBER);
            // throw new LocalizedException(Messages.badPNumber, pNumber);

            // "asdf" = "{0} is a {1} dog";
            // "asdf" = ""
            //
            // "action.check.pnumberNotNull" =
            // "Patient number '{0}' is not a legal value";
        }

        if (centerId == null) {
            // throw new IllegalValueException(Center.class, CenterPeer.ID);
        }

        context.load(Center.class, centerId); // ensure Center exists

        boolean exists = isPatientExists(context);
        List<ProcessingEvent> pEvents = getProcessingEvents(context);

        return new PatientGetProcessingEventsByPNumberResult(exists, pEvents);
    }

    private boolean isPatientExists(ActionContext context) {
        Query query = context.getSession().createQuery(PATIENT_EXISTS_HQL);
        query.setParameter(0, pNumber);

        return !query.list().isEmpty();
    }

    private List<ProcessingEvent> getProcessingEvents(ActionContext context) {
        List<ProcessingEvent> pEvents = new ArrayList<ProcessingEvent>();

        Query query =
            context.getSession().createQuery(PROCESSING_EVENTS_GET_HQL);
        query.setParameter(0, pNumber);
        query.setParameter(1, centerId);

        @SuppressWarnings("unchecked")
        List<ProcessingEvent> result = query.list();
        pEvents.addAll(result);

        return pEvents;
    }
}
