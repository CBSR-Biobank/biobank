package edu.ualberta.med.biobank.model.listener;

import java.io.Serializable;
import java.text.MessageFormat;

import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import edu.ualberta.med.biobank.model.Revision;
import edu.ualberta.med.biobank.model.context.ExecutingUser;
import edu.ualberta.med.biobank.model.context.ExecutingUserImpl;

public class RevisionListenerImpl
    implements EntityTrackingRevisionListener {
    @SuppressWarnings("nls")
    private static final String WRONG_REVISION_ENTITY =
        "Unexpected RevisionEntity: {0}, was expecting an instance of: {1}";

    private final ExecutingUser executingUser = new ExecutingUserImpl();

    @Override
    public void newRevision(Object revisionEntity) {
        Revision revision = asRevision(revisionEntity);
        revision.setUser(executingUser.get());
    }

    @Override
    public void entityChanged(@SuppressWarnings("rawtypes") Class entityClass,
        String entityName, Serializable entityId, RevisionType revisionType,
        Object revisionEntity) {
        Revision revision = asRevision(revisionEntity);
        revision.getModifiedTypes().add(entityName);
    }

    private static Revision asRevision(Object object) {
        if (object instanceof Revision) {
            return (Revision) object;
        }

        String msg = MessageFormat.format(WRONG_REVISION_ENTITY,
            object.getClass().getName(),
            Revision.class.getName());

        throw new IllegalArgumentException(msg);
    }
}