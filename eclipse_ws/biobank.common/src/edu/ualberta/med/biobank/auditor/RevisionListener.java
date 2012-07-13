package edu.ualberta.med.biobank.auditor;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.action.AfterTransactionCompletionProcess;
import org.hibernate.action.BeforeTransactionCompletionProcess;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.AuditEntitiesConfiguration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import edu.ualberta.med.biobank.model.Revision;

/**
 * This class does things I would like to do by extending the Envers code, but
 * that is too difficult right now, or involves a big copy and paste.
 * <ol>
 * <li>Look for updates on the audit tables and ensure that the end revision
 * number is greater than the revision number (this check <em>should</em> happen
 * in {@link org.hibernate.envers.strategy.ValidityAuditStrategy}, but doesn't).
 * </li>
 * <li>Whenever a {@link Revision} is inserted, add a listener to update the
 * revision right before it is committed, with the "commit time" (should happen
 * in {@link org.hibernate.envers.synchronization.AuditProcess})</li>
 * </ol>
 * 
 * @author Jonathan Ferland
 */
public class RevisionListener
    implements PreInsertEventListener, PreUpdateEventListener, Initializable {
    private static final long serialVersionUID = 1L;

    private AuditConfiguration verCfg;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();

        if (entity instanceof Revision) {
            Revision revision = (Revision) entity;

            event.getSession().getActionQueue().registerProcess(
                new UpdateRevisionCommittedAtBeforeCompletion(revision));

            event.getSession().getActionQueue().registerProcess(
                new UpdateRevisionCommittedAtAfterCompletion(revision));
        }

        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        String entityName = event.getPersister().getEntityName();
        if (isEntityNameRevisionData(entityName)) {
            Object entity = event.getEntity();

            @SuppressWarnings("unchecked")
            Map<String, Object> revisionData = (Map<String, Object>) entity;
            checkEndRevision(revisionData);
        }
        return false;
    }

    @Override
    public void initialize(Configuration cfg) {
        verCfg = AuditConfiguration.getFor(cfg);
    }

    private boolean isEntityNameRevisionData(String entityName) {
        return verCfg.getEntCfg()
            .getEntityNameForVersionsEntityName(entityName) != null;
    }

    @SuppressWarnings("nls")
    private void checkEndRevision(Map<String, Object> revisionData) {
        AuditEntitiesConfiguration auditEntCfg = verCfg.getAuditEntCfg();
        String revFieldName = auditEntCfg.getRevisionFieldName();
        String revEndFieldName = auditEntCfg.getRevisionEndFieldName();

        Revision revision = (Revision) revisionData.get(revFieldName);
        Revision endRevision = (Revision) revisionData.get(revEndFieldName);

        if (endRevision != null && revision.getId() >= endRevision.getId()) {
            throw new RuntimeException("Revision data with a revision end " +
                "must have ");
        }
    }

    private static void updateRevisionCommittedAt(Session session,
        Revision revision) {
        long time = System.currentTimeMillis();
        revision.setCommittedAt(time);
        session.update(revision);
        session.flush();
    }

    static class UpdateRevisionCommittedAtBeforeCompletion
        implements BeforeTransactionCompletionProcess {
        private final Revision revision;

        public UpdateRevisionCommittedAtBeforeCompletion(Revision revision) {
            this.revision = revision;
        }

        @Override
        public void doBeforeTransactionCompletion(SessionImplementor impl) {
            Session session = (Session) impl;
            session.flush();

            updateRevisionCommittedAt(session, revision);
        }
    }

    static class UpdateRevisionCommittedAtAfterCompletion
        implements AfterTransactionCompletionProcess {
        private final Revision revision;

        public UpdateRevisionCommittedAtAfterCompletion(Revision revision) {
            this.revision = revision;
        }

        @Override
        public void doAfterTransactionCompletion(boolean success,
            SessionImplementor impl) {
            if (success) {
                Session session = (Session) impl;
                session.beginTransaction();
                updateRevisionCommittedAt(session, revision);
                session.getTransaction().commit();
            }
        }
    }
}
