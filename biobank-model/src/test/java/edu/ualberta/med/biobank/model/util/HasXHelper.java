package edu.ualberta.med.biobank.model.util;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class HasXHelper {
    public static void checkEmptyName(Session session, HasDescription named) {
        try {
            named.setDescription(null);
            session.save(named);
            session.flush();
            Assert.fail("null name should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(named)
                .withPropertyPath("name")
                .assertIn(e);
        }

        try {
            named.setDescription("");
            session.save(named);
            session.flush();
            Assert.fail("empty name should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(named)
                .withPropertyPath("name")
                .assertIn(e);
        }
    }

    public static <T extends HasDescription> void checkDuplicateName(
        Session session,
        T original, T duplicate) {
        Transaction tx = session.getTransaction();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("duplicate name should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "name" })
                .assertIn(e);
        }
    }

    public static void checkEmptyNameShort(Session session,
        HasName shortNamed) {
        try {
            shortNamed.setName(null);
            session.save(shortNamed);
            session.flush();
            Assert.fail("null nameShort should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(shortNamed)
                .withPropertyPath("nameShort")
                .assertIn(e);
        }

        try {
            shortNamed.setName("");
            session.save(shortNamed);
            session.flush();
            Assert.fail("empty nameShort should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(shortNamed)
                .withPropertyPath("nameShort")
                .assertIn(e);
        }
    }

    public static <T extends HasName> void checkDuplicateNameShort(
        Session session, T original, T duplicate) {
        Transaction tx = session.getTransaction();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("duplicate nameShort should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "nameShort" })
                .assertIn(e);
        }
    }

    public static void checkNullCreatedAt(Session session,
        HasCreationTime hasCreatedAt) {
        try {
            hasCreatedAt.setTimeCreated(null);
            session.save(hasCreatedAt);
            session.flush();
            Assert.fail("null createdAt should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(hasCreatedAt)
                .withPropertyPath("createdAt")
                .assertIn(e);
        }
    }

    public static void checkNullActivityStatus(Session session,
        HasActivityStatus hasActivityStatus) {
        try {
            hasActivityStatus.setActivityStatus(null);
            session.save(hasActivityStatus);
            session.flush();
            Assert.fail("null activityStatus should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(hasActivityStatus)
                .withPropertyPath("activityStatus")
                .assertIn(e);
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
                    activityStatus.getId(), new Integer(id));
            }
        } finally {
            tx.rollback();
        }
    }

    private HasXHelper() {
        // static class
    }
}
