package edu.ualberta.med.biobank.model.provider;

public abstract class AbstractProvider<T>
    implements EntityProvider<T> {

    private T provided;
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
}
