package edu.ualberta.med.biobank.model.provider;

import java.util.Date;

import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.study.CollectionEventType;
import edu.ualberta.med.biobank.model.study.Patient;

public class CollectionEventProvider
    extends AbstractProvider<CollectionEvent> {
    private int visitNumber = 1;

    public CollectionEventProvider(Mother mother) {
        super(mother);
    }

    @Override
    public CollectionEvent onCreate() {
        CollectionEvent ce = new CollectionEvent();
        ce.setType(mother.getProvider(CollectionEventType.class).get());
        ce.setPatient(mother.getProvider(Patient.class).get());
        ce.setVisitNumber(visitNumber++);
        ce.setTimeDone(new Date());
        return ce;
    }
}
