package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.User;

public class ProcessingEventGetListAction implements
    Action<ProcessingEventGetListResult> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String PROCESSING_EVENT_GET_HQL =
        "SELECT distinct(processingEvent)" +
        " FROM " + Patient.class.getName() + " AS patient" +
        " JOIN patient.collectionEventCollection AS collectionEvents" +
        " JOIN collectionEvents.allSpecimenCollection AS specimens" +
        " JOIN specimens.processingEvent AS processingEvent" +
        " WHERE patient.pnumber = ?" +
        " AND processingEvent.center.id = ?" +
        " ORDER BY processingEvent.createdAt DESC";
    // @formatter:on

    private final String patientNumber;
    private final Integer centerId;

    public ProcessingEventGetListAction(String patientNumber,
        Integer centerId) {
        this.patientNumber = patientNumber;
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true; // TODO: permission check
    }

    @Override
    public ProcessingEventGetListResult run(User user, Session session)
        throws ActionException {
        List<ProcessingEvent> processingEvents =
            new ArrayList<ProcessingEvent>();

        boolean exists = true; // TODO: actually check whether patient exists

        Query query = session.createQuery(PROCESSING_EVENT_GET_HQL);
        query.setParameter(0, patientNumber);
        query.setParameter(1, centerId);

        @SuppressWarnings("unchecked")
        List<ProcessingEvent> result = query.list();
        processingEvents.addAll(result);

        return new ProcessingEventGetListResult(processingEvents, exists);
    }
}
