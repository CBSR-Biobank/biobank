package edu.ualberta.med.biobank.mvp.event.ui;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.mvp.user.ui.HasList;

public class ListChangeEvent<E> extends GwtEvent<ListChangeHandler<E>> {
    private final HasList<E> list;

    /**
     * Handler type.
     */
    private static Type<ListChangeHandler<?>> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ListChangeHandler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<ListChangeHandler<?>>();
        }
        return TYPE;
    }

    public ListChangeEvent(HasList<E> list) {
        this.list = list;
    }

    public HasList<E> getList() {
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Type<ListChangeHandler<E>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(ListChangeHandler<E> handler) {
        handler.onListChange(this);
    }
}
