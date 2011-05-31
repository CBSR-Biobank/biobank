package edu.ualberta.med.biobank.common.wrappers.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class Check {
    public static <E> CheckUnique<E> unique(ModelWrapper<E> modelWrapper,
        Property<?, E> property) {

        Collection<Property<?, E>> properties = new ArrayList<Property<?, E>>();
        properties.add(property);

        return new CheckUnique<E>(modelWrapper, properties);
    }

    public static <E> CheckUnique<E> unique(ModelWrapper<E> modelWrapper,
        Collection<Property<?, E>> properties) {

        // make our own copy that is not exposed
        properties = new ArrayList<Property<?, E>>(properties);

        return new CheckUnique<E>(modelWrapper, properties);
    }

    public static <E> CheckNotNull<E> notNull(ModelWrapper<E> wrapper,
        Property<?, E> property) {
        // TODO: check on the client and on the server?
        return new CheckNotNull<E>(wrapper, property);
    }

    public static <E> CheckCollectionIsEmpty<E> collectionIsEmpty(
        ModelWrapper<E> wrapper, Property<? extends Collection<?>, E> property) {
        return new CheckCollectionIsEmpty<E>(wrapper, property);
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
