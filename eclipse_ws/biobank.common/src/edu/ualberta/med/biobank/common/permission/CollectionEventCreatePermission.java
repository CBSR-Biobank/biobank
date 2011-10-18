package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final CollectionEvent collectionEvent;

    public CollectionEventCreatePermission(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Study study = collectionEvent.getPatient().getStudy();

        return false;
    }
}
