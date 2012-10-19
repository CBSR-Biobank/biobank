package edu.ualberta.med.biobank.model.envers;

import java.lang.reflect.Field;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.synchronization.AuditProcess;
import org.hibernate.event.spi.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditWriterImpl
    implements AuditWriter {
    private static final Logger log = LoggerFactory
        .getLogger(AuditWriterImpl.class);

    private final AuditProcess process;
    private final SessionImplementor sessionImpl;
    private final Field revisionDataField;

    public AuditWriterImpl(AuditConfiguration envCfg, Session session) {
        if (!(session instanceof EventSource)) {
            throw new IllegalArgumentException(
                "The given session is not an EventSource.");
        }

        this.process = envCfg.getSyncManager().get((EventSource) session);
        this.sessionImpl = getSessionImplementor(session);

        try {
            revisionDataField = AuditProcess.class
                .getDeclaredField("revisionData");
            revisionDataField.setAccessible(true);
        } catch (Exception e) {
            log.error("Unable to access revision data field", e);
            throw new IllegalStateException(
                "Unable to access revision data field",
                e);
        }
    }

    @Override
    public void flush() {
        process.doBeforeTransactionCompletion(sessionImpl);
        try {
            // it's unfortunate that we need to use reflection, but we _must_
            // clear the revision entity so that a new one is generated
            // instead of updating the old one
            revisionDataField.set(process, null);
        } catch (Exception e) {
            log.error("Unable to clear revision data field", e);
            throw new AuditException(
                "Unable to clear revision data field",
                e);
        }
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
