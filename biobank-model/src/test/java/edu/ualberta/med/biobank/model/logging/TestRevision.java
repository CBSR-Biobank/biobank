package edu.ualberta.med.biobank.model.logging;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.envers.Revision;

public class TestRevision extends LoggingTest {
    @Test
    public void revisionEntityTypes() {
        Transaction tx = session.beginTransaction();
        Center center = factory.createCenter();
        tx.commit();

        tx = session.beginTransaction();
        String oldName = center.getName();
        String newName = oldName + "X";
        center.setName(newName);
        session.update(center);
        tx.commit();

        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void twoSessions() {
        session.beginTransaction();
        Center center = factory.createCenter();
        session.getTransaction().commit();

        Session s1 = openSession();
        Session s2 = openSession();

        s1.beginTransaction();

    }

    @Test
    public void nonAuditedCommit() {
        session.beginTransaction();
        Revision tmp = new Revision();
        session.save(tmp);
        session.getTransaction().commit();
    }
}
