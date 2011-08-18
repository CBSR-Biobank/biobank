package edu.ualberta.med.biobank.client.util;

import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.client.proxy.ProxyHelperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.aop.framework.Advised;

public class BiobankProxyHelperImpl extends ProxyHelperImpl {
    private static Logger log = Logger.getLogger(ProxyHelperImpl.class
        .getName());

    @Override
    public Object convertToProxy(ApplicationService as, Object obj) {
        if (obj instanceof AbstractBiobankListProxy) {
            return convertListProxyToProxy(as,
                (AbstractBiobankListProxy<?>) obj);
        }
        if (obj instanceof NotAProxy) {
            return obj;
        }
        return super.convertToProxy(as, obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convertToObject(Object proxyObject) throws Throwable {
        if (proxyObject instanceof NotAProxy) {
            return proxyObject;
        }

        Map<Object, Object> map = new IdentityHashMap<Object, Object>();

        if (proxyObject instanceof Collection) {
            Collection<Object> unwrapedProxyObjects = new ArrayList<Object>();
            Collection<Object> batchQueries = (Collection<Object>) proxyObject;
            for (Object tempProxyObject : batchQueries) {
                Object unwrapedProxyObject = convertToObject(map,
                    tempProxyObject);
                unwrapedProxyObjects.add(unwrapedProxyObject);
            }
            return unwrapedProxyObjects;
        } else {
            Object unwrapedProxyObject = convertToObject(map, proxyObject);
            return unwrapedProxyObject;
        }
    }

    @SuppressWarnings("unchecked")
    private Object convertToObject(Map<Object, Object> map, Object proxyObject)
        throws Exception {
        if (isPrimitiveObject(proxyObject) || proxyObject instanceof Class
            || proxyObject instanceof DetachedCriteria) {
            return proxyObject;
        }

        Object plainObject = convertProxyToObject(proxyObject);
        Object mapPlainObject = map.get(plainObject);
        if (mapPlainObject != null)
            return mapPlainObject;

        map.put(plainObject, plainObject);
        Method[] methods = plainObject.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get") //$NON-NLS-1$
                && method.getParameterTypes().length == 0) {
                Object childObject = method.invoke(plainObject);
                if (!(childObject == null || isPrimitiveObject(childObject) || childObject instanceof Class)
                    && Hibernate.isInitialized(childObject)) {

                    if (childObject instanceof ListProxy) {
                        ListProxy objectProxy = (ListProxy) childObject;
                        int associationSize = objectProxy.size();
                        if (associationSize != objectProxy.getListChunk()
                            .size()) {
                            String associationName = null;
                            if (objectProxy.getListChunk().size() > 0) {
                                String cglibClassName = objectProxy
                                    .getListChunk().get(0).getClass().getName();
                                int startindex = cglibClassName
                                    .indexOf("$$EnhancerByCGLIB"); //$NON-NLS-1$
                                associationName = cglibClassName.substring(0,
                                    startindex);
                            }
                            String className = objectProxy.getTargetClassName();
                            throw new Exception(
                                "update or delete elements for the association " //$NON-NLS-1$
                                    + associationName
                                    + " is not allowed.association " //$NON-NLS-1$
                                    + associationName
                                    + " for Class " //$NON-NLS-1$
                                    + className
                                    + " is not fully initialized. Total size of assocation in database " //$NON-NLS-1$
                                    + associationSize + " retrieved size is " //$NON-NLS-1$
                                    + objectProxy.getListChunk().size() + "."); //$NON-NLS-1$
                        }
                    }
                    log.debug("invoking " + method.getName() + " on class " //$NON-NLS-1$ //$NON-NLS-2$
                        + plainObject.getClass());
                    String setterMethodName = "set" //$NON-NLS-1$
                        + method.getName().substring(3);
                    if (childObject instanceof List
                        && !(childObject instanceof Set)) {
                        Object plainObjectCollection = convertProxyToObject(childObject);
                        Collection<Object> objects = (Collection<Object>) plainObjectCollection;
                        Collection<Object> tempObjects = new ArrayList<Object>();
                        for (Object object : objects) {
                            Object child = convertToObject(map, object);
                            tempObjects.add(child);
                        }
                        Method setterMethod = plainObject.getClass().getMethod(
                            setterMethodName,
                            new Class[] { method.getReturnType() });
                        setterMethod.invoke(plainObject, tempObjects);
                    } else if (childObject instanceof Collection) {
                        Object plainObjectCollection = convertProxyToObject(childObject);
                        Collection<Object> objects = (Collection<Object>) plainObjectCollection;
                        Collection<Object> tempObjects = new HashSet<Object>();
                        for (Object object : objects) {
                            Object child = convertToObject(map, object);
                            tempObjects.add(child);
                        }
                        Method setterMethod = plainObject.getClass().getMethod(
                            setterMethodName,
                            new Class[] { method.getReturnType() });
                        setterMethod.invoke(plainObject, tempObjects);
                    } else {
                        try {
                            Method setterMethod = plainObject.getClass()
                                .getMethod(setterMethodName,
                                    new Class[] { method.getReturnType() });
                            Object child = convertToObject(map, childObject);
                            setterMethod.invoke(plainObject, child);
                        } catch (Exception e) {
                            //
                        }
                    }
                }
            }
        }
        return plainObject;
    }

    private boolean isPrimitiveObject(Object obj) {
        if (obj == null || obj instanceof Integer || obj instanceof Float
            || obj instanceof Double || obj instanceof Character
            || obj instanceof Long || obj instanceof Boolean
            || obj instanceof Byte || obj instanceof Short
            || obj instanceof String || obj instanceof Date) {
            return true;
        } else
            return false;
    }

    private Object convertProxyToObject(Object obj) {
        if (obj == null)
            return null;
        while (obj != null && obj instanceof Advised) {
            Advised proxy = (org.springframework.aop.framework.Advised) obj;
            try {
                obj = proxy.getTargetSource().getTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    private Object convertListProxyToProxy(ApplicationService as,
        AbstractBiobankListProxy<?> proxy) {
        proxy.setAppService(as);
        // We don't convert the chunk: it is suppose to contain only simple
        // object, no model objects
        // List<Object> chunk = proxy.getListChunk();
        // @SuppressWarnings("unchecked")
        // List<Object> modifiedChunk = new ArrayList<Object>(
        // (Collection<Object>) convertToProxy(as, chunk));
        // proxy.setListChunk(modifiedChunk);
        return proxy;
    }

    /**
     * Copy of ProxyHelperImpl.lazyLoad
     */
    @Override
    public Object lazyLoad(ApplicationService as, MethodInvocation invocation)
        throws Throwable {
        Object bean = invocation.getThis();
        Method method = invocation.getMethod();
        String methodName = method.getName();
        Object args[] = invocation.getArguments();
        if (methodName.startsWith("get") && (args == null || args.length == 0)) { //$NON-NLS-1$
            String fieldName = methodName.substring(3);
            fieldName = Character.toLowerCase(fieldName.charAt(0))
                + fieldName.substring(1);

            Field field = getField(bean, fieldName);

            if (field == null) { // Fix for [#8200] Query generator assumes
                                 // lowercase association names
                fieldName = Character.toUpperCase(fieldName.charAt(0))
                    + fieldName.substring(1);
                field = getField(bean, fieldName);
            }

            Object obj = as.getAssociation(createClone(bean), fieldName);
            Object value = obj;

            if (obj instanceof ListProxy)
                ((ListProxy) obj).setAppService(as);

            // Add this part for our own ListProxy
            if (obj instanceof AbstractBiobankListProxy)
                ((AbstractBiobankListProxy<?>) obj).setAppService(as);

            if (!field.getType().getName()
                .equalsIgnoreCase("java.util.Collection")) { //$NON-NLS-1$
                Collection<?> results = (Collection<?>) obj;
                if (results.size() == 1)
                    value = results.iterator().next();
                else if (results.size() == 0)
                    value = null;
                else
                    throw new Exception(
                        "Invalid data obtained from the database for the " //$NON-NLS-1$
                            + fieldName + " attribute of the " //$NON-NLS-1$
                            + bean.getClass().getName());
            }

            Class<?>[] params = new Class[] { field.getType() };
            Method setter = getMethod(bean,
                "set" + method.getName().substring(3), params); //$NON-NLS-1$
            if (setter != null && params != null && params.length == 1)
                setter.invoke(bean, new Object[] { value });

            return value;
        }

        return null;
    }

    @Override
    protected Object convertObjectToProxy(ApplicationService as, Object obj) {
        if (null == obj)
            return null;
        if (obj instanceof Map)
            return obj;
        return super.convertObjectToProxy(as, obj);
    }
}
