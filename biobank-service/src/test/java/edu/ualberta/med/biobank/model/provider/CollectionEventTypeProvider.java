package edu.ualberta.med.biobank.model.provider;

import edu.ualberta.med.biobank.model.study.CollectionEventType;
import edu.ualberta.med.biobank.model.study.Study;

public class CollectionEventTypeProvider
    extends AbstractProvider<CollectionEventType> {

    private int name = 1;

    protected CollectionEventTypeProvider(Mother mother) {
        super(mother);
    }

    @Override
    public CollectionEventType onCreate() {
        CollectionEventType type = new CollectionEventType();
        Study study = mother.getProvider(Study.class).get();
        type.setStudy(study);
        type.setName(mother.getName() + "_" + name++);
        type.setDescription("no description");
        type.setRecurring(Boolean.TRUE);
        return type;
    }
}
