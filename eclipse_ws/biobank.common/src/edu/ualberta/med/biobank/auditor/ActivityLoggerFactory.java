package edu.ualberta.med.biobank.auditor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Synchronization;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ActivityLoggerFactory {
    private static final Map<Transaction, ActivityLogger> activityLoggers =
        new ConcurrentHashMap<Transaction, ActivityLogger>();

    public static ActivityLogger get(Session session) {
        Transaction tx = session.getTransaction();
        tx.registerSynchronization(new Synchronization() {
            @Override
            public void afterCompletion(int arg0) {
            }

            @Override
            public void beforeCompletion() {
            }
        });
        return null;
    }
}
