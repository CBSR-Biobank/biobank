package edu.ualberta.med.biobank.common.wrappers.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class Check {
    public static <E> CheckUnique<E> unique(ModelWrapper<E> modelWrapper,
        Property<?, ? super E> property) {

        Collection<Property<?, ? super E>> properties = new ArrayList<Property<?, ? super E>>();
        properties.add(property);

        return new CheckUnique<E>(modelWrapper, properties);
    }

    public static <E> CheckUnique<E> unique(ModelWrapper<E> modelWrapper,
        Collection<Property<?, ? super E>> properties) {

        // make our own copy that is not exposed
        properties = new ArrayList<Property<?, ? super E>>(properties);

        return new CheckUnique<E>(modelWrapper, properties);
    }

    public static <E> CheckNotNull<E> notNull(ModelWrapper<E> wrapper,
        Property<?, ? super E> property) {
        // TODO: check on the client and on the server?
        return new CheckNotNull<E>(wrapper, property);
    }

    public static <E> CheckCollectionIsEmpty<E> empty(ModelWrapper<E> wrapper,
        Property<? extends Collection<?>, ? super E> property) {
        return new CheckCollectionIsEmpty<E>(wrapper, property);
    }

    public static <E> CheckCollectionIsEmpty<E> empty(ModelWrapper<E> wrapper,
        Property<? extends Collection<?>, ? super E> property, String msg) {
        return new CheckCollectionIsEmpty<E>(wrapper, property, msg);
    }

    public static Long getCountFromResult(List<?> results) {
        Long count = null;
        if (results != null && results.size() == 1
            && (results.get(0) instanceof Number)) {
            count = new Long(((Number) results.get(0)).longValue());
        }
        return count;
    }
}
