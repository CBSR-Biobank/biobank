package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.Messages;

public abstract class InfoTableBgrLoader<T> extends AbstractInfoTableWidget<T> {
    protected Thread backgroundThread;

    private int size;

    public InfoTableBgrLoader(Composite parent, List<T> list,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, headings, columnWidths, rowsPerPage);
        setList(list);
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
    protected abstract void tableLoader(final List<T> list, final T Selection);

    @Override
    public void setList(List<T> collection) {
        setList(collection, null);
        if (collection != null) {
            size = collection.size();
        }
    }

    @Override
    public void setList(final List<T> collection, final T selection) {
        try {
            if ((collection == null)
                || ((backgroundThread != null) && backgroundThread.isAlive())) {
                return;
            } else if ((this.list != collection)
                || (size != collection.size())) {
                this.list = collection;
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

    protected abstract void init(List<T> list);

    protected abstract void setPaginationParams(List<T> list);

    @Override
    protected BgcTableSorter getTableSorter() {
        // sorting not supported on background loading tables because
        // not all rows have been populated on the client side
        return null;
    }

    @Override
    public void firstPage() {
        setList(list);
    }

    @Override
    public void lastPage() {
        setList(list);
    }

    @Override
    public void prevPage() {
        setList(list);
    }

    @Override
    public void nextPage() {
        setList(list);
    }

    @Override
    public void reload() {
        setList(list);
    }

}
