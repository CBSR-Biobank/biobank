package edu.ualberta.med.biobank.model.envers;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.event.EnversListener;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEventListener;

public class AuditWriterFactory {
    private AuditWriterFactory() {
    }

    public static AuditWriter get(Session session)
        throws AuditException {
        SessionImplementor sessionImpl = getSessionImplementor(session);

        final EventListenerRegistry listenerRegistry = sessionImpl
            .getFactory()
            .getServiceRegistry()
            .getService(EventListenerRegistry.class);

        for (PostInsertEventListener listener : listenerRegistry
            .getEventListenerGroup(EventType.POST_INSERT)
            .listeners()) {
            if (listener instanceof EnversListener) {
                return new AuditWriterImpl(
                    ((EnversListener) listener).getAuditConfiguration(),
                    session);
            }
        }

        throw new AuditException(
            "Cannot find an envers listener. Envers listeners must not have" +
                " been properly configured.");
    }

    private static SessionImplementor getSessionImplementor(Session session) {
        SessionImplementor sessionImpl;
        if (!(session instanceof SessionImplementor)) {
            sessionImpl = (SessionImplementor) session
                .getSessionFactory()
                .getCurrentSession();
        } else {
            sessionImpl = (SessionImplementor) session;
        }
        return sessionImpl;
    }
}
