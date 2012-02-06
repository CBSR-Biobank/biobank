package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.Set;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;

public class CollectionEventLogProvider implements
    WrapperLogProvider<CollectionEvent> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(CollectionEvent collectionEvent) {
        Log log = new Log();

        log.setPatientNumber(collectionEvent.getPatient().getPnumber());

        String details = "visit: " + collectionEvent.getVisitNumber() //$NON-NLS-1$
            + ", specimens: " + getOriginalSpecimensCount(collectionEvent); //$NON-NLS-1$
        log.setDetails(details);

        return log;
    }

    private int getOriginalSpecimensCount(CollectionEvent collectionEvent) {
        int count = 0;

        // TODO: could be switched to HQL count query to be way faster
        Set<Specimen> originals = collectionEvent
            .getOriginalSpecimenCollection();
        if (originals != null) {
            count = originals.size();
        }

        return count;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((CollectionEvent) model);
    }
}
