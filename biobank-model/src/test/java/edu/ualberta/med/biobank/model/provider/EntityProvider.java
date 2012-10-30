package edu.ualberta.med.biobank.model.provider;

public interface EntityProvider<T> {
    /**
     * @return the last created object, or calls {@link EntityProvider#create()}
     *         and returns that if no object has been created yet.
     */
    T get();

    /**
     * @param provided what will be returned by the subsequent calls to
     *            {@link #get()}.
     */
    void set(T provided);

    /**
     * @return create a new object and return it.
     */
    T create();

    /**
     * @param processor sets an {@link EntityProcessor} that is called after an
     *            entity is created so that some general pattern can be applied
     *            or properties can be overridden.
     */
    EntityProvider<T> setProcessor(EntityProcessor<T> processor);
}
