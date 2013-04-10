package edu.ualberta.med.biobank.model.provider;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;

public abstract class AbstractProvider<T extends VersionedLongIdModel>
    implements EntityProvider<T> {

    protected final Mother mother;

    private T provided;
    private EntityProcessor<T> processor;

    protected AbstractProvider(Mother mother) {
        this.mother = mother;
    }

    @Override
    public T get() {
        if (provided == null) provided = create();
        return provided;
    }

    @Override
    public void set(T provided) {
        this.provided = provided;
    }

    @Override
    public final T create() {
        T created = onCreate();
        if (processor != null) processor.process(created);
        return created;
    }

    @Override
    public EntityProvider<T> setProcessor(EntityProcessor<T> processor) {
        this.processor = processor;
        return this;
    }

    protected abstract T onCreate();
}
