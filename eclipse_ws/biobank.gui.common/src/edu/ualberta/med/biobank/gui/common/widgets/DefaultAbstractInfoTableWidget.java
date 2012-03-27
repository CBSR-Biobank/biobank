package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.ListChangeEvent;
import edu.ualberta.med.biobank.common.util.ListChangeHandler;

public abstract class DefaultAbstractInfoTableWidget<T>
    extends AbstractInfoTableWidget<T> {
    private final InfoTableListChangeHandler infoTableListChangeHandler =
        new InfoTableListChangeHandler();

    public DefaultAbstractInfoTableWidget(Composite parent, String[] headings,
        int rowsPerPage) {
        super(parent, headings, null, rowsPerPage);

        addListChangeHandler(infoTableListChangeHandler);
    }

    public void setSelection(T selection) {
        if (selection != null) {
            tableViewer.setSelection(new StructuredSelection(selection));
        }
    }

    protected void setPaginationParams(List<T> collection) {
        paginationRequired = paginationWidget
            .setTableMaxRows(collection.size());
        if (paginationRequired) {
            getTableViewer().refresh();
        }
    }

    @Override
    public void setList(List<T> list) {
        super.setList(list);

        resizeTable();

        if (paginationRequired) {
            showPaginationWidget();
            paginationWidget.setPageLabelText();
            enablePaginationWidget(false);

            int rowsPerPage = paginationWidget.getRowsPerPage();
            start = paginationWidget.getCurrentPage() * rowsPerPage;
            end = Math.min(start + rowsPerPage, getList().size());

            enablePaginationWidget(true);
        } else {
            start = 0;
            end = getList().size();

            if (paginationWidget != null) {
                paginationWidget.setVisible(false);
            }
        }

        final List<T> subList = getList().subList(start, end);
        getTableViewer().setInput(subList);

        if (autoSizeColumns) {
            autoSizeColumns();
        }

        // Table table = tableViewer.getTable();
        // for (int i = 0, n = table.getColumnCount(); i < n; i++) {
        // table.getColumn(i).pack();
        // }

        System.out.println("2");
    }

    @Override
    public void setCollection(final Collection<T> list) {
        setList(new ArrayList<T>(list));
    }

    @Override
    public void firstPage() {
        setList(getList());
    }

    @Override
    public void prevPage() {
        setList(getList());
    }

    @Override
    public void nextPage() {
        setList(getList());
    }

    @Override
    public void lastPage() {
        setList(getList());
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    @Override
    public void reload() {
        setList(getList());
    }

    @Override
    public boolean isEditMode() {
        return false;
    }

    @Override
    public T getSelection() {
        if (tableViewer.getTable().isDisposed()) return null;

        IStructuredSelection selection = (IStructuredSelection) tableViewer
            .getSelection();

        @SuppressWarnings("unchecked")
        T firstElement = (T) selection.getFirstElement();
        return firstElement;
    }

    private class InfoTableListChangeHandler implements ListChangeHandler<T> {
        private boolean ignoreEvents = false;

        @Override
        public void onListChange(ListChangeEvent<T> event) {
            // init() may cause ListChangeEvent-s to be fired, so don't listen
            // for them when init() is called.
            if (!ignoreEvents) {
                try {
                    ignoreEvents = true;
                    List<T> list = getList();
                    setPaginationParams(list);
                } finally {
                    ignoreEvents = false;
                }
            }
        }
    }
}
