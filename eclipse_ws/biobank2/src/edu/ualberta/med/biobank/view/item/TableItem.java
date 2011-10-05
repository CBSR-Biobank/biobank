package edu.ualberta.med.biobank.view.item;

import java.util.List;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeHandler;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableBgrLoader;

public class TableItem<T extends List<?>> implements HasValue<T> {
    private final InfoTableBgrLoader table;

    public TableItem(InfoTableBgrLoader table) {
        this.table = table;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T tmp = (T) table.getCollection();
        return tmp;
    }

    @Override
    public void setValue(T value) {
        table.setCollection(value);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        // TODO Auto-generated method stub

    }
}
