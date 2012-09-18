package edu.ualberta.med.biobank.model.util;

import org.hibernate.Hibernate;

public class ProxyUtil {
    public static boolean isClassEqual(Object a, Object b) {
        // note that HibernateProxyHelper.getClassWithoutInitializingProxy(o)
        // does not seem to work properly in terms of returning the actual
        // class, it may return a superclass, such as, Center. However,
        // Hibernate.getClass() seems to always return the correct instance.
        Class<?> classA = Hibernate.getClass(a);
        Class<?> classB = Hibernate.getClass(b);
        return (classA == classB);
    }
}
