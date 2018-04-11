package edu.ualberta.med.biobank.client.util;

import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.client.proxy.ProxyHelperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;

public class BiobankProxyHelperImpl extends ProxyHelperImpl {
    private static Logger log = LoggerFactory
        .getLogger(BiobankProxyHelperImpl.class
            .getName());

    @Override
    public Object convertToProxy(ApplicationService as, Object obj) {
        if (obj instanceof AbstractBiobankListProxy) {
            return convertListProxyToProxy(as, (AbstractBiobankListProxy<?>) obj);
        } else if (obj instanceof NotAProxy) {
            return obj;
        } else if (obj instanceof BigDecimal) {
            return obj;
        }
        //OHSDEV
        return enhanceProxyObject(as, obj);
        //return super.convertToProxy(as, obj);
    }

    //OHSDEV
    //Retrieve the items in the inner listChunk object and add it back to the ListProxy object.
    //This is done so that in Java 1.7+, we end up having the correct elementData at the ArrayList level
    //And that data is the CGLIB enhanced ListChunk object rather than a raw ListChunk object.
    @SuppressWarnings({ "rawtypes", "nls" })
    private Object enhanceProxyObject(ApplicationService as, Object obj)
    {
		Object enhancedObject = super.convertToProxy(as, obj);
		if(enhancedObject instanceof ListProxy)
		{
			ListProxy objectProxy = (ListProxy) enhancedObject;
			if(enhancedObject != null)
			{
				int size = objectProxy.getListChunk().size();
				objectProxy.ensureCapacity(size);
				List chunk = objectProxy.getListChunk();

				for(int i=0; i<size; i++) {
					objectProxy.set(i, chunk.get(i));
				}
			}
			return objectProxy;
		}
		return enhancedObject;
    }

    @SuppressWarnings({ "rawtypes", "nls" })
    @Override
    protected Object convertCollectionToProxy(ApplicationService as,
        Collection collection) {
        if (null == collection) return null;
        Collection<Object> modifiedCollection;
        if (collection instanceof List)
            modifiedCollection = new ArrayList<Object>();
        else if (collection instanceof Set)
            modifiedCollection = new HashSet<Object>();
        else
            throw new RuntimeException("Unable to convert collection to proxy");
        for (Object obj : collection)
            modifiedCollection.add(convertToProxy(as, obj));
        return modifiedCollection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convertToObject(Object proxyObject) throws Throwable {
        if (proxyObject instanceof NotAProxy) {
            return proxyObject;
        } else if (proxyObject instanceof BigDecimal) {
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
        }
        Object unwrapedProxyObject = convertToObject(map, proxyObject);
        return unwrapedProxyObject;
    }

    @SuppressWarnings({ "unchecked", "nls" })
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
            if (method.getName().startsWith("get")
                && method.getParameterTypes().length == 0
                && !method.isAnnotationPresent(Transient.class)) {
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
                                    .indexOf("$$EnhancerByCGLIB");
                                associationName = cglibClassName.substring(0,
                                    startindex);
                            }
                            String className = objectProxy.getTargetClassName();
                            throw new Exception(
                                "update or delete elements for the association "
                                    + associationName
                                    + " is not allowed.association "
                                    + associationName
                                    + " for Class "
                                    + className
                                    + " is not fully initialized. Total size of assocation in database "
                                    + associationSize + " retrieved size is "
                                    + objectProxy.getListChunk().size() + ".");
                        }
                    }
                    log.trace("invoking {} on class {}", method.getName(), plainObject.getClass());
                    String setterMethodName = "set" + method.getName().substring(3);
                    if (childObject instanceof List && !(childObject instanceof Set)) {
                        Object plainObjectCollection = convertProxyToObject(childObject);
                        Collection<Object> objects = (Collection<Object>) plainObjectCollection;
                        Collection<Object> tempObjects = new ArrayList<Object>();
                        for (Object object : objects) {
                            Object child = convertToObject(map, object);
                            tempObjects.add(child);
                        }
                        Method setterMethod = plainObject.getClass().getMethod(setterMethodName,
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
                        Method setterMethod = plainObject.getClass().getMethod(setterMethodName,
                            new Class[] { method.getReturnType() });
                        setterMethod.invoke(plainObject, tempObjects);
                    } else {
                        try {
                            Method setterMethod = plainObject.getClass().getMethod(setterMethodName,
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
        }
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
    @SuppressWarnings("nls")
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

            if (!Collection.class.isAssignableFrom(field.getType())) {
                Collection<?> results = (Collection<?>) obj;
                if (results.size() == 1)
                    value = results.iterator().next();
                else if (results.size() == 0)
                    value = null;
                else
                    throw new Exception(
                        "Invalid data obtained from the database for the "
                            + fieldName + " attribute of the "
                            + bean.getClass().getName());
            }

            Class<?>[] params = new Class[] { field.getType() };
            Method setter = getMethod(bean,
                "set" + method.getName().substring(3), params);
            if (setter != null && params.length == 1)
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
