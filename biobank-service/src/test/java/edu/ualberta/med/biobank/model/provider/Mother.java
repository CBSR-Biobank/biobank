package edu.ualberta.med.biobank.model.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a set of {@link EntityProvider}s for various entities.
 * 
 * @author Jonathan Ferland
 */
public class Mother {
    private String name;

    private final Map<Class<?>, EntityProvider<?>> providers =
        new HashMap<Class<?>, EntityProvider<?>>();

    public Mother() {
    }

    public <T> EntityProvider<T> getProvider(Class<T> klazz) {
        @SuppressWarnings("unchecked")
        EntityProvider<T> tmp = (EntityProvider<T>) providers.get(klazz);
        return tmp;
    }

    public <T> EntityProvider<T> bind(Class<T> klazz, EntityProvider<T> provider) {
        @SuppressWarnings("unchecked")
        EntityProvider<T> tmp = (EntityProvider<T>) providers.put(
            klazz, new TrackingEntityProvider<T>(provider));
        return tmp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class TrackingEntityProvider<T>
        implements EntityProvider<T> {

        private final EntityProvider<T> delegate;
        private T provided = null;

        public TrackingEntityProvider(EntityProvider<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            if (provided == null) provided = create();
            return provided;
        }

        @Override
        public void set(T provided) {
            delegate.set(provided);
        }

        @Override
        public T create() {
            T provided = delegate.create();
            delegate.save(provided);
            return provided;
        }

        @Override
        public EntityProvider<T> setProcessor(EntityProcessor<T> processor) {
            delegate.setProcessor(processor);
            return this;
        }

        @Override
        public T save(T object) {
            return null;
        }
    }
}
