package edu.ualberta.med.biobank.test.model.logging;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.DbTest;

public class TestRevision extends DbTest {
    @Test
    public void correctEntityTypes() {
        Transaction tx = session.beginTransaction();
        Site site = factory.createSite();
        tx.commit();

        tx = session.beginTransaction();
        tx.commit();

        try {
        } finally {
            tx.rollback();
        }
    }
}
