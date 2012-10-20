package edu.ualberta.med.biobank.model.listener;

import org.hibernate.Session;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import edu.ualberta.med.biobank.model.envers.Revision;

/**
 * This class does things I would like to do by extending the Envers code, but
 * that is too difficult right now, or involves a big copy and paste.
 * <p>
 * Whenever a {@link Revision} is inserted, add a listener to update the
 * revision right after it is committed, with the "commit time," in a new
 * transaction. Ideally, this would happen in
 * {@link org.hibernate.envers.synchronization.AuditProcess}, but that class is
 * not easy to extend.
 * 
 * @author Jonathan Ferland
 */
public class UpdateRevisionCommittedAtListener
    implements PreInsertEventListener {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();

        if (entity instanceof Revision) {
            Revision revision = (Revision) entity;
            event.getSession().getActionQueue().registerProcess(
                new UpdateCommittedAtAfterTransactionCompletion(revision));
        }
        return false;
    }

    static class UpdateCommittedAtAfterTransactionCompletion
        implements AfterTransactionCompletionProcess {
        private final Revision revision;

        public UpdateCommittedAtAfterTransactionCompletion(Revision revision) {
            this.revision = revision;
        }

        @Override
        public void doAfterTransactionCompletion(boolean success,
            SessionImplementor impl) {
            if (success) {
                Session session = (Session) impl;
                updateCommittedAtInOtherSession(impl);
                session.refresh(revision);
            }
        }

        private void updateCommittedAtInOtherSession(SessionImplementor impl) {
            Session otherSession = impl.getFactory().openSession();

            otherSession.beginTransaction();
            Revision revision = (Revision) otherSession
                .load(Revision.class, this.revision.getId());

            long time = System.currentTimeMillis();
            revision.setCommittedAt(time);
            otherSession.update(revision);

            otherSession.getTransaction().commit();
            otherSession.close();
        }
    }
}
