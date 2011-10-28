package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;

// TODO: ideally, tables would use MVP too, but for now, do this
public class TableItem<T> extends ValidationItem<Collection<T>> implements
    HasValue<Collection<T>> {
    private AbstractInfoTableWidget<T> table;
    private List<T> list = new ArrayList<T>();
    private boolean fireEvents = true;

    public synchronized void setTable(AbstractInfoTableWidget<T> table) {
        this.table = table;

        // TODO: there are no events fired from an info table when its
        // collection is modified. We needs this.
    }

    @Override
    public Collection<T> getValue() {
        return table != null ? table.getList() : list;
    }

    @Override
    public void setValue(Collection<T> collection, boolean fireEvents) {
        list = new ArrayList<T>(collection);

        if (table != null) {
            this.fireEvents = fireEvents;
            table.setList(list);
            this.fireEvents = true;
        }
    }
}
