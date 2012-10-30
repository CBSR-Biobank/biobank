package edu.ualberta.med.biobank.model.util;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.ConstraintViolationAssertion;
import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasTimeInserted;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class HasXHelper {
    public static void checkEmptyName(Session session, HasDescription named) {
        try {
            named.setDescription(null);
            session.save(named);
            session.flush();
            Assert.fail("null name should not be allowed");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
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
            new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
                .withRootBean(named)
                .withPropertyPath("name")
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
            new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
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
            new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
                .withRootBean(shortNamed)
                .withPropertyPath("nameShort")
                .assertIn(e);
        }
    }

    public static <T extends HasName> void checkDuplicateName(
        Session session, T original, T duplicate) {
        Transaction tx = session.getTransaction();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("duplicate nameShort should not be allowed");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "nameShort" })
                .assertIn(e);
        }
    }

    public static void checkNullCreatedAt(Session session,
        HasTimeInserted hasTimeCreated) {
        try {
            hasTimeCreated.setTimeInserted(null);
            session.save(hasTimeCreated);
            session.flush();
            Assert.fail("null createdAt should not be allowed");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion().withAnnotationClass(NotNull.class)
                .withRootBean(hasTimeCreated)
                .withPropertyPath("timeCreated")
                .assertIn(e);
        }
    }

    private HasXHelper() {
        // static class
    }
}
