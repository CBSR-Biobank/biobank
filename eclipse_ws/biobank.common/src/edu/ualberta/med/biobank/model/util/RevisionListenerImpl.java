package edu.ualberta.med.biobank.model.util;

import java.io.Serializable;
import java.text.MessageFormat;

import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import edu.ualberta.med.biobank.model.RevisedEntity;
import edu.ualberta.med.biobank.model.Revision;
import edu.ualberta.med.biobank.model.User;

public class RevisionListenerImpl
    implements EntityTrackingRevisionListener {
    @SuppressWarnings("nls")
    private static final String WRONG_REVISION_ENTITY =
        "Unexpected RevisionEntity: {0}, was expecting: {1}";
    private static final ThreadLocal<User> THREAD_USER =
        new ThreadLocal<User>();

    public static void setUser(User user) {
        THREAD_USER.set(user);
    }

    @Override
    public void newRevision(Object revisionEntity) {
        Revision revision = asRevision(revisionEntity);
        User user = THREAD_USER.get();
        revision.setUser(user);
    }

    @Override
    public void entityChanged(@SuppressWarnings("rawtypes") Class entityClass,
        String entityName, Serializable entityId, RevisionType revisionType,
        Object revisionEntity) {
        Revision revision = asRevision(revisionEntity);
        
        RevisedEntity revisedEntity = new RevisedEntity();
        revisedEntity.setRevision(revision);
        revisedEntity.setType(entityClass);
        
        revision.getRevisedEntities().add(revisedEntity);
    }

    private static Revision asRevision(Object o) {
        if (o instanceof Revision) return (Revision) o;

        throw new IllegalArgumentException(
            MessageFormat.format(WRONG_REVISION_ENTITY,
                o.getClass().getName(),
                Revision.class.getName()));
    }
}