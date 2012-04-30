package edu.ualberta.med.biobank.common.wrappers.loggers;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;

public class CollectionEventLogProvider implements
    WrapperLogProvider<CollectionEvent> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(CollectionEvent collectionEvent) {
        Log log = new Log();

        log.setPatientNumber(collectionEvent.getPatient().getPnumber());

        String details = "visit: " + collectionEvent.getVisitNumber(); //$NON-NLS-1$
        log.setDetails(details);

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((CollectionEvent) model);
    }
}
