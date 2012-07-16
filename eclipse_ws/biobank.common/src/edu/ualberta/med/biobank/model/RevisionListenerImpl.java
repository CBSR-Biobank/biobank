package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.text.MessageFormat;

import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

public class RevisionListenerImpl
    implements EntityTrackingRevisionListener {
    @SuppressWarnings("nls")
    private static final String WRONG_REVISION_ENTITY =
        "Unexpected RevisionEntity: {0}, was expecting an instance of: {1}";
    private static final ThreadLocal<User> THREAD_USER =
        new ThreadLocal<User>();

    public static void setUser(User user) {
        THREAD_USER.set(user);
    }

    @Override
    public void newRevision(Object revisionEntity) {
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