package edu.ualberta.med.biobank.common.wrappers.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class LazyMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final LazyArg[] lazyArgs;

    /**
     * Replaces {n} with the corresponding argument value, like
     * {@code MessageFormat}.
     * 
     * @see MessageFormat
     * @param message
     * @param lazyArgs
     */
    public LazyMessage(String message, LazyArg... lazyArgs) {
        this.message = message;
        this.lazyArgs = lazyArgs;
    }

    public String format(Session session) {
        List<String> args = new ArrayList<String>();

        for (LazyArg lazyArg : lazyArgs) {
            String arg = lazyArg.getValue(session);
            args.add(arg);
        }

        String result = MessageFormat.format(message, args.toArray());
        return result;
    }

    public static <T> LazyArg newArg(ModelWrapper<T> wrapper,
        Property<?, ? super T> property) {
        return new WrapperLazyArg(wrapper, property);
    }

    public static <T, U> LazyArg newArg(Class<T> modelClass,
        Property<U, ? super T> idProperty, U id, Property<?, ? super T> property) {
        return new ModelIdLazyArg(modelClass, idProperty, id, property);
    }

    public interface LazyArg extends Serializable {
        public String getValue(Session session);
    }

    private static class ModelIdLazyArg implements LazyArg {
        private static final long serialVersionUID = 1L;
        private static final String SELECT_PROPERTY_HQL = "SELECT o.{0} FROM {1} o WHERE o.{2} = ?"; //$NON-NLS-1$

        private final Class<?> modelClass;
        private final Property<?, ?> idProperty;
        private final Object id;
        private final Property<?, ?> property;

        <T, U> ModelIdLazyArg(Class<T> modelClass,
            Property<U, ? super T> idProperty, U id,
            Property<?, ? super T> property) {
            this.modelClass = modelClass;
            this.idProperty = idProperty;
            this.id = id;
            this.property = property;
        }

        @Override
        public String getValue(Session session) {
            String value = null;

            String hql = MessageFormat.format(SELECT_PROPERTY_HQL,
                property.getName(), modelClass.getName(), idProperty.getName());
            Query query = session.createQuery(hql);
            query.setParameter(0, id);

            List<?> results = query.list();
            for (Object result : results) {
                value = result.toString();
                break;
            }

            return value;
        }
    }

    private static class WrapperLazyArg implements LazyArg {
        private static final long serialVersionUID = 1L;
        private static final String SELECT_PROPERTY_HQL = "SELECT o.{0} FROM {1} o WHERE o = ?"; //$NON-NLS-1$

        private final Object model;
        private final Class<?> modelClass;
        private final Property<?, ?> property;

        <T> WrapperLazyArg(ModelWrapper<T> wrapper,
            Property<?, ? super T> property) {
            this.model = ProxyUtil.convertProxyToObject(wrapper
                .getWrappedObject());
            this.modelClass = wrapper.getWrappedClass();
            this.property = property;
        }

        @Override
        public String getValue(Session session) {
            String value = null;

            String hql = MessageFormat.format(SELECT_PROPERTY_HQL,
                property.getName(), modelClass.getName());
            Query query = session.createQuery(hql);
            query.setParameter(0, model);

            List<?> results = query.list();
            for (Object result : results) {
                value = result.toString();
                break;
            }

            return value;
        }
    }
}
