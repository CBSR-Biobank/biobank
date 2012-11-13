package edu.ualberta.med.biobank.model.util;

import org.hibernate.Hibernate;

public class ProxyUtil {
    /**
     * Null safe check on whether the given objects have the same class.
     * 
     * @param a
     * @param b
     * @return true if the two objects have the same class, or are both null,
     *         otherwise return false.
     */
    public static boolean sameClass(Object a, Object b) {
        if (a == null ^ b == null) return false;
        if (a == null && b == null) return true;

        // note that HibernateProxyHelper.getClassWithoutInitializingProxy(o)
        // does not seem to work properly in terms of returning the actual
        // class, it may return a superclass. However, Hibernate.getClass()
        // seems to always return the correct instance.
        Class<?> classA = Hibernate.getClass(a);
        Class<?> classB = Hibernate.getClass(b);
        return (classA == classB);
    }
}