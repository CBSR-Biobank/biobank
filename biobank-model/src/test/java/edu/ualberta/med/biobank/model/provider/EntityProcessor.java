package edu.ualberta.med.biobank.model.provider;

public interface EntityProcessor<T> {
    void process(T entity);
}
