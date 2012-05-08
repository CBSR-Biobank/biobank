package edu.ualberta.med.biobank.test;

import junit.framework.Assert;

import org.hibernate.Hibernate;

public class AssertMore {
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
}
