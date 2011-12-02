package edu.ualberta.med.biobank.common.util;

import java.util.ArrayList;
import java.util.List;

public class AbstractListChangeSource<E> implements ListChangeSource<E> {
    private final List<ListChangeHandler<E>> handlers =
        new ArrayList<ListChangeHandler<E>>();

    @Override
    public void addListChangeHandler(ListChangeHandler<E> handler) {
        handlers.add(handler);
    }

    @Override
    public void removeListChangeHandler(ListChangeHandler<E> handler) {
        handlers.remove(handler);
    }

    protected void fireListChangeEvent() {
        ListChangeEvent<E> event = new ListChangeEvent<E>();
        List<ListChangeHandler<E>> handlersCopy =
            new ArrayList<ListChangeHandler<E>>(handlers);
        for (ListChangeHandler<E> handler : handlersCopy) {
            handler.onListChange(event);
        }
    }
}
