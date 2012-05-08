package edu.ualberta.med.biobank.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

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

    public static void assertContainsAnnotation(ConstraintViolationException e,
        Class<?> annotationClass, Attr... expectedAttrs) {
        Collection<String> annotations = new ArrayList<String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();

            Object annotation = cd.getAnnotation();
            Map<String, Object> attrs = cd.getAttributes();

            annotations.add(annotation.toString());

            if (!annotationClass.isAssignableFrom(annotation.getClass())) {
                continue;
            }
            if (expectedAttrs != null && !attrsContains(attrs, expectedAttrs)) {
                continue;
            }

            return;
        }

        Assert.fail(ConstraintViolationException.class.getSimpleName()
            + " does not contain an expected "
            + ConstraintDescriptor.class.getSimpleName()
            + " with an annotation of type " + annotationClass.getName()
            + " and properties: " + expectedAttrs
            + ". Instead, it contains the"
            + " annotation(s): " + Arrays.toString(annotations.toArray()));
    }

    /**
     * @param attrs
     * @param expectedAttrs
     * @return True if the first map contains all {@link Attr}-s specified,
     *         otherwise false.
     */
    private static boolean attrsContains(Map<String, Object> attrs,
        Attr... expectedAttrs) {
        for (Attr attr : expectedAttrs) {
            if (!attrs.containsKey(attr.key) ||
                !arrayWiseEq(attrs.get(attr.key), attr.value)) {
                return false;
            }
        }
        return true;
    }

    private static boolean arrayWiseEq(Object a, Object b) {
        // because new String[] { "a" }.equals(new String[] { "a" }) is false.
        return Arrays.deepEquals(new Object[] { a }, new Object[] { b });
    }

    public static void assertMessageContains(Throwable t, String substring) {
        if (!t.getMessage().contains(substring)) {
            Assert.fail("Expected exception " + t.getClass().getName()
                + "' to contain the substring '" + substring
                + "' but instead got message: '" + t.getMessage() + "'");
        }
    }

    public static void assertInitialized(Object o) {
        if (!Hibernate.isInitialized(o)) {
            Assert.fail("Expected initialized object " + o
                + ", but was uninitialized.");
        }
    }

    public static void assertNotInitialized(Object o) {
        if (Hibernate.isInitialized(o)) {
            Assert.fail("Expected uninitialized object " + o
                + ", but was initialized.");
        }
    }

    public static void assertPropertyInitialized(Object o, String propertyName) {
        if (!Hibernate.isPropertyInitialized(o, propertyName)) {
            Assert.fail("Expected initialized property named '" + propertyName
                + "' of object " + o + ", but was uninitialized.");
        }
    }

    public static void assertPropertyNotInitialized(Object o,
        String propertyName) {
        if (Hibernate.isPropertyInitialized(o, propertyName)) {
            Assert.fail("Expected uninitialized property named '"
                + propertyName + "' of object " + o + ", but was initialized.");
        }
    }

    public static class Attr {
        private final String key;
        private final Object value;

        public Attr(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
