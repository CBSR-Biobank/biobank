package edu.ualberta.med.biobank.model.provider;

public abstract class AbstractProvider<T>
    implements EntityProvider<T> {

    private T provided;
    private EntityProcessor<T> processor;
    protected final Mother mother;

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
