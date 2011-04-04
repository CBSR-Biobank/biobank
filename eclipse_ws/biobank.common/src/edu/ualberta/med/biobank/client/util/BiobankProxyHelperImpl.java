package edu.ualberta.med.biobank.client.util;

import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.client.proxy.ProxyHelperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

public class BiobankProxyHelperImpl extends ProxyHelperImpl {

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
    public Object convertToObject(Object proxyObject) throws Throwable {
        if (proxyObject instanceof NotAProxy) {
            return proxyObject;
        }

        return super.convertToObject(proxyObject);
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
        if (methodName.startsWith("get") && (args == null || args.length == 0)) {
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
                .equalsIgnoreCase("java.util.Collection")) {
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
