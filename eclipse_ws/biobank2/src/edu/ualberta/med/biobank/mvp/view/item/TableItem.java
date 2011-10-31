package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.util.ListChangeEvent;
import edu.ualberta.med.biobank.common.util.ListChangeHandler;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;

// TODO: ideally, tables would use MVP too, but for now, do this
public class TableItem<T> extends ValidationItem<Collection<T>> implements
    HasValue<Collection<T>> {
    private final ListChangeHandler<T> listChangeHandler = new ListChangeHandler<T>() {
        @Override
        public void onListChange(ListChangeEvent<T> event) {
            if (fireEvents) {
                Collection<T> value = getValue();
                fireEvent(new SimpleValueChangeEvent<Collection<T>>(value));
            }
        }
    };
    private AbstractInfoTableWidget<T> table;
    private List<T> list = new ArrayList<T>();
    private boolean fireEvents = true;

    public synchronized void setTable(AbstractInfoTableWidget<T> table) {
        unbindOldTable();

        this.table = table;
        setValue(list);
        table.addListChangeHandler(listChangeHandler);
    }

    @Override
    public Collection<T> getValue() {
        return table != null ? table.getList() : list;
    }

    @Override
    public void setValue(Collection<T> value, boolean fireEvents) {
        list = new ArrayList<T>(value);

        if (table != null) {
            this.fireEvents = fireEvents;
            table.setList(list);
            this.fireEvents = true;
        }
    }

    private void unbindOldTable() {
        if (table != null) {
            table.removeListChangeHandler(listChangeHandler);
        }
    }
}
