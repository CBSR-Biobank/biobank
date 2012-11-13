package edu.ualberta.med.biobank.model.provider;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.study.CollectionEventType;
import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.model.study.Study;

/**
 * Manages a set of {@link EntityProvider}s for various entities.
 * 
 * @author Jonathan Ferland
 */
public class Mother {
    private final String name;
    private final Map<Class<?>, EntityProvider<?>> providers =
        new HashMap<Class<?>, EntityProvider<?>>();
    private EntityProcessor<Object> processor;

    public Mother(String name) {
        this.name = name;

        bind(CollectionEvent.class, new CollectionEventProvider(this));
        bind(CollectionEventType.class, new CollectionEventTypeProvider(this));
        bind(Patient.class, new PatientProvider(this));
        bind(Study.class, new StudyProvider(this));
    }

    public <T> EntityProvider<T> getProvider(Class<T> klazz) {
        @SuppressWarnings("unchecked")
        EntityProvider<T> tmp = (EntityProvider<T>) providers.get(klazz);
        return tmp;
    }

    public <T> EntityProvider<T> bind(Class<T> klazz, EntityProvider<T> provider) {
        @SuppressWarnings("unchecked")
        EntityProvider<T> tmp = (EntityProvider<T>) providers.put(
            klazz,
            new TrackingEntityProvider<T>(provider));
        return tmp;
    }

    public String getName() {
        return name;
    }

    public void setEntityProcessor(EntityProcessor<Object> processor) {
        this.processor = processor;
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
            if (processor != null) processor.process(provided);
            return provided;
        }

        @Override
        public EntityProvider<T> setProcessor(EntityProcessor<T> processor) {
            delegate.setProcessor(processor);
            return this;
        }
    }
}
