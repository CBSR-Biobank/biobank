package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.event.ui.ListChangeEvent;
import edu.ualberta.med.biobank.mvp.event.ui.ListChangeHandler;
import edu.ualberta.med.biobank.mvp.user.ui.HasListField;

public abstract class AbstractListField<E> extends AbstractValidationField
    implements HasListField<E> {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final List<E> list = new ArrayList<E>();
    private final List<E> unmodifiableList = Collections.unmodifiableList(list);

    @Override
    public List<E> asUnmodifiableList() {
        return unmodifiableList;
    }

    @Override
    public final void setElements(Collection<? extends E> elements) {
        setElements(elements, false);
    }

    @Override
    public void setElements(Collection<? extends E> elements, boolean fireEvents) {
        list.clear();
        list.addAll(elements);

        update();

        if (fireEvents) {
            fireEvent(new ListChangeEvent<E>(this));
        }
    }

    @Override
    public HandlerRegistration addListChangeHandler(ListChangeHandler<E> handler) {
        return handlerManager.addHandler(ListChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    /**
     * Update the GUI's value to match {@link #asUnmodifiableList()}. Do so
     * without firing any events.
     */
    protected abstract void update();
}
