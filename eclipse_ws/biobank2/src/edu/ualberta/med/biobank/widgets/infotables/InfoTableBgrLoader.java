package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.Messages;

public abstract class InfoTableBgrLoader<T> extends AbstractInfoTableWidget<T> {

    private int size;

    private List<T> collection;

    public InfoTableBgrLoader(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, collection, headings, columnWidths, rowsPerPage);
    }

    /**
     * This method is used to load object model data in the background thread.
     * 
     * @param item the model object representing the base object to get
     *            information from.
     * @return an non-object model object with the table data.
     * 
     * @throws Exception
     */
    protected abstract void tableLoader(final List<T> collection,
        final T Selection);

    @Override
    public void setCollection(final List<T> collection) {
        setCollection(collection, null);
        if (collection != null) {
            size = collection.size();
        }
    }

    public void setCollection(final List<T> collection, final T selection) {
        try {
            if ((collection == null)
                || ((backgroundThread != null) && backgroundThread.isAlive())) {
                return;
            } else if ((this.collection != collection)
                || (size != collection.size())) {
                this.collection = collection;
                init(collection);
                setPaginationParams(collection);
            }
            if (paginationRequired) {
                showPaginationWidget();
                paginationWidget.setPageLabelText();
                enablePaginationWidget(false);
            } else if (paginationWidget != null) {
                paginationWidget.setVisible(false);
            }
            final Display display = getTableViewer().getTable().getDisplay();

            resizeTable();
            backgroundThread = new Thread() {
                @Override
                public void run() {
                    tableLoader(collection, selection);
                    if (autoSizeColumns) {
                        display.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                autoSizeColumns();
                            }
                        });
                    }
                }
            };
            backgroundThread.start();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.AbstractInfoTableWidget_load_error_title, e);
        }
    }

    public List<T> getCollection() {
        return collection;
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        // sorting not supported on background loading tables because
        // not all rows have been populated on the client side
        return null;
    }

    @Override
    public void firstPage() {
        setCollection(collection);
    }

    @Override
    public void lastPage() {
        setCollection(collection);
    }

    @Override
    public void prevPage() {
        setCollection(collection);
    }

    @Override
    public void nextPage() {
        setCollection(collection);
    }

}
