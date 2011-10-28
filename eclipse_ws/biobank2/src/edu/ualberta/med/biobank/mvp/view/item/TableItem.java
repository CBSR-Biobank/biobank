package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.widgets.infotables.InfoTableBgrLoader;

// TODO: ideally, tables would use MVP too, but for now, do this
public class TableItem<T> extends ValidationItem<Collection<T>> implements
    HasValue<Collection<T>> {
    private InfoTableBgrLoader table;
    private List<T> value = new ArrayList<T>();
    private boolean fireEvents = true;

    public synchronized void setTable(InfoTableBgrLoader table) {
        this.table = table;

        // TODO: there are no events fired from an info table when its
        // collection is modified. We needs this.
    }

    @Override
    public Collection<T> getValue() {
        if (table == null) return value;

        @SuppressWarnings("unchecked")
        Collection<T> collection = (Collection<T>) table.getCollection();
        return collection;
    }

    @Override
    public void setValue(Collection<T> collection, boolean fireEvents) {
        value = new ArrayList<T>(collection);

        if (table != null) {
            this.fireEvents = fireEvents;
            table.setCollection(value);
            this.fireEvents = true;
        }
    }
}
