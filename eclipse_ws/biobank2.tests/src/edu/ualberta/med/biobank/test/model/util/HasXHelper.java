package edu.ualberta.med.biobank.test.model.util;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.HasActivityStatus;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.AssertMore.Attr;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class HasXHelper {
    public static <T extends HasName> void checkDuplicateName(Session session,
        T original, T duplicate) {
        Transaction tx = session.getTransaction();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two objects with the same name: "
                + original.getClass().getName());
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "name" }));
        }
    }

    public static <T extends HasNameShort> void checkDuplicateNameShort(
        Session session, T original, T duplicate) {
        Transaction tx = session.getTransaction();

        duplicate.setNameShort(original.getNameShort());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two objects with the same nameShort: "
                + original.getClass().getName());
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "nameShort" }));
        }
    }

    public static void checkExpectedActivityStatusIds(Session session,
        HasActivityStatus hasActivityStatus) {
        Transaction tx = session.getTransaction();

        Query query = HibernateHelper.getDehydratedPropertyQuery(
            session, hasActivityStatus, "activityStatus");

        try {
            for (ActivityStatus activityStatus : ActivityStatus.values()) {
                hasActivityStatus.setActivityStatus(activityStatus);
                session.update(hasActivityStatus);
                session.flush();

                int id = ((Number) query.uniqueResult()).intValue();
                Assert.assertEquals("persisted id does not match enum's id",
                    activityStatus.getId(), id);
            }
        } finally {
            tx.rollback();
        }
    }

    private HasXHelper() {
        // static class
    }
}
