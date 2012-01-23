package edu.ualberta.med.biobank.common.action;

import java.util.Collection;
import java.util.HashSet;

import edu.ualberta.med.biobank.common.wrappers.Property;

@Deprecated
public class CollectionUtils {

    public static <T, R> Collection<T> getCollection(R model,
        Property<Collection<T>, R> property) {
        Collection<T> collection = property.get(model);
        if (collection == null) {
            collection = new HashSet<T>();
            property.set(model, collection);
        }
        return collection;
    }
}
