package edu.ualberta.med.biobank.model.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a set of {@link EntityProvider}s for various entities.
 * 
 * @author Jonathan Ferland
 */
public class Mother {
    private final String name;
    private final Map<Class<?>, EntityProvider<?>> providers =
        new HashMap<Class<?>, EntityProvider<?>>();

    public Mother(String name) {
        this.name = name;
    }

    public <T> EntityProvider<T> getProvider(Class<T> klazz) {
        @SuppressWarnings("unchecked")
        EntityProvider<T> tmp = (EntityProvider<T>) providers.get(klazz);
        return tmp;
    }

    public String getName() {
        return name;
    }
}
