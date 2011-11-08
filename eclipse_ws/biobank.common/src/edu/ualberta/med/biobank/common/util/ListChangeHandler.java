package edu.ualberta.med.biobank.common.util;

public interface ListChangeHandler<E> {
    void onListChange(ListChangeEvent<E> event);
}
