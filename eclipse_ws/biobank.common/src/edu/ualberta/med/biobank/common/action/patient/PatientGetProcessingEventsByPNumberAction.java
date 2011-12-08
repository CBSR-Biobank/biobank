package edu.ualberta.med.biobank.common.action.patient;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.User;

public class PatientGetProcessingEventsByPNumberAction implements
    Action<PatientGetProcessingEventsByPNumberResult> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    private static final String PATIENT_EXISTS_HQL = 
        "SELECT 1 FROM " + Patient.class.getName() + "WHERE pnumber = ?";
    private static final String PROCESSING_EVENTS_GET_HQL =
        "SELECT distinct(processingEvent)" +
        " FROM " + Patient.class.getName() + " AS patient" +
        " JOIN patient.collectionEventCollection AS collectionEvents" +
        " JOIN collectionEvents.allSpecimenCollection AS specimens" +
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
    public boolean isAllowed(User user, Session session) {
        return true; // TODO: permission check
    }

    @Override
    public PatientGetProcessingEventsByPNumberResult run(User user,
        Session session)
        throws ActionException {

        boolean exists = isPatientExists(session);
        List<ProcessingEvent> pEvents = getProcessingEvents(session);

        return new PatientGetProcessingEventsByPNumberResult(exists, pEvents);
    }

    private boolean isPatientExists(Session session) {
        Query query = session.createQuery(PATIENT_EXISTS_HQL);
        query.setParameter(0, pNumber);

        return !query.list().isEmpty();
    }

    private List<ProcessingEvent> getProcessingEvents(Session session) {
        List<ProcessingEvent> pEvents = new ArrayList<ProcessingEvent>();

        Query query = session.createQuery(PROCESSING_EVENTS_GET_HQL);
        query.setParameter(0, pNumber);
        query.setParameter(1, centerId);

        @SuppressWarnings("unchecked")
        List<ProcessingEvent> result = query.list();
        pEvents.addAll(result);

        return pEvents;
    }
}
