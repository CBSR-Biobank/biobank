package edu.ualberta.med.biobank.mvp.view.item;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.widgets.infotables.InfoTableBgrLoader;

// TODO: ideally, tables would use MVP too, but for now, do this
public class TableItem<T extends List<?>> implements HasValue<T> {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private InfoTableBgrLoader table;

    public synchronized void setTable(InfoTableBgrLoader table) {
        this.table = table;
        // TODO: for now, this table doesn't really fire any ValueChangeEvent-s
        // because AbstractInfoTableWidget's listener system doesn't make any
        // sense. The addXXXItemTableListener methods need to be rewritten.
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public void setValue(T value) {
    }

    @Override
    public void setValue(T value, boolean fireEvents) {

    }
}
