package edu.ualberta.med.biobank.test.model.util;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.HasActivityStatus;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.AssertMore.Attr;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class HasXHelper {
    public static void checkEmptyName(Session session, HasName named) {
        try {
            named.setName(null);
            session.save(named);
            session.flush();
            Assert.fail("name cannot be null");
        } catch (ConstraintViolationException e) {
            // TODO: make sure it's on the right object class and on the right
            // property name
            AssertMore.assertContainsAnnotation(e, NotEmpty.class);
        }

        try {
            named.setName("");
            session.save(named);
            session.flush();
            Assert.fail("name cannot be empty");
        } catch (ConstraintViolationException e) {
            // TODO: make sure it's on the right object class and on the right
            // property name
            AssertMore.assertContainsAnnotation(e, NotEmpty.class);
        }
    }

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

    public static void checkEmptyNameShort(Session session,
        HasNameShort shortNamed) {
        try {
            shortNamed.setNameShort(null);
            session.save(shortNamed);
            session.flush();
            Assert.fail("nameShort cannot be null");
        } catch (ConstraintViolationException e) {
            // TODO: make sure it's on the right object class and on the right
            // property name
            AssertMore.assertContainsAnnotation(e, NotEmpty.class);
        }

        try {
            shortNamed.setNameShort("");
            session.save(shortNamed);
            session.flush();
            Assert.fail("nameShort cannot be empty");
        } catch (ConstraintViolationException e) {
            // TODO: make sure it's on the right object class and on the right
            // property name
            AssertMore.assertContainsAnnotation(e, NotEmpty.class);
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
