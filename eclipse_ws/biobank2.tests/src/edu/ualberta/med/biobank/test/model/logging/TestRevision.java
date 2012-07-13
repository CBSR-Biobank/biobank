package edu.ualberta.med.biobank.test.model.logging;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Site;

public class TestRevision extends LoggingTest {
    @Test
    public void revisionEntityTypes() {
        Transaction tx = session.beginTransaction();
        Site site = factory.createSite();
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
        Site site = factory.createSite();
        session.getTransaction().commit();

        Session s1 = openSession();
        Session s2 = openSession();

        s1.beginTransaction();

    }
}
