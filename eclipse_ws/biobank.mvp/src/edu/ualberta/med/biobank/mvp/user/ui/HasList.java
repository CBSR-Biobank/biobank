package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.Collection;
import java.util.List;

public interface HasList<E> extends HasListChangeHandlers<E> {
    List<E> asUnmodifiableList();

    void setElements(Collection<? extends E> elements);

    void setElements(Collection<? extends E> elements, boolean fireEvents);
}
