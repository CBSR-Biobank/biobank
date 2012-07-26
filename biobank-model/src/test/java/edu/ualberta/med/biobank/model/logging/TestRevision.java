package edu.ualberta.med.biobank.model.logging;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Revision;
import edu.ualberta.med.biobank.model.Site;

public class TestRevision extends LoggingTest {
    @Test
    public void revisionEntityTypes() {
        Transaction tx = session.beginTransaction();
        Site site = factory.createCenter();
        tx.commit();

        tx = session.beginTransaction();
        String oldName = site.getName();
        String newName = oldName + "X";
        site.setName(newName);
        session.update(site);
        tx.commit();

        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void twoSessions() {
        session.beginTransaction();
        Site site = factory.createCenter();
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
