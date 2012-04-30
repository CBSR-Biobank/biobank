package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.ListChangeEvent;
import edu.ualberta.med.biobank.common.util.ListChangeHandler;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;

/**
 * 
 * @author jferland
 * 
 * @param <T>
 */
// TODO: ideally, tables would use MVP too, but for now, do this
public class TableItem<E> extends AbstractListField<E> {
    private final ListChangeHandler<E> listChangeHandler =
        new ListChangeHandler<E>() {
            @Override
            public void onListChange(ListChangeEvent<E> event) {
                setElementsInternal(table.getList());
            }
        };
    private AbstractInfoTableWidget<E> table;

    public synchronized void setTable(AbstractInfoTableWidget<E> table) {
        unbindOldTable();

        this.table = table;
        updateGui();
        table.addListChangeHandler(listChangeHandler);
    }

    @Override
    protected void updateGui() {
        if (table != null) {
            table.removeListChangeHandler(listChangeHandler);

            List<E> copy = new ArrayList<E>(asUnmodifiableList());
            table.setList(copy);

            table.addListChangeHandler(listChangeHandler);
        }
    }

    private void unbindOldTable() {
        if (table != null) {
            table.removeListChangeHandler(listChangeHandler);
        }
    }
}
