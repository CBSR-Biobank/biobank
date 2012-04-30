package edu.ualberta.med.biobank.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Hibernate;

public class AssertMore {
    public static void assertContainsTemplate(ConstraintViolationException e,
        String messageTemplate) {
        Collection<String> messageTemplates = new ArrayList<String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            messageTemplates.add(violation.getMessageTemplate());
        }
        if (!messageTemplates.contains(messageTemplate)) {
            Assert.fail(ConstraintViolationException.class.getSimpleName()
                + " does not contain an expected "
                + ConstraintViolation.class.getName()
                + " with a message template of " + messageTemplate
                + ". Instead, it contains the message template(s): "
                + Arrays.toString(messageTemplates.toArray()));
        }
    }

    public static void assertMessageContains(Throwable t, String substring) {
        if (!t.getMessage().contains(substring)) {
            Assert.fail("Expected exception " + t.getClass().getName()
                + "' to contain the substring '" + substring
                + "' but instead got message: '" + t.getMessage() + "'");
        }
    }

    public static void assertInited(Object o) {
        if (!Hibernate.isInitialized(o)) {
            Assert.fail("Expected initialized object " + o
                + ", but was uninitialized.");
        }
    }

    public static void assertNotInited(Object o) {
        if (Hibernate.isInitialized(o)) {
            Assert.fail("Expected uninitialized object " + o
                + ", but was initialized.");
        }
    }

    public static void assertPropertyInited(Object o, String propertyName) {
        if (!Hibernate.isPropertyInitialized(o, propertyName)) {
            Assert.fail("Expected initialized property named '" + propertyName
                + "' of object " + o + ", but was uninitialized.");
        }
    }

    public static void assertPropertyNotInited(Object o, String propertyName) {
        if (Hibernate.isPropertyInitialized(o, propertyName)) {
            Assert.fail("Expected uninitialized property named '"
                + propertyName + "' of object " + o + ", but was initialized.");
        }
    }
}
