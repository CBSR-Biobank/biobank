package edu.ualberta.med.biobank.test.model.logging;

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
    }

    @Test
    public void executingUser() {

    }
}
