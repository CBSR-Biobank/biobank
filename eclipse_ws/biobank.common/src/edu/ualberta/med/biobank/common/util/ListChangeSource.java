package edu.ualberta.med.biobank.common.util;

public interface ListChangeSource<E> {
    void addListChangeHandler(ListChangeHandler<E> handler);

    void removeListChangeHandler(ListChangeHandler<E> handler);
}
