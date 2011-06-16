package edu.ualberta.med.biobank.common.wrappers.checks;

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
    private final LazyArg<?>[] lazyArgs;

    /**
     * Replaces {n} with the corresponding argument value, like
     * {@code MessageFormat}.
     * 
     * @see MessageFormat
     * @param message
     * @param lazyArgs
     */
    public LazyMessage(String message, LazyArg<?>... lazyArgs) {
        this.message = message;
        this.lazyArgs = lazyArgs;
    }

    public String format(Session session) {
        List<String> args = new ArrayList<String>();

        for (LazyArg<?> lazyArg : lazyArgs) {
            String arg = lazyArg.getValue(session);
            args.add(arg);
        }

        String result = MessageFormat.format(message, args.toArray());
        return result;
    }

    public static class LazyArg<E> implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final String SELECT_PROPERTY_HQL = "SELECT o.{0} FROM {1} o WHERE o = ?";

        private final E model;
        private final Class<E> modelClass;
        private final Property<?, ? super E> property;

        LazyArg(ModelWrapper<E> wrapper, Property<?, ? super E> property) {
            this.model = wrapper.getWrappedObject();
            this.modelClass = wrapper.getWrappedClass();
            this.property = property;
        }

        public static <T> LazyArg<T> create(ModelWrapper<T> wrapper,
            Property<?, ? super T> property) {
            return new LazyArg<T>(wrapper, property);
        }

        String getValue(Session session) {
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
