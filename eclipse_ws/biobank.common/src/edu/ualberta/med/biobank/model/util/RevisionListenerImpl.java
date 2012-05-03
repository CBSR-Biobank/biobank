package edu.ualberta.med.biobank.model.util;

import java.text.MessageFormat;

import org.hibernate.envers.RevisionListener;

import edu.ualberta.med.biobank.model.Revision;
import edu.ualberta.med.biobank.model.User;

public class RevisionListenerImpl implements RevisionListener {
    private static final String UNEXPECTED_REVISION_ENTITY_MSG =
        "Unexpected RevisionEntity {0}. Expected {1}";
    private static final ThreadLocal<User> THREAD_USER =
        new ThreadLocal<User>();

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof Revision) {
            Revision revision = (Revision) revisionEntity;
            User user = THREAD_USER.get();

            revision.setUser(user);
        } else {
            String msg = MessageFormat.format(UNEXPECTED_REVISION_ENTITY_MSG,
                revisionEntity.getClass().getName(),
                Revision.class.getName());

            throw new RuntimeException(msg);
        }
    }

    public static void setUser(User user) {
        THREAD_USER.set(user);
    }
}